package com.gem.baize.api.admin.tenant.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.gem.baize.common.core.entity.BaseEntity;
import lombok.Data;

import java.time.LocalDateTime;

/**
 *  租户信息
 */
@Data
@TableName("sys_tenant")
public class Tenant extends BaseEntity {
    // 租户ID
    @TableId(type = IdType.AUTO)
    private Long id;

    // 租户名称
    @TableField("tenant_name")
    private String tenantName;

    // 租户编码
    @TableField("tenant_code")
    private String tenantCode;

    // 过期时间
    @TableField("expire_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expireTime;

    // 联系人
    @TableField("contact_person")
    private String contactPerson;

    // 联系号码
    @TableField("contact_phone")
    private String contactPhone;

    // 联系邮件
    @TableField("contact_email")
    private String contactEmail;

    // 短域名
    @TableField("domain")
    private String domain;

    // 最大用户数
    @TableField("max_user")
    private Integer maxUser;
}
