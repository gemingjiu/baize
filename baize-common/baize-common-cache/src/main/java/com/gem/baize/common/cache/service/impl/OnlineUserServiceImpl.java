package com.gem.baize.common.cache.service.impl;

import com.gem.baize.common.core.service.OnlineUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class OnlineUserServiceImpl implements OnlineUserService {

    private static final String ONLINE_KEY_PREFIX = "baize:online:";
    private static final long ONLINE_TIMEOUT_MINUTES = 30;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private String buildKey(String tenantId) {
        return ONLINE_KEY_PREFIX + (tenantId != null ? tenantId : "default");
    }

    @Override
    public void login(String userId, String tenantId, String username) {
        String key = buildKey(tenantId);
        redisTemplate.opsForZSet().add(key, userId, System.currentTimeMillis());
        redisTemplate.expire(key, ONLINE_TIMEOUT_MINUTES, TimeUnit.MINUTES);
        log.debug("用户登录: userId={}, tenantId={}", userId, tenantId);
    }

    @Override
    public void logout(String userId, String tenantId) {
        String key = buildKey(tenantId);
        redisTemplate.opsForZSet().remove(key, userId);
        log.debug("用户登出: userId={}, tenantId={}", userId, tenantId);
    }

    @Override
    public void refresh(String userId, String tenantId) {
        String key = buildKey(tenantId);
        redisTemplate.opsForZSet().add(key, userId, System.currentTimeMillis());
    }

    @Override
    public long getOnlineCount(String tenantId) {
        String key = buildKey(tenantId);
        cleanExpired(tenantId);
        Long count = redisTemplate.opsForZSet().size(key);
        return count != null ? count : 0;
    }

    @Override
    public List<String> getOnlineUsers(String tenantId) {
        String key = buildKey(tenantId);
        cleanExpired(tenantId);
        Set<Object> members = redisTemplate.opsForZSet().range(key, 0, -1);
        List<String> result = new ArrayList<>();
        if (members != null) {
            for (Object member : members) {
                result.add(member.toString());
            }
        }
        return result;
    }

    @Override
    public boolean isOnline(String userId, String tenantId) {
        String key = buildKey(tenantId);
        Double score = redisTemplate.opsForZSet().score(key, userId);
        if (score == null) {
            return false;
        }
        long lastActive = score.longValue();
        long timeoutMillis = ONLINE_TIMEOUT_MINUTES * 60 * 1000;
        return System.currentTimeMillis() - lastActive < timeoutMillis;
    }

    @Override
    public void kickout(String userId, String tenantId) {
        logout(userId, tenantId);
        log.info("用户被强制下线: userId={}, tenantId={}", userId, tenantId);
    }

    private void cleanExpired(String tenantId) {
        String key = buildKey(tenantId);
        long minScore = System.currentTimeMillis() - (ONLINE_TIMEOUT_MINUTES * 60 * 1000);
        redisTemplate.opsForZSet().removeRangeByScore(key, 0, minScore);
    }
}
