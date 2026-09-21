package com.gem.baize.gen.util;

import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 代码生成工具类
 */
public class GenUtils {

    /** 数据库类型映射 */
    public static String getJavaType(String columnType) {
        if (columnType == null) {
            return "String";
        }
        String type = columnType.toLowerCase();
        if (type.contains("char") || type.contains("text") || type.contains("json")) {
            return "String";
        } else if (type.contains("bigint")) {
            return "Long";
        } else if (type.contains("int") || type.contains("serial")) {
            return "Integer";
        } else if (type.contains("tinyint")) {
            return "Boolean";
        } else if (type.contains("decimal") || type.contains("numeric")) {
            return "BigDecimal";
        } else if (type.contains("double")) {
            return "Double";
        } else if (type.contains("float")) {
            return "Float";
        } else if (type.contains("date") || type.contains("time")) {
            return "LocalDateTime";
        } else if (type.contains("bytea")) {
            return "byte[]";
        }
        return "String";
    }

    /** 转换数据库字段名到Java属性名 */
    public static String columnToJavaField(String columnName) {
        if (!StringUtils.hasText(columnName)) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        String[] parts = columnName.split("_");
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i].toLowerCase();
            if (i == 0) {
                result.append(part);
            } else {
                result.append(StringUtils.capitalize(part));
            }
        }
        return result.toString();
    }

    /** 转换表名到类名 */
    public static String tableToClassName(String tableName, String prefix) {
        String name = tableName;
        if (StringUtils.hasText(prefix) && tableName.startsWith(prefix)) {
            name = tableName.substring(prefix.length());
        }
        // 默认去掉 sys_ 前缀
        if (name.startsWith("sys_")) {
            name = name.substring(4);
        }
        String[] parts = name.split("_");
        StringBuilder result = new StringBuilder();
        for (String part : parts) {
            if (StringUtils.hasText(part)) {
                result.append(StringUtils.capitalize(part.toLowerCase()));
            }
        }
        return result.toString();
    }

    /** 首字母大写 */
    public static String capitalize(String str) {
        return StringUtils.capitalize(str);
    }

    /** 首字母小写 */
    public static String uncapitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }

    /** 判断字段是否在基类中已经存在 */
    public static boolean isBaseEntityField(String columnName) {
        Set<String> baseFields = new HashSet<>(Arrays.asList(
                "id", "sort", "version", "status", "deleted",
                "created_by", "created_time", "modified_by", "modified_time", "remark"
        ));
        return baseFields.contains(columnName);
    }

    /** 获取字段的@TableField注解 */
    public static String getTableFieldAnnotation(String columnName) {
        if ("id".equals(columnName)) {
            return "@TableId(type = IdType.ASSIGN_ID)";
        }
        return "@TableField(value = \"" + columnName + "\")";
    }
}
