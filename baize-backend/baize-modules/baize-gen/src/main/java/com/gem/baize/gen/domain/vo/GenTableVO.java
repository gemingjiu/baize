package com.gem.baize.gen.domain.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 代码生成表信息
 */
@Data
public class GenTableVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 表名称 */
    private String tableName;

    /** 表描述 */
    private String tableComment;

    /** 实体类名称 */
    private String className;

    /** 实体类小写名称 */
    private String classNameLower;

    /** 模块名 */
    private String moduleName;

    /** 业务名 */
    private String businessName;

    /** 功能名 */
    private String functionName;

    /** 作者 */
    private String author;

    /** 包路径 */
    private String packageName;

    /** 表字段 */
    private List<GenTableColumnVO> columns;
}
