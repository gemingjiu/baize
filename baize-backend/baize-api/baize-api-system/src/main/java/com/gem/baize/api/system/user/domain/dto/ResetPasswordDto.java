package com.gem.baize.api.system.user.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * 重置密码请求体（管理员重置用户密码）。
 *
 * <p>密码属于敏感凭据，此前通过 URL query parameter 传递，会落入网关/反向代理/Web 服务器的
 * access log 与浏览器历史，造成凭据泄露。改为 PUT body 传递，避免明文出现在任何日志链路中。</p>
 */
@Data
@Schema(description = "重置密码请求")
public class ResetPasswordDto {

    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "用户ID不能为空")
    private String userId;

    @Schema(description = "新密码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "新密码不能为空")
    private String newPassword;
}
