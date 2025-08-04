package com.gem.baize.admin.tenant.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gem.baize.common.core.annotation.GeneratedId;
import com.gem.baize.common.datasource.entity.BaseEntity;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 租户信息
 */
@Data
@TableName("sys_tenant")
public class Tenant extends BaseEntity {
    /**
     * 业务ID
     */
    @GeneratedId(prefix = "tenant-")
    @TableField(value = "biz_id", fill = FieldFill.INSERT_UPDATE)
    private String bizId;
    /**
     * 租户名称
     */
    @TableField("tenant_name")
    private String tenantName;
    /**
     * 租户编码
     */
    @TableField("tenant_code")
    private String tenantCode;
    /**
     * 过期时间
     */
    @TableField("expire_time")
    private LocalDateTime expireTime;
    /**
     * 联系人
     */
    @TableField("contact_person")
    private String contactPerson;
    /**
     * 联系号码
     */
    @TableField("contact_phone")
    private String contactPhone;
    /**
     * 联系邮件
     */
    @TableField("contact_email")
    private String contactEmail;
    /**
     * 短域名
     */
    @TableField("domain")
    private String domain;
    /**
     * 最大用户数
     */
    @TableField("max_user")
    private Integer maxUser;
}
