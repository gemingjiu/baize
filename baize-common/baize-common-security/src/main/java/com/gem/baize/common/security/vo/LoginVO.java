package com.gem.baize.common.security.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 登录成功响应体
 */
@Data
@Schema(description = "登录响应数据")
public class LoginVO {

    @Schema(description = "访问令牌", example = "eyJhbGciOi...")
    private String accessToken;

    @Schema(description = "刷新令牌", example = "eyJhbGciOi...")
    private String refreshToken;

    @Schema(description = "令牌类型", example = "Bearer")
    private String tokenType = "Bearer";

    @Schema(description = "过期时间（秒）", example = "3600")
    private Integer expiresIn;

    @Schema(description = "用户基础信息")
    private UserVO user;
}

