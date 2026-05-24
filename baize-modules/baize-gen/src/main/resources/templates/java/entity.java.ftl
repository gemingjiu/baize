package ${dto.packageName}.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gem.baize.common.database.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * ${dto.tableComment!}
 *
 * @author ${dto.author!"baize"}
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("${dto.tableName}")
public class ${dto.className} extends BaseEntity {

    private static final long serialVersionUID = 1L;

<#list dto.columns as column>
<#if column.columnName != "id" && column.columnName != "sort" && column.columnName != "version" && column.columnName != "status" && column.columnName != "deleted" && column.columnName != "created_by" && column.columnName != "created_time" && column.columnName != "modified_by" && column.columnName != "modified_time" && column.columnName != "remark">
    /** ${column.columnComment!} */
    @TableField(value = "${column.columnName}")
    private ${column.javaType} ${column.javaField};

</#if>
</#list>
}
