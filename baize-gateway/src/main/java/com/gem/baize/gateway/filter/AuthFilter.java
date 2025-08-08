package com.gem.baize.gateway.filter;


import com.gem.baize.common.core.constant.HTTPHeaderConstant;
import com.gem.baize.common.security.util.JwtUtils;
import com.gem.baize.gateway.enums.FilterOrder;
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
@Component
public class AuthFilter implements GlobalFilter, Ordered {
    public static final String BEARER = "Bearer ";
    private static final String SKIP_AUTH_METADATA_KEY = "skipAuth";

    @Autowired
    private static JwtUtils jwtUtils;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        Route route = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
        // 元数据标记为skipAuth的路由地址
        if (isSkipAuth(route)) {
            return chain.filter(exchange);
        }

        return authenticate(exchange, chain);
    }

    private static Mono<Void> authenticate(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String auth = request.getHeaders().getFirst(HTTPHeaderConstant.AUTHORIZATION);
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
            JwtUtils.Payload payload = jwtUtils.parsePayload(auth.replace(BEARER, ""));
            exchange.getRequest().mutate()
                    .header(HTTPHeaderConstant.TENANT_ID, payload.getSubject())
                    .header(HTTPHeaderConstant.USER_ID, payload.getUserId())
                    .header(HTTPHeaderConstant.USER_NAME, payload.getUserName())
                    .header(HTTPHeaderConstant.ROLE, payload.getRole());
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
