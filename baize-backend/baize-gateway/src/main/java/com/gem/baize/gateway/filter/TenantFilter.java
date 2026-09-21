package com.gem.baize.gateway.filter;

import com.gem.baize.common.core.constant.CustomHttpHeaders;
import com.gem.baize.gateway.enums.FilterOrder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 租户校验过滤器：校验租户头，缺失时注入默认租户（0=master），白名单路径不强制校验
 */
@Component
public class TenantFilter implements GlobalFilter, Ordered {

    /** 默认 master 租户 ID */
    private static final String DEFAULT_TENANT_ID = "0";

    @Value("${baize.gateway.whitelist:}")
    private List<String> whitelist;

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
            if (isWhitelist(path)) {
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

    @Override
    public int getOrder() {
        return FilterOrder.TENANT_FILTER.getOrder();
    }
}
