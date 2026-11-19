package com.gem.baize.common.security.domain.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    /**
     * T1.3：HttpOnly Cookie 模式的刷新令牌载荷，仅 service→controller 内部传递，
     * 绝不进 JSON 响应（明文 refresh 只能经 Set-Cookie 下发）。flag 关闭时为 null。
     */
    @JsonIgnore
    private IssuedRefresh issuedRefresh;
}

