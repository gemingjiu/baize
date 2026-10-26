package com.gem.baize.auth.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 切换租户请求参数
 */
@Data
public class SwitchTenantDTO {

    /**
     * 目标租户ID。
     * <p>
     * 只接受数字：租户 ID 会原样写入令牌的 X-Tenant-Id，再由 CustomTenantHandler
     * 拼进 SQL，必须杜绝注入非数字内容。平台租户固定为 0。
     */
    @NotBlank(message = "目标租户ID不能为空")
    @Pattern(regexp = "\\d+", message = "目标租户ID必须为数字")
    private String tenantId;
}
