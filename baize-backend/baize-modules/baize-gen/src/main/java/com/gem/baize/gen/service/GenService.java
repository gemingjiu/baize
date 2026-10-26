package com.gem.baize.gen.service;

import com.gem.baize.gen.domain.dto.GenTableDTO;
import com.gem.baize.gen.domain.vo.GenTableColumnVO;
import com.gem.baize.gen.domain.vo.GenTableVO;
import com.gem.baize.gen.util.GenUtils;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 代码生成服务。
 *
 * <p><b>模板方案：FreeMarker（不是字符串拼接）。</b>模块 pom 已引入
 * {@code spring-boot-starter-freemarker}，模板放在 {@code classpath:/templates/} 下，
 * 见 {@code src/main/resources/templates/java/*.java.ftl} 与 {@code templates/ui/*.ftl}。
 *
 * <p><b>生成物（一次生成 8 个文件）：</b>
 * <ol>
 *   <li>Java：Entity / DTO / Convert / Mapper / Service / ServiceImpl / Controller（7 个）</li>
 *   <li>前端：service.ts + index.tsx（2 个）</li>
 * </ol>
 * 风格照抄 baize-system 的 post（岗位）模块：Entity 继承 BaseEntity、DTO 带 id/tenantId、
 * ServiceImpl 走逻辑删除（deleted='1' + status='1'）、Controller 用 PageConvert 返回
 * ApiResult&lt;PageResult&lt;Dto&gt;&gt; 并挂 @RequiresPermission("{module}:{business}:*")。
 *
 * <p><b>两种交付形态：</b>
 * <ul>
 *   <li>{@link #generateCode(GenTableDTO)} —— 返回「文件路径 → 源码」的 Map，供前端预览；</li>
 *   <li>{@link #generateZip(GenTableDTO)} —— 把上一步的结果打成 zip 字节流下载。</li>
 * </ul>
 */
@Slf4j
@Service
public class GenService {

    /** Entity 模板 */
    private static final String TPL_ENTITY = "java/entity.java.ftl";
    private static final String TPL_DTO = "java/dto.java.ftl";
    private static final String TPL_CONVERT = "java/convert.java.ftl";
    private static final String TPL_MAPPER = "java/mapper.java.ftl";
    private static final String TPL_SERVICE = "java/service.java.ftl";
    private static final String TPL_SERVICE_IMPL = "java/serviceImpl.java.ftl";
    private static final String TPL_CONTROLLER = "java/controller.java.ftl";
    private static final String TPL_UI_SERVICE = "ui/service.ts.ftl";
    private static final String TPL_UI_INDEX = "ui/index.tsx.ftl";

    /** Controller 上的 @RequiresPermission 前缀分隔符 */
    private static final String DEFAULT_PACKAGE = "com.gem.baize.system";

    @Autowired
    private Configuration freeMarkerConfig;

    @Autowired
    private DataSource dataSource;

    /**
     * 获取数据库所有表
     */
    public List<Map<String, Object>> getTableList() {
        List<Map<String, Object>> tables = new ArrayList<>();
        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            String catalog = conn.getCatalog();
            String schema = conn.getSchema();
            ResultSet rs = metaData.getTables(catalog, schema, "%", new String[]{"TABLE"});
            while (rs.next()) {
                Map<String, Object> table = new HashMap<>();
                table.put("tableName", rs.getString("TABLE_NAME"));
                table.put("tableComment", rs.getString("REMARKS"));
                table.put("tableType", rs.getString("TABLE_TYPE"));
                tables.add(table);
            }
            rs.close();
        } catch (SQLException e) {
            log.error("获取表列表失败", e);
        }
        return tables;
    }

    /**
     * 获取表字段信息
     */
    public GenTableVO getTableInfo(String tableName) {
        GenTableVO tableVO = new GenTableVO();
        tableVO.setTableName(tableName);

        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            String catalog = conn.getCatalog();
            String schema = conn.getSchema();

            // 获取表注释
            ResultSet tableRs = metaData.getTables(catalog, schema, tableName, new String[]{"TABLE"});
            if (tableRs.next()) {
                tableVO.setTableComment(tableRs.getString("REMARKS"));
            }

            // 获取主键
            Set<String> primaryKeys = new HashSet<>();
            ResultSet pkRs = metaData.getPrimaryKeys(catalog, schema, tableName);
            while (pkRs.next()) {
                primaryKeys.add(pkRs.getString("COLUMN_NAME"));
            }

            // 获取列信息
            List<GenTableColumnVO> columns = new ArrayList<>();
            ResultSet rs = metaData.getColumns(catalog, schema, tableName, "%");
            while (rs.next()) {
                GenTableColumnVO column = new GenTableColumnVO();
                String columnName = rs.getString("COLUMN_NAME");
                column.setColumnName(columnName);
                column.setColumnComment(rs.getString("REMARKS"));
                column.setColumnType(rs.getString("TYPE_NAME"));
                column.setJavaField(GenUtils.columnToJavaField(columnName));
                column.setJavaType(GenUtils.getJavaType(rs.getString("TYPE_NAME")));
                column.setPrimaryKey(primaryKeys.contains(columnName));
                column.setInsert(true);
                column.setEdit(true);
                column.setList(true);
                column.setQuery(false);
                columns.add(column);
            }
            tableVO.setColumns(columns);

            // 前端只传 tableName 时（预览场景），后端补全默认命名，避免模板里取到 null
            if (!StringUtils.hasText(tableVO.getClassName())) {
                tableVO.setClassName(GenUtils.tableToClassName(tableName, null));
            }
            if (!StringUtils.hasText(tableVO.getClassNameLower())) {
                tableVO.setClassNameLower(GenUtils.uncapitalize(tableVO.getClassName()));
            }
            if (!StringUtils.hasText(tableVO.getTableComment())) {
                tableVO.setTableComment(tableName);
            }
            if (!StringUtils.hasText(tableVO.getPackageName())) {
                tableVO.setPackageName(DEFAULT_PACKAGE);
            }
            if (!StringUtils.hasText(tableVO.getModuleName())) {
                tableVO.setModuleName("system");
            }
            if (!StringUtils.hasText(tableVO.getBusinessName())) {
                tableVO.setBusinessName(tableVO.getClassNameLower());
            }
            if (!StringUtils.hasText(tableVO.getFunctionName())) {
                tableVO.setFunctionName(tableVO.getTableComment());
            }
            if (!StringUtils.hasText(tableVO.getAuthor())) {
                tableVO.setAuthor("baize");
            }
        } catch (SQLException e) {
            log.error("获取表信息失败", e);
        }
        return tableVO;
    }

    /**
     * 生成代码（预览用）。
     *
     * @param dto 生成参数；className / columns 为空时自动从数据库元数据补齐
     * @return 「相对文件路径 → 源码内容」的有序 Map（LinkedHashMap，保持展示顺序）
     */
    public Map<String, String> generateCode(GenTableDTO dto) {
        Map<String, String> result = new LinkedHashMap<>();
        try {
            GenTableVO tableVO = buildTableVO(dto);
            // 模板里统一写 ${dto.xxx}（含 packageName / className / moduleName 等），
            // 但前端最小闭环只传 tableName，这里把 buildTableVO 兜底出来的值回填到 DTO，
            // 避免 FreeMarker 取到 null 直接抛 InvalidReferenceException。
            applyResolvedValues(dto, tableVO);

            Map<String, Object> model = new HashMap<>();
            model.put("table", tableVO);
            model.put("dto", dto);

            String pkgPath = tableVO.getPackageName().replace('.', '/');
            String cls = tableVO.getClassName();
            String module = tableVO.getModuleName();
            String business = tableVO.getBusinessName();

            result.put(pkgPath + "/entity/" + cls + ".java", processTemplate(TPL_ENTITY, model));
            result.put(pkgPath + "/domain/dto/" + cls + "Dto.java", processTemplate(TPL_DTO, model));
            result.put(pkgPath + "/convert/" + cls + "Convert.java", processTemplate(TPL_CONVERT, model));
            result.put(pkgPath + "/mapper/" + cls + "Mapper.java", processTemplate(TPL_MAPPER, model));
            result.put(pkgPath + "/service/" + cls + "Service.java", processTemplate(TPL_SERVICE, model));
            result.put(pkgPath + "/service/impl/" + cls + "ServiceImpl.java", processTemplate(TPL_SERVICE_IMPL, model));
            result.put(pkgPath + "/controller/" + cls + "Controller.java", processTemplate(TPL_CONTROLLER, model));
            result.put("ui/src/services/" + module + "/" + business + ".ts", processTemplate(TPL_UI_SERVICE, model));
            result.put("ui/src/pages/" + module + "/" + business + "/index.tsx", processTemplate(TPL_UI_INDEX, model));
        } catch (Exception e) {
            log.error("生成代码失败", e);
        }
        return result;
    }

    /**
     * 生成代码并打包成 zip。
     *
     * @param dto 生成参数
     * @return zip 字节流；生成异常时返回空数组（由 Controller 转成错误响应）
     */
    public byte[] generateZip(GenTableDTO dto) {
        Map<String, String> files = generateCode(dto);
        if (files.isEmpty()) {
            return new byte[0];
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(bos)) {
            for (Map.Entry<String, String> entry : files.entrySet()) {
                zos.putNextEntry(new ZipEntry(entry.getKey()));
                byte[] bytes = entry.getValue().getBytes(StandardCharsets.UTF_8);
                zos.write(bytes);
                zos.closeEntry();
            }
        } catch (IOException e) {
            log.error("打包 zip 失败", e);
            return new byte[0];
        }
        return bos.toByteArray();
    }

    /**
     * 构建 GenTableVO。
     * <p>
     * 前端「一键生成」通常只给 tableName + moduleName + businessName，列信息为空；
     * 这里回落到 {@link #getTableInfo(String)} 从 JDBC 元数据读全表列，
     * 保证 {@code POST /gen/generate} 单参数也能出完整代码。
     */
    private GenTableVO buildTableVO(GenTableDTO dto) {
        GenTableVO vo = new GenTableVO();
        vo.setTableName(dto.getTableName());
        if (!StringUtils.hasText(dto.getClassName())) {
            vo.setClassName(GenUtils.tableToClassName(dto.getTableName(), null));
        } else {
            vo.setClassName(dto.getClassName());
        }
        vo.setClassNameLower(GenUtils.uncapitalize(vo.getClassName()));
        vo.setTableComment(StringUtils.hasText(dto.getTableComment()) ? dto.getTableComment() : dto.getTableName());
        vo.setModuleName(StringUtils.hasText(dto.getModuleName()) ? dto.getModuleName() : "system");
        vo.setBusinessName(StringUtils.hasText(dto.getBusinessName()) ? dto.getBusinessName() : vo.getClassNameLower());
        vo.setFunctionName(StringUtils.hasText(dto.getFunctionName()) ? dto.getFunctionName() : vo.getTableComment());
        vo.setAuthor(StringUtils.hasText(dto.getAuthor()) ? dto.getAuthor() : "baize");
        vo.setPackageName(StringUtils.hasText(dto.getPackageName()) ? dto.getPackageName() : DEFAULT_PACKAGE);

        if (dto.getColumns() != null && !dto.getColumns().isEmpty()) {
            vo.setColumns(dto.getColumns().stream().map(c -> {
                GenTableColumnVO columnVO = new GenTableColumnVO();
                columnVO.setColumnName(c.getColumnName());
                columnVO.setColumnComment(c.getColumnComment());
                columnVO.setColumnType(c.getColumnType());
                columnVO.setJavaField(c.getJavaField());
                columnVO.setJavaType(c.getJavaType());
                columnVO.setPrimaryKey(c.isPrimaryKey());
                columnVO.setInsert(c.isInsert());
                columnVO.setEdit(c.isEdit());
                columnVO.setList(c.isList());
                columnVO.setQuery(c.isQuery());
                return columnVO;
            }).toList());
        } else {
            // 列信息缺失：直接查库，前端只选表名也要能生成
            vo.setColumns(getTableInfo(dto.getTableName()).getColumns());
        }
        return vo;
    }

    /**
     * 把 buildTableVO 兜底出来的命名回填到入参 DTO。
     * <p>
     * 模板（entity/dto/convert/mapper/service/serviceImpl/controller/ui/*）里统一用
     * {@code ${dto.className}} / {@code ${dto.packageName}} 等取值，而前端「选表即生成」
     * 只传 tableName；若不回填，FreeMarker 会因取到 null 抛 InvalidReferenceException。
     * 回填只补空值，不覆盖调用方显式传入的参数。
     */
    private void applyResolvedValues(GenTableDTO dto, GenTableVO vo) {
        if (!StringUtils.hasText(dto.getClassName())) {
            dto.setClassName(vo.getClassName());
        }
        if (!StringUtils.hasText(dto.getTableComment())) {
            dto.setTableComment(vo.getTableComment());
        }
        if (!StringUtils.hasText(dto.getModuleName())) {
            dto.setModuleName(vo.getModuleName());
        }
        if (!StringUtils.hasText(dto.getBusinessName())) {
            dto.setBusinessName(vo.getBusinessName());
        }
        if (!StringUtils.hasText(dto.getFunctionName())) {
            dto.setFunctionName(vo.getFunctionName());
        }
        if (!StringUtils.hasText(dto.getAuthor())) {
            dto.setAuthor(vo.getAuthor());
        }
        if (!StringUtils.hasText(dto.getPackageName())) {
            dto.setPackageName(vo.getPackageName());
        }
    }

    private String processTemplate(String templateName, Map<String, Object> model) throws IOException, TemplateException {
        Template template = freeMarkerConfig.getTemplate(templateName, "UTF-8");
        try (StringWriter writer = new StringWriter()) {
            template.process(model, writer);
            return writer.toString();
        }
    }
}
