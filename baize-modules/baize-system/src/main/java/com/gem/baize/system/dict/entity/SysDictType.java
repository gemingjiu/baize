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

    @TableField(value = "tenant_id")
    private String tenantId;

    @TableField(value = "dict_name")
    private String dictName;

    @TableField(value = "dict_type")
    private String dictType;
}
