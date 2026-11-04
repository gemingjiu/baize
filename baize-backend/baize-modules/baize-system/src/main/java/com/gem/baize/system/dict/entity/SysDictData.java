package com.gem.baize.system.dict.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gem.baize.common.database.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("sys_dict_data")
public class SysDictData extends BaseEntity {

    @TableField(value = "dict_type")
    private String dictType;

    @TableField(value = "dict_label")
    private String dictLabel;

    @TableField(value = "dict_value")
    private String dictValue;

    /**
     * 是否内置字典项;0:租户自定义,1:平台内置（平台维护，租户不可删）
     */
    @TableField(value = "is_builtin")
    private String isBuiltin;

    @TableField(value = "defaulted")
    private String defaulted;
}
