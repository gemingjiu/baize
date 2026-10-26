package com.gem.baize.gateway.filter.support;

import com.gem.baize.common.core.utils.TokenBlacklistUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * 访问令牌黑名单查询器（网关侧）
 * <p>
 * 网关是 WebFlux 响应式栈，这里使用 ReactiveStringRedisTemplate 查询认证服务写入的黑名单 key，
 * 避免引入阻塞式 RedisTemplate 阻塞事件循环线程。
 */
@Component
public class ReactiveTokenBlacklistChecker {

    @Autowired
    private ReactiveStringRedisTemplate reactiveStringRedisTemplate;

    /**
     * 判断访问令牌是否命中黑名单
     *
     * @param accessToken 访问令牌
     * @return true 表示令牌已失效
     */
    public Mono<Boolean> isBlacklisted(String accessToken) {
        return reactiveStringRedisTemplate.hasKey(TokenBlacklistUtils.buildBlacklistKey(accessToken))
                .defaultIfEmpty(Boolean.FALSE);
    }
}
