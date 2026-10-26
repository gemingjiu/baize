package com.gem.baize.gateway.filter;


import com.gem.baize.common.core.constant.CustomHttpHeaders;
import com.gem.baize.common.security.domain.dto.Payload;
import com.gem.baize.common.security.util.JwtUtils;
import com.gem.baize.gateway.config.GatewayWhitelist;
import com.gem.baize.gateway.enums.FilterOrder;
import com.gem.baize.gateway.filter.support.ReactiveTokenBlacklistChecker;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 认证过滤器
 */
@Slf4j
@Component
public class AuthFilter implements GlobalFilter, Ordered {
    public static final String BEARER = "Bearer ";
    private static final String SKIP_AUTH_METADATA_KEY = "skipAuth";

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private ReactiveTokenBlacklistChecker tokenBlacklistChecker;

    @Autowired
    private GatewayWhitelist gatewayWhitelist;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // 放行OPTIONS请求
        if (request.getMethod().name().equals("OPTIONS")) {
            return chain.filter(exchange);
        }

        // 配置文件白名单跳过认证
        if (gatewayWhitelist.matches(path)) {
            return chain.filter(exchange);
        }

        Route route = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
        // 元数据标记为skipAuth的路由地址
        if (isSkipAuth(route)) {
            return chain.filter(exchange);
        }

        return authenticate(exchange, chain);
    }

    private Mono<Void> authenticate(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String auth = request.getHeaders().getFirst(CustomHttpHeaders.AUTHORIZATION);
        // 缺少令牌或不是 Bearer 格式直接拒绝
        if (StringUtils.isBlank(auth) || !auth.startsWith(BEARER)) {
            return unauthorized(exchange);
        }
        String token = auth.substring(BEARER.length()).trim();
        if (StringUtils.isBlank(token)) {
            return unauthorized(exchange);
        }

        // 验证token签名与有效期
        Payload payload;
        try {
            payload = jwtUtils.parsePayload(token);
        } catch (Exception e) {
            log.debug("JWT 校验失败: {}", e.getMessage());
            return unauthorized(exchange);
        }

        // 构造透传给下游服务的用户上下文请求头（声明缺失时不写入，避免 NPE）
        ServerHttpRequest mutatedRequest = buildUserContextRequest(request, payload);

        // 验签通过后再查黑名单：登出后令牌立即失效。
        // 查询失败时 fail-closed：宁可拒绝请求，也不能让已登出的令牌在存储故障期间继续通行。
        return tokenBlacklistChecker.isBlacklisted(token)
                .flatMap(blacklisted -> {
                    if (Boolean.TRUE.equals(blacklisted)) {
                        log.info("令牌已失效（命中黑名单）: path={}", request.getURI().getPath());
                        return unauthorized(exchange);
                    }
                    return chain.filter(exchange.mutate().request(mutatedRequest).build());
                })
                .onErrorResume(e -> {
                    log.error("访问令牌黑名单查询失败，按 fail-closed 拒绝请求: path={}, error={}",
                            request.getURI().getPath(), e.getMessage());
                    return serviceUnavailable(exchange);
                });
    }

    /**
     * 把解析出的用户上下文写入下游请求头
     */
    private ServerHttpRequest buildUserContextRequest(ServerHttpRequest request, Payload payload) {
        ServerHttpRequest.Builder builder = request.mutate();
        addHeaderIfPresent(builder, CustomHttpHeaders.SUBJECT_ID, payload.getSubject());
        addHeaderIfPresent(builder, CustomHttpHeaders.TENANT_ID, payload.getTenantId());
        addHeaderIfPresent(builder, CustomHttpHeaders.USER_ID, payload.getUserId());
        addHeaderIfPresent(builder, CustomHttpHeaders.USER_NAME, payload.getUserName());
        addHeaderIfPresent(builder, CustomHttpHeaders.ROLE, payload.getRole());
        addHeaderIfPresent(builder, CustomHttpHeaders.DATA_SCOPE, payload.getDataScope());
        return builder.build();
    }

    private void addHeaderIfPresent(ServerHttpRequest.Builder builder, String name, String value) {
        if (StringUtils.isNotBlank(value)) {
            builder.header(name, value);
        }
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    /**
     * 依赖的鉴权存储不可用时的响应。
     * <p>
     * 返回 503 而不是放行：让调用方明确知道「无法判定令牌状态」，
     * 而不是把已登出的令牌当作有效令牌继续转发。
     */
    private Mono<Void> serviceUnavailable(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
        return exchange.getResponse().setComplete();
    }

    private boolean isSkipAuth(Route route) {
        if (route == null || route.getMetadata() == null) {
            return false;
        }
        // 获取元数据中的skipAuth值
        Object skipAuth = route.getMetadata().get(SKIP_AUTH_METADATA_KEY);
        return Boolean.TRUE.equals(skipAuth);
    }


    @Override
    public int getOrder() {
        return FilterOrder.AUTH_FILTER.getOrder();
    }
}
