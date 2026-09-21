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

    @TableField(value = "tenant_id")
    private String tenantId;

    @TableField(value = "dict_type")
    private String dictType;

    @TableField(value = "dict_label")
    private String dictLabel;

    @TableField(value = "dict_value")
    private String dictValue;

    @TableField(value = "css_class")
    private String cssClass;

    @TableField(value = "list_class")
    private String listClass;

    @TableField(value = "defaulted")
    private String defaulted;
}
