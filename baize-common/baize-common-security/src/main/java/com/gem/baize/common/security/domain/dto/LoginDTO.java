package com.gem.baize.common.security.domain.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 多租户系统登录凭证
 */
@Schema(description = "登录请求参数")
@Data
public class LoginDTO {
    @NotBlank(message = "租户标识不能为空")
    @Schema(description = "租户域名或编码", example = "tenant.com")
    private String tenant;

    @NotBlank(message = "用户名不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9_]{4,20}$", message = "用户名格式错误")
    @Schema(description = "用户名", example = "admin")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 8, max = 20, message = "密码长度8-20位")
    @Schema(description = "密码", example = "Password123!", format = "password")
    private String password;

    @Schema(description = "验证码（可选）", example = "3842")
    private String captcha;

    @Schema(description = "设备标识（用于多端登录控制）", example = "WEB/APP")
    private String deviceType;
}