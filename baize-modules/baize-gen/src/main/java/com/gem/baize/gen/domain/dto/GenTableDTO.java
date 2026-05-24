package com.gem.baize.gen.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 代码生成表参数
 */
@Data
public class GenTableDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 表名称 */
    @NotBlank(message = "表名称不能为空")
    private String tableName;

    /** 表描述 */
    private String tableComment;

    /** 实体类名称 */
    private String className;

    /** 模块名 */
    @NotBlank(message = "模块名不能为空")
    private String moduleName;

    /** 业务名 */
    @NotBlank(message = "业务名不能为空")
    private String businessName;

    /** 功能名 */
    private String functionName;

    /** 作者 */
    private String author;

    /** 包路径 */
    private String packageName;

    /** 字段信息 */
    private List<GenTableColumnDTO> columns;
}
