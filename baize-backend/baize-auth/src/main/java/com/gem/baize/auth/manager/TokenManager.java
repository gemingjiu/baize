package com.gem.baize.auth.manager;

/**
 * 令牌管理接口
 */
public interface TokenManager {

    /**
     * 保存刷新令牌
     *
     * @param tenantId     租户ID
     * @param userId       用户ID
     * @param refreshToken 刷新令牌
     */
    void saveRefreshToken(String tenantId, String userId, String refreshToken);

    /**
     * 验证刷新令牌
     *
     * @param tenantId     租户ID
     * @param userId       用户ID
     * @param refreshToken 刷新令牌
     * @return 是否有效
     */
    boolean validateRefreshToken(String tenantId, String userId, String refreshToken);

    /**
     * 删除刷新令牌
     *
     * @param tenantId 租户ID
     * @param userId   用户ID
     */
    void deleteRefreshToken(String tenantId, String userId);

    /**
     * 获取刷新令牌
     *
     * @param tenantId 租户ID
     * @param userId   用户ID
     * @return 刷新令牌
     */
    String getRefreshToken(String tenantId, String userId);
}
