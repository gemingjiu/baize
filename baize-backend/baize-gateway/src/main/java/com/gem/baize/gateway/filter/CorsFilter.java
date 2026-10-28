package com.gem.baize.gateway.filter;

import com.gem.baize.gateway.enums.FilterOrder;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * CORS 预检兜底过滤器（P1-C 收敛后仅保留该职责）。
 * <p>
 * 职责划分（重要）：真正的 CORS 校验与响应头由 Spring Cloud Gateway 的
 * {@code spring.cloud.gateway.globalcors.cors-configurations} 白名单负责
 * （预检在 {@code RoutePredicateHandlerMapping} 层处理，早于任何 GlobalFilter）。
 * 本过滤器不再回显 {@code Access-Control-Allow-Origin}，避免与框架层重复/冲突。
 * </p>
 * <p>
 * 保留的唯一职责：到达此处的 OPTIONS 请求（未命中框架预检处理的情形）直接 200 放行，
 * 防止身份/租户过滤器把预检当成业务请求处理。
 * </p>
 */
@Component
public class CorsFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        var request = exchange.getRequest();

        // 处理OPTIONS预检请求
        if (request.getMethod() == HttpMethod.OPTIONS) {
            var response = exchange.getResponse();
            response.setStatusCode(HttpStatus.OK);
            return response.setComplete(); // 实际完成响应
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return FilterOrder.TRACE_ID_FILTER.getOrder() - 1;
    }
}
