package com.gem.baize.gateway.filter;

import com.gem.baize.common.core.constant.HTTPHeaderConstant;
import com.gem.baize.gateway.enums.FilterOrder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import org.apache.skywalking.apm.toolkit.trace.TraceContext;


/**
 * 链路追踪过滤器，用于在请求处理过程中添加链路追踪ID。
 */
@Component
public class TraceIdFilter implements GlobalFilter, Ordered {

    private static final String MDC_TRACE_ID_KEY = "traceId";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String traceId = request.getHeaders().getFirst(HTTPHeaderConstant.TRACE_ID);
        // 如果请求头中没有traceId，则使用Skywalking生成的traceId
        if (StringUtils.isBlank(traceId)) {
            traceId = TraceContext.traceId();
            // 如果Skywalking的traceId为空，则生成一个新的UUID作为traceId
            if (StringUtils.isBlank(traceId)) {
                traceId = java.util.UUID.randomUUID().toString();
            }
        }

        MDC.put(MDC_TRACE_ID_KEY, traceId);
        exchange.getRequest().mutate().header(HTTPHeaderConstant.TRACE_ID, traceId);
        return chain.filter(exchange).doFinally(signalType -> MDC.remove(MDC_TRACE_ID_KEY));

    }

    @Override
    public int getOrder() {
        return FilterOrder.TRACE_ID_FILTER.getOrder();
    }
}
