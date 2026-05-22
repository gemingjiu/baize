package com.gem.baize.gateway.filter;


import com.gem.baize.common.core.constant.CustomHttpHeaders;
import com.gem.baize.common.security.domain.dto.Payload;
import com.gem.baize.common.security.util.JwtUtils;
import com.gem.baize.gateway.enums.FilterOrder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

import java.util.List;

/**
 * 认证过滤器
 */
@Component
public class AuthFilter implements GlobalFilter, Ordered {
    public static final String BEARER = "Bearer ";
    private static final String SKIP_AUTH_METADATA_KEY = "skipAuth";

    @Autowired
    private  JwtUtils jwtUtils;

    @Value("${baize.gateway.whitelist:}")
    private List<String> whitelist;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // 放行OPTIONS请求
        if (request.getMethod().name().equals("OPTIONS")) {
            return chain.filter(exchange);
        }

        // 配置文件白名单跳过认证
        if (isWhitelist(path)) {
            return chain.filter(exchange);
        }

        Route route = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
        // 元数据标记为skipAuth的路由地址
        if (isSkipAuth(route)) {
            return chain.filter(exchange);
        }

        return authenticate(exchange, chain);
    }

    private boolean isWhitelist(String path) {
        if (whitelist == null || whitelist.isEmpty()) {
            return false;
        }
        for (String whitePath : whitelist) {
            if (whitePath.endsWith("/**")) {
                String prefix = whitePath.substring(0, whitePath.length() - 3);
                if (path.startsWith(prefix)) {
                    return true;
                }
            } else if (path.equals(whitePath)) {
                return true;
            }
        }
        return false;
    }

    private Mono<Void> authenticate(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String auth = request.getHeaders().getFirst(CustomHttpHeaders.AUTHORIZATION);
        if (StringUtils.isBlank(auth)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        // 验证token
        if (StringUtils.isNotBlank(auth) && !auth.startsWith(BEARER)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        try {
            Payload payload = jwtUtils.parsePayload(auth.replace(BEARER, ""));
            exchange.getRequest().mutate()
                    .header(CustomHttpHeaders.SUBJECT_ID, payload.getSubject())
                    .header(CustomHttpHeaders.TENANT_ID, payload.getTenantId())
                    .header(CustomHttpHeaders.USER_ID, payload.getUserId())
                    .header(CustomHttpHeaders.USER_NAME, payload.getUserName())
                    .header(CustomHttpHeaders.ROLE, payload.getRole());
        } catch (Exception e) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        return chain.filter(exchange);
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
