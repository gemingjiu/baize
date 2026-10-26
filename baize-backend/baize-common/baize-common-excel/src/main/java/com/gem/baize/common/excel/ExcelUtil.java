package com.gem.baize.common.excel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 通用 Excel 导出工具（基于 EasyExcel）。
 *
 * <p>设计取舍：这里刻意使用「表头 List&lt;String&gt; + 数据行 List&lt;List&lt;Object&gt;&gt;」的通用形态，
 * 而不是要求每个导出对象都打 {@code @ExcelProperty} 注解。原因是本项目的对外模型是
 * baize-api 里的 DTO（跨服务共享），在 DTO 上耦合 EasyExcel 注解会把导出关注点泄漏到 API 包，
 * 也会让 baize-api 被迫依赖 excel 模块。由各 Controller 在导出时声明表头与取列的方式，
 * 可以让 DTO 保持纯净，也便于导出列随权限/租户动态增减。
 */
@Slf4j
public final class ExcelUtil {

    private ExcelUtil() {
    }

    /**
     * 导出为 xlsx 并写入 HTTP 响应。
     *
     * @param response  HTTP 响应
     * @param fileName  下载文件名（不含扩展名），中文会按 UTF-8 编码
     * @param sheetName sheet 名称
     * @param head      表头
     * @param data      数据行，每行元素个数应与表头一致（不足补空串，超出忽略）
     */
    public static void export(HttpServletResponse response, String fileName, String sheetName,
                              List<String> head, List<List<Object>> data) {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        String encodedName = urlEncode(fileName);
        response.setHeader("Content-Disposition", "attachment;filename*=utf-8''" + encodedName + ".xlsx");
        // 暴露给前端，便于 axios 从响应头取文件名
        response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");

        try (OutputStream os = response.getOutputStream()) {
            write(os, sheetName, head, data);
        } catch (IOException e) {
            log.error("导出Excel失败, fileName={}", fileName, e);
            throw new IllegalStateException("导出Excel失败", e);
        }
    }

    /**
     * 写出到任意输出流（便于单元测试与生成临时文件）。
     */
    public static void write(OutputStream os, String sheetName, List<String> head, List<List<Object>> data) {
        List<List<String>> headList = new ArrayList<>();
        for (String h : head) {
            headList.add(new ArrayList<>(java.util.Collections.singletonList(h)));
        }
        EasyExcel.write(os)
                .head(headList)
                // 列宽按内容自适应，避免中文列过窄
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .sheet(sheetName)
                .doWrite(nullToEmpty(data, head.size()));
    }

    /**
     * 便捷方法：按表头顺序从 Map 中取值组装数据行。
     */
    public static List<List<Object>> buildRows(Collection<? extends Map<String, Object>> rows, List<String> head) {
        List<List<Object>> data = new ArrayList<>(rows.size());
        for (Map<String, Object> row : rows) {
            List<Object> line = new ArrayList<>(head.size());
            for (String h : head) {
                Object v = row.get(h);
                line.add(v == null ? "" : v);
            }
            data.add(line);
        }
        return data;
    }

    /**
     * 便捷方法：把有序表头描述（列名 -> 取值函数）套用到一批对象上。
     */
    public static <T> List<List<Object>> buildRows(Collection<T> rows,
                                                   LinkedHashMap<String, java.util.function.Function<T, Object>> columns) {
        List<String> head = new ArrayList<>(columns.keySet());
        List<List<Object>> data = new ArrayList<>(rows.size());
        for (T row : rows) {
            List<Object> line = new ArrayList<>(head.size());
            for (java.util.function.Function<T, Object> getter : columns.values()) {
                Object v = getter.apply(row);
                line.add(v == null ? "" : v);
            }
            data.add(line);
        }
        return data;
    }

    /**
     * 快速构造表头。
     */
    public static List<String> head(String... names) {
        return new ArrayList<>(Arrays.asList(names));
    }

    private static List<List<Object>> nullToEmpty(List<List<Object>> data, int width) {
        if (data == null) {
            return new ArrayList<>();
        }
        List<List<Object>> out = new ArrayList<>(data.size());
        for (List<Object> row : data) {
            List<Object> line = new ArrayList<>(width);
            for (int i = 0; i < width; i++) {
                Object v = row != null && i < row.size() ? row.get(i) : null;
                line.add(v == null ? "" : v);
            }
            out.add(line);
        }
        return out;
    }

    private static String urlEncode(String fileName) {
        try {
            return URLEncoder.encode(fileName, StandardCharsets.UTF_8.name()).replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException e) {
            // UTF-8 必然存在，不可能走到这里
            return fileName;
        }
    }
}
