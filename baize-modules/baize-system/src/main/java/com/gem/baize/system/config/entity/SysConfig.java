package com.gem.baize.system.config.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gem.baize.common.database.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("sys_config")
public class SysConfig extends BaseEntity {

    @TableField(value = "tenant_id")
    private String tenantId;

    @TableField(value = "config_name")
    private String configName;

    @TableField(value = "config_key")
    private String configKey;

    @TableField(value = "config_value")
    private String configValue;

    @TableField(value = "config_type")
    private String configType;
}
