package com.gem.baize.gen.controller;

import com.gem.baize.common.core.annotation.RequiresPermission;
import com.gem.baize.common.core.model.vo.ApiResult;
import com.gem.baize.gen.domain.dto.GenTableDTO;
import com.gem.baize.gen.domain.vo.GenTableVO;
import com.gem.baize.gen.service.GenService;
import com.gem.baize.gen.util.GenUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * 代码生成Controller。
 *
 * <p>网关路由 {@code /api/v1/gen/** -> lb://baize-gen}，权限码沿用菜单里已有的
 * {@code tool:gen:query} / {@code tool:gen:code}。
 */
@Tag(name = "代码生成器")
@RestController
@RequestMapping("/gen")
public class GenController {

    @Autowired
    private GenService genService;

    @GetMapping("/tables")
    @Operation(summary = "获取数据库表列表")
    @RequiresPermission("tool:gen:query")
    public ApiResult<List<Map<String, Object>>> tables() {
        return ApiResult.ok(genService.getTableList());
    }

    @GetMapping("/table/{tableName}")
    @Operation(summary = "获取表字段信息")
    @RequiresPermission("tool:gen:query")
    public ApiResult<GenTableVO> tableInfo(@PathVariable String tableName) {
        GenTableVO tableInfo = genService.getTableInfo(tableName);
        // 自动推断类名
        if (!StringUtils.hasText(tableInfo.getClassName())) {
            tableInfo.setClassName(GenUtils.tableToClassName(tableName, null));
        }
        return ApiResult.ok(tableInfo);
    }

    /**
     * 生成代码（预览）。
     * <p>
     * 返回「相对文件路径 → 源码」的 Map，前端可直接在弹窗里分文件查看；
     * 需要落盘时用 {@link #download(GenTableDTO, HttpServletResponse)} 拿 zip。
     * <p>
     * 最小闭环下前端只需传 tableName（moduleName / businessName / columns 均可省略，
     * 由 GenService 回落默认值并从 JDBC 元数据补齐列信息）。
     */
    @PostMapping("/generate")
    @Operation(summary = "生成代码（返回文件内容）")
    @RequiresPermission("tool:gen:code")
    public ApiResult<Map<String, String>> generate(@RequestBody GenTableDTO dto) {
        return ApiResult.ok(genService.generateCode(dto));
    }

    /**
     * 生成代码并打包下载（zip）。
     */
    @PostMapping("/download")
    @Operation(summary = "生成代码并下载 zip")
    @RequiresPermission("tool:gen:code")
    public void download(@RequestBody GenTableDTO dto, HttpServletResponse response) throws IOException {
        byte[] zip = genService.generateZip(dto);
        if (zip.length == 0) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"success\":false,\"message\":\"生成代码失败，请查看服务端日志\"}");
            return;
        }
        String filename = StringUtils.hasText(dto.getTableName()) ? dto.getTableName() : "baize-gen";
        response.setContentType("application/zip");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setHeader("Content-Disposition",
                "attachment; filename=" + URLEncoder.encode(filename + ".zip", StandardCharsets.UTF_8));
        response.setContentLength(zip.length);
        try (OutputStream os = response.getOutputStream()) {
            os.write(zip);
            os.flush();
        }
    }
}
