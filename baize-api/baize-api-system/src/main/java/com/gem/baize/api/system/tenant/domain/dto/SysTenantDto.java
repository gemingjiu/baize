package com.gem.baize.api.system.tenant.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gem.baize.common.core.model.dto.BaseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 租户信息
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SysTenantDto extends BaseDto {

    /**
     * 租户名称
     */
    private String tenantName;
    /**
     * 租户编码
     */
    private String tenantCode;
    /**
     * 过期时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expireTime;
    /**
     * 联系人
     */
    private String contactPerson;
    /**
     * 联系号码
     */
    private String contactPhone;
    /**
     * 联系邮件
     */
    private String contactEmail;
    /**
     * 短域名
     */
    private String domain;
    /**
     * 最大用户数
     */
    private Integer maxUser;
}
