package com.gem.baize.common.security.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "刷新Token请求参数")
public class TokenRefreshDTO {
    @NotBlank(message = "刷新令牌不能为空")
    @Schema(description = "刷新令牌")
    private String refreshToken;

    @NotBlank(message = "用户ID不能为空")
    @Schema(description = "用户ID")
    private String userId;

    @NotBlank(message = "租户ID不能为空")
    @Schema(description = "租户ID")
    private String tenantId;

    @NotBlank(message = "用户名不能为空")
    @Schema(description = "用户名")
    private String username;
}
