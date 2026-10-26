package com.gem.baize.common.cache.service.impl;

import com.gem.baize.common.cache.service.TokenBlacklistService;
import com.gem.baize.common.core.utils.TokenBlacklistUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * 基于 Redis 的访问令牌黑名单实现
 * <p>
 * 认证服务（Servlet 栈）侧使用阻塞式 StringRedisTemplate；网关侧为 WebFlux，
 * 另有响应式实现查询同一个 key（见 gateway 模块的 ReactiveTokenBlacklistChecker）。
 */
@Slf4j
@Service
public class TokenBlacklistServiceImpl implements TokenBlacklistService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void addToBlacklist(String accessToken, long ttlSeconds) {
        if (!StringUtils.hasText(accessToken)) {
            return;
        }
        if (ttlSeconds <= 0) {
            // 令牌本身已过期，无需写入黑名单
            return;
        }
        String key = TokenBlacklistUtils.buildBlacklistKey(accessToken);
        stringRedisTemplate.opsForValue().set(key, "1", ttlSeconds, TimeUnit.SECONDS);
        log.debug("访问令牌已加入黑名单: key={}, ttl={}s", key, ttlSeconds);
    }

    @Override
    public boolean isBlacklisted(String accessToken) {
        if (!StringUtils.hasText(accessToken)) {
            return false;
        }
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(TokenBlacklistUtils.buildBlacklistKey(accessToken)));
    }
}
