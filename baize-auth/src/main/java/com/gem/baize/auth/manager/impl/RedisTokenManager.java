package com.gem.baize.auth.manager.impl;

import com.gem.baize.auth.manager.TokenManager;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Redis令牌管理实现
 */
@Component
public class RedisTokenManager implements TokenManager {

    private static final String REFRESH_TOKEN_PREFIX = "refresh_token:";
    /**
     * 刷新令牌过期时间：7天（秒）
     */
    private static final long REFRESH_TOKEN_EXPIRE = 7 * 24 * 60 * 60;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void saveRefreshToken(String tenantId, String userId, String refreshToken) {
        String key = buildKey(tenantId, userId);
        stringRedisTemplate.opsForValue().set(key, refreshToken, REFRESH_TOKEN_EXPIRE, TimeUnit.SECONDS);
    }

    @Override
    public boolean validateRefreshToken(String tenantId, String userId, String refreshToken) {
        String key = buildKey(tenantId, userId);
        String storedToken = stringRedisTemplate.opsForValue().get(key);
        return StringUtils.equals(storedToken, refreshToken);
    }

    @Override
    public void deleteRefreshToken(String tenantId, String userId) {
        String key = buildKey(tenantId, userId);
        stringRedisTemplate.delete(key);
    }

    @Override
    public String getRefreshToken(String tenantId, String userId) {
        String key = buildKey(tenantId, userId);
        return stringRedisTemplate.opsForValue().get(key);
    }

    private String buildKey(String tenantId, String userId) {
        return REFRESH_TOKEN_PREFIX + tenantId + ":" + userId;
    }
}
