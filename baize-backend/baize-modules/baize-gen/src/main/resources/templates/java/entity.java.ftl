package ${dto.packageName}.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gem.baize.common.database.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * ${dto.tableComment!dto.tableName}
 *
 * @author ${dto.author!"baize"}
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("${dto.tableName}")
public class ${dto.className} extends BaseEntity {

    private static final long serialVersionUID = 1L;

<#list table.columns as column>
<#if !column.baseColumn>
    /**
     * ${column.columnComment!column.columnName}
     */
    @TableField(value = "${column.columnName}")
    private ${column.javaType} ${column.javaField};

</#if>
</#list>
}
