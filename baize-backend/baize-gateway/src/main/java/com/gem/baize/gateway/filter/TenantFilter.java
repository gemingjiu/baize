package com.gem.baize.gateway.filter;

import com.gem.baize.common.core.constant.CustomHttpHeaders;
import com.gem.baize.gateway.config.GatewayWhitelist;
import com.gem.baize.gateway.enums.FilterOrder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 租户校验过滤器：校验租户头，缺失时注入默认租户（nil UUID=平台租户），白名单路径不强制校验
 */
@Component
public class TenantFilter implements GlobalFilter, Ordered {

    /** 默认平台租户 ID（nil UUID） */
    private static final String DEFAULT_TENANT_ID = "00000000-0000-0000-0000-000000000000";

    @Autowired
    private GatewayWhitelist gatewayWhitelist;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // OPTIONS 预检直接放行
        if ("OPTIONS".equals(request.getMethod().name())) {
            return chain.filter(exchange);
        }

        String tenantId = request.getHeaders().getFirst(CustomHttpHeaders.TENANT_ID);
        if (StringUtils.isBlank(tenantId)) {
            // 白名单（如登录）注入默认租户，其余请求强制校验
            if (gatewayWhitelist.matches(path)) {
                tenantId = DEFAULT_TENANT_ID;
            } else {
                exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
                return exchange.getResponse().setComplete();
            }
        }

        ServerHttpRequest mutatedRequest = request.mutate()
                .header(CustomHttpHeaders.TENANT_ID, tenantId)
                .build();
        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }

    @Override
    public int getOrder() {
        return FilterOrder.TENANT_FILTER.getOrder();
    }
}
