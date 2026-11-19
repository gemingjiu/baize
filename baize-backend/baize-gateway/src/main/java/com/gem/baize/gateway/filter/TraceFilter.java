package com.gem.baize.gateway.filter;

import com.gem.baize.common.core.constant.CustomHttpHeaders;
import com.gem.baize.gateway.enums.FilterOrder;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 链路追踪过滤器，用于在请求处理过程中添加链路追踪ID。
 *
 * <p>追踪实现由 SkyWalking 迁移至 OpenTelemetry（见 docs/adr/0003-observability-otel-lgtm.md）。
 * traceId 取值优先级：
 * <ol>
 *   <li>上游透传的 {@code X-Trace-Id} 请求头；</li>
 *   <li>当前 OTel Span 的 traceId（Micrometer Tracing 自动为请求创建 span）；</li>
 *   <li>兜底生成 UUID（无 Collector 且未启用采样时保证日志仍可串联）。</li>
 * </ol>
 * 取值后写入 logback MDC 与下游请求头，使 gateway/auth/system 日志共用同一 traceId。
 */
@Component
public class TraceFilter implements GlobalFilter, Ordered {

    private static final String MDC_TRACE_ID_KEY = "traceId";
    private static final String MDC_SPAN_ID_KEY = "spanId";
    /** OTel 无效 traceId 的占位值（全 0）。 */
    private static final String INVALID_TRACE_ID = "00000000000000000000000000000000";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String traceId = request.getHeaders().getFirst(CustomHttpHeaders.TRACE_ID);
        if (StringUtils.isBlank(traceId)) {
            traceId = currentOtelTraceId();
            if (StringUtils.isBlank(traceId)) {
                traceId = java.util.UUID.randomUUID().toString();
            }
        }

        MDC.put(MDC_TRACE_ID_KEY, traceId);
        String spanId = currentOtelSpanId();
        if (StringUtils.isNotBlank(spanId)) {
            MDC.put(MDC_SPAN_ID_KEY, spanId);
        }
        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                .header(CustomHttpHeaders.TRACE_ID, traceId)
                .build();
        return chain.filter(exchange.mutate().request(mutatedRequest).build())
                .doFinally(signalType -> {
                    MDC.remove(MDC_TRACE_ID_KEY);
                    MDC.remove(MDC_SPAN_ID_KEY);
                });

    }

    /**
     * 读取当前 OTel Span 的 traceId，无有效 span 时返回 null。
     */
    private String currentOtelTraceId() {
        SpanContext spanContext = Span.current().getSpanContext();
        if (spanContext.isValid()) {
            String traceId = spanContext.getTraceId();
            if (StringUtils.isNotBlank(traceId) && !INVALID_TRACE_ID.equals(traceId)) {
                return traceId;
            }
        }
        return null;
    }

    /**
     * 读取当前 OTel Span 的 spanId，无有效 span 时返回 null。
     */
    private String currentOtelSpanId() {
        SpanContext spanContext = Span.current().getSpanContext();
        if (spanContext.isValid()) {
            return spanContext.getSpanId();
        }
        return null;
    }

    @Override
    public int getOrder() {
        return FilterOrder.TRACE_ID_FILTER.getOrder();
    }
}
