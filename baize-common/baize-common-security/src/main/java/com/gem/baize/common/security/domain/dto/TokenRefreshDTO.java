package com.gem.baize.common.security.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "刷新Token请求参数")
public class TokenRefreshDTO {
    @NotBlank(message = "刷新令牌不能为空")
    private String refreshToken;
}
