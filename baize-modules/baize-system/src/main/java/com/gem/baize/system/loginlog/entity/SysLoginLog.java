package com.gem.baize.system.loginlog.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gem.baize.common.database.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("sys_login_log")
public class SysLoginLog extends BaseEntity {

    @TableField(value = "tenant_id")
    private String tenantId;

    @TableField(value = "user_id")
    private String userId;

    @TableField(value = "user_name")
    private String userName;

    @TableField(value = "ipaddr")
    private String ipaddr;

    @TableField(value = "login_location")
    private String loginLocation;

    @TableField(value = "browser")
    private String browser;

    @TableField(value = "os")
    private String os;

    @TableField(value = "status")
    private String status;

    @TableField(value = "msg")
    private String msg;

    @TableField(value = "login_time")
    private LocalDateTime loginTime;
}
