package com.gem.baize.system.dict.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gem.baize.common.database.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("sys_dict_type")
public class SysDictType extends BaseEntity {

    @TableField(value = "dict_name")
    private String dictName;

    @TableField(value = "dict_type")
    private String dictType;

    /**
     * 是否内置字典类型;0:租户自定义,1:平台内置（内置不可删除）
     */
    @TableField(value = "is_builtin")
    private String isBuiltin;
}
