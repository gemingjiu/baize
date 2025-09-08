package com.gem.baize.common.security.domain.vo;


import lombok.Data;

/**
 * 登录成功响应体
 */
@Data
public class LoginVO {
    private String accessToken;

    private String refreshToken;

    private String tokenType = "Bearer";

    private Integer expiresIn;

    private UserVO user;
}

