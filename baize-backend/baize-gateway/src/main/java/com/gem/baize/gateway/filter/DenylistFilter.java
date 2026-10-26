package com.gem.baize.gateway.filter;

import com.gem.baize.gateway.config.GatewayDenylist;
import com.gem.baize.gateway.enums.FilterOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

/**
 * 路径黑名单过滤器。
 * <p>
 * 排在认证过滤器之前（-1100），命中 {@code baize.gateway.denylist} 的路径直接 403，
 * 不再经过认证、租户与限流逻辑。用于把仅供服务间调用的内部接口挡在网关之外。
 */
@Slf4j
@Component
public class DenylistFilter implements GlobalFilter, Ordered {

    /**
     * 与 ApiResult 的字段保持一致，便于前端统一处理
     */
    private static final String DENIED_BODY =
            "{\"success\":false,\"errorCode\":403,\"errorMessage\":\"接口不允许通过网关访问\"}";

    @Autowired
    private GatewayDenylist gatewayDenylist;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        if (!gatewayDenylist.matches(path)) {
            return chain.filter(exchange);
        }
        InetSocketAddress remoteAddress = request.getRemoteAddress();
        log.warn("命中网关黑名单，拒绝访问: path={}, remote={}", path,
                remoteAddress == null ? "unknown" : remoteAddress.toString());
        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        DataBuffer buffer = exchange.getResponse().bufferFactory()
                .wrap(DENIED_BODY.getBytes(StandardCharsets.UTF_8));
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return FilterOrder.DENYLIST_FILTER.getOrder();
    }
}
