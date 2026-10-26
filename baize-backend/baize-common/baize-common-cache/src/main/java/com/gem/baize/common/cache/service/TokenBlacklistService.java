package com.gem.baize.common.cache.service;

/**
 * 访问令牌黑名单服务
 * <p>
 * 用户登出后把访问令牌写入黑名单，网关据此让令牌立即失效，而不是等待 JWT 自然过期。
 */
public interface TokenBlacklistService {

    /**
     * 将访问令牌加入黑名单
     *
     * @param accessToken 访问令牌
     * @param ttlSeconds  过期时间（秒），取令牌剩余有效期；小于等于 0 时表示令牌已过期，无需写入
     */
    void addToBlacklist(String accessToken, long ttlSeconds);

    /**
     * 判断访问令牌是否已失效（命中黑名单）
     *
     * @param accessToken 访问令牌
     * @return true 表示已失效
     */
    boolean isBlacklisted(String accessToken);
}
