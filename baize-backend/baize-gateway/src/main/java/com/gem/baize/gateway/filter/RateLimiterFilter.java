package com.gem.baize.gateway.filter;

import com.gem.baize.gateway.enums.FilterOrder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 限流过滤器：基于客户端 IP 的固定窗口限流（单实例内存实现）
 * 可通过 baize.gateway.rate-limit.enabled / qps 配置开关与阈值
 */
@Component
public class RateLimiterFilter implements GlobalFilter, Ordered {

    private static final class WindowCounter {
        final AtomicLong count = new AtomicLong(0);
        volatile long windowStart = System.currentTimeMillis();
    }

    private final Map<String, WindowCounter> counters = new ConcurrentHashMap<>();

    @Value("${baize.gateway.rate-limit.enabled:false}")
    private boolean enabled;

    @Value("${baize.gateway.rate-limit.qps:100}")
    private long qps;

    /** 窗口大小：1 秒 */
    private static final long WINDOW_MS = 1000L;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (!enabled) {
            return chain.filter(exchange);
        }
        // OPTIONS 预检不限制
        if ("OPTIONS".equals(exchange.getRequest().getMethod().name())) {
            return chain.filter(exchange);
        }

        String key = resolveKey(exchange.getRequest());
        WindowCounter counter = counters.computeIfAbsent(key, k -> new WindowCounter());

        long now = System.currentTimeMillis();
        synchronized (counter) {
            // 窗口过期则重置
            if (now - counter.windowStart >= WINDOW_MS) {
                counter.windowStart = now;
                counter.count.set(0);
            }
            long current = counter.count.incrementAndGet();
            if (current > qps) {
                exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                return exchange.getResponse().setComplete();
            }
        }
        return chain.filter(exchange);
    }

    private String resolveKey(ServerHttpRequest request) {
        String ip = request.getHeaders().getFirst("X-Real-IP");
        if (ip == null || ip.isEmpty()) {
            ip = request.getHeaders().getFirst("X-Forwarded-For");
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddress() != null
                    ? request.getRemoteAddress().getAddress().getHostAddress() : "unknown";
        }
        return ip;
    }

    @Override
    public int getOrder() {
        return FilterOrder.RATE_LIMITER_FILTER.getOrder();
    }
}
