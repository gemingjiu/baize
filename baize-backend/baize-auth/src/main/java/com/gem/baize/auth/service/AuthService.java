package com.gem.baize.auth.service;

import com.gem.baize.common.security.domain.dto.LoginDTO;
import com.gem.baize.common.security.domain.dto.Payload;
import com.gem.baize.common.security.domain.dto.TokenRefreshDTO;
import com.gem.baize.common.security.domain.vo.LoginVO;

/**
 * 认证服务接口
 */
public interface AuthService {

    /**
     * 用户登录
     *
     * @param dto 登录参数
     * @return 登录结果
     */
    LoginVO login(LoginDTO dto);

    /**
     * 用户登出
     *
     * @param userId 用户ID
     * @param tenantId 租户ID
     */
    void logout(String userId, String tenantId);

    /**
     * 用户登出（同时把当前访问令牌加入黑名单，使其立即失效）
     *
     * @param userId      用户ID
     * @param tenantId    租户ID
     * @param accessToken 当前访问令牌
     */
    void logout(String userId, String tenantId, String accessToken);

    /**
     * T1.3：用户登出（含 HttpOnly Cookie 模式下的 refresh family 吊销）。
     *
     * @param userId       用户ID
     * @param tenantId     租户ID
     * @param accessToken  当前访问令牌
     * @param refreshToken 从 Cookie 读取的 refresh 明文（flag 关闭时可为 null）
     */
    void logout(String userId, String tenantId, String accessToken, String refreshToken);

    /**
     * 刷新令牌
     *
     * @param dto 刷新令牌参数
     * @return 新的令牌
     */
    LoginVO refreshToken(TokenRefreshDTO dto);

    /**
     * 切换租户：为同一用户签发目标租户下的新令牌。
     *
     * @param payload      当前访问令牌解析出的载荷（可信身份来源）
     * @param targetTenant 目标租户ID
     * @param accessToken  当前访问令牌，切换成功后加入黑名单使其立即失效
     * @return 新的令牌
     */
    LoginVO switchTenant(Payload payload, String targetTenant, String accessToken);
}
