package com.gem.baize.gateway.filter;


import com.gem.baize.common.core.constant.HTTPHeaderConstant;
import com.gem.baize.common.security.util.JwtUtils;
import com.gem.baize.gateway.config.SecurityProperties;
import com.gem.baize.gateway.enums.FilterOrder;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import org.springframework.http.server.reactive.ServerHttpRequest;

/**
 * 认证过滤器
 */
@Component
public class AuthFilter implements GlobalFilter, Ordered {
    public static final String BEARER = "Bearer ";

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    private final SecurityProperties securityProperties;

    public AuthFilter(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 白名单路径不需要认证
        String path = exchange.getRequest().getPath().value();
        boolean shouldSkip = securityProperties.getExcludePaths().stream()
                .anyMatch(pattern -> antPathMatcher.match(pattern, path));

        if (shouldSkip) {
            return chain.filter(exchange);
        }

        ServerHttpRequest request = exchange.getRequest();
        String token = request.getHeaders().getFirst(HTTPHeaderConstant.AUTHORIZATION);
        if (StringUtils.isBlank(token)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        // 验证token
        if (StringUtils.isNotBlank(token) && !token.startsWith(BEARER)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        try {
            Claims claims = JwtUtils.parseToken(token.replace(BEARER, ""));
            exchange.getRequest().mutate()
                    .header(HTTPHeaderConstant.TENANT_ID, claims.get(HTTPHeaderConstant.TENANT_ID, String.class))
                    .header(HTTPHeaderConstant.USER_ID, claims.getSubject())
                    .header(HTTPHeaderConstant.USER_NAME, claims.get(HTTPHeaderConstant.USER_NAME, String.class))
                    .header(HTTPHeaderConstant.ROLE, claims.get(HTTPHeaderConstant.ROLE, String.class));
        } catch (Exception e) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        return chain.filter(exchange);
    }


    @Override
    public int getOrder() {
        return FilterOrder.AUTH_FILTER.getOrder();
    }
}
