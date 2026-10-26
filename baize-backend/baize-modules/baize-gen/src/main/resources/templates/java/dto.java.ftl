package ${dto.packageName}.domain.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * ${dto.tableComment!dto.tableName} DTO
 *
 * @author ${dto.author!"baize"}
 */
@Data
public class ${dto.className}Dto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 业务ID
     */
    private String id;

    /**
     * 租户ID
     */
    private String tenantId;

<#list table.columns as column>
<#if !column.baseColumn>
    /**
     * ${column.columnComment!column.columnName}
     */
    private ${column.javaType} ${column.javaField};

</#if>
</#list>
}
