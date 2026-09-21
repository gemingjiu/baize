package com.gem.baize.auth.service;

import com.gem.baize.common.security.domain.dto.LoginDTO;
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
     * 刷新令牌
     *
     * @param dto 刷新令牌参数
     * @return 新的令牌
     */
    LoginVO refreshToken(TokenRefreshDTO dto);
}
