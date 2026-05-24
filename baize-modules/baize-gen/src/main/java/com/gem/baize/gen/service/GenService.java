package com.gem.baize.gen.service;

import com.gem.baize.gen.domain.dto.GenTableDTO;
import com.gem.baize.gen.domain.vo.GenTableColumnVO;
import com.gem.baize.gen.domain.vo.GenTableVO;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.*;
import java.util.*;

/**
 * 代码生成服务
 */
@Slf4j
@Service
public class GenService {

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
                column.setJavaField(com.gem.baize.gen.util.GenUtils.columnToJavaField(columnName));
                column.setJavaType(com.gem.baize.gen.util.GenUtils.getJavaType(rs.getString("TYPE_NAME")));
                column.setPrimaryKey(primaryKeys.contains(columnName));
                column.setInsert(true);
                column.setEdit(true);
                column.setList(true);
                column.setQuery(false);
                columns.add(column);
            }
            tableVO.setColumns(columns);

        } catch (SQLException e) {
            log.error("获取表信息失败", e);
        }
        return tableVO;
    }

    /**
     * 生成代码
     */
    public Map<String, String> generateCode(GenTableDTO dto) {
        Map<String, String> result = new LinkedHashMap<>();
        try {
            GenTableVO tableVO = buildTableVO(dto);
            Map<String, Object> model = new HashMap<>();
            model.put("table", tableVO);
            model.put("dto", dto);

            // Entity
            String entityCode = processTemplate("java/entity.java.ftl", model);
            result.put("Entity", entityCode);

            // Mapper
            String mapperCode = processTemplate("java/mapper.java.ftl", model);
            result.put("Mapper", mapperCode);

            // Service
            String serviceCode = processTemplate("java/service.java.ftl", model);
            result.put("Service", serviceCode);

            // ServiceImpl
            String serviceImplCode = processTemplate("java/serviceImpl.java.ftl", model);
            result.put("ServiceImpl", serviceImplCode);

            // Controller
            String controllerCode = processTemplate("java/controller.java.ftl", model);
            result.put("Controller", controllerCode);

        } catch (Exception e) {
            log.error("生成代码失败", e);
        }
        return result;
    }

    /**
     * 构建 GenTableVO
     */
    private GenTableVO buildTableVO(GenTableDTO dto) {
        GenTableVO vo = new GenTableVO();
        vo.setTableName(dto.getTableName());
        vo.setTableComment(dto.getTableComment());
        vo.setClassName(dto.getClassName());
        vo.setClassNameLower(com.gem.baize.gen.util.GenUtils.uncapitalize(dto.getClassName()));
        vo.setModuleName(dto.getModuleName());
        vo.setBusinessName(dto.getBusinessName());
        vo.setFunctionName(dto.getFunctionName());
        vo.setAuthor(dto.getAuthor());
        vo.setPackageName(dto.getPackageName());
        if (dto.getColumns() != null) {
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
        }
        return vo;
    }

    private String processTemplate(String templateName, Map<String, Object> model) throws IOException, TemplateException {
        Template template = freeMarkerConfig.getTemplate(templateName, "UTF-8");
        try (StringWriter writer = new StringWriter()) {
            template.process(model, writer);
            return writer.toString();
        }
    }
}
