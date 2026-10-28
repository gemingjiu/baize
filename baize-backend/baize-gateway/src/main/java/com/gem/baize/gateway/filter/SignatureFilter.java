package com.gem.baize.gateway.filter;

import com.gem.baize.common.core.constant.CustomHttpHeaders;
import com.gem.baize.common.core.util.GatewaySignatureUtil;
import com.gem.baize.gateway.enums.FilterOrder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * 网关签名过滤器（P0-A）。
 * <p>
 * 运行顺序：在 {@code TENANT_FILTER(-800)} 之后、{@code LOGGING_FILTER(-700)} 之前，
 * 此时 {@link AuthFilter} 注入的身份头与 {@link TenantFilter} 规整后的租户头都已落到请求上，
 * 本过滤器对「最终请求」计算 HMAC 签名并写入 {@link GatewaySignatureUtil#SIGNATURE_HEADER}，
 * 下游后端据此判断身份头是否真的来自网关（而非直连后端伪造）。
 * </p>
 * <p>
 * 降级：{@code baize.gateway.signature-secret} 未配置时本过滤器不写签名头，与未启用签名时行为一致。
 * 一旦网关与后端同时配置同一密钥，防护即生效（fail-closed）。
 * </p>
 * <p>
 * 上线顺序（重要）：<b>先给网关注入密钥（开始签名，后端此时尚未校验、自动放行），
 * 再给后端注入密钥（开始校验）</b>。反之（后端先校验、网关尚未签名）会导致所有鉴权请求 401。
 * </p>
 */
@Component
public class SignatureFilter implements GlobalFilter, Ordered {

    @Value("${baize.gateway.signature-secret:}")
    private String secret;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // 预检请求不签名：OPTIONS 由网关/CORS 直接处理，不会携带业务身份头
        if ("OPTIONS".equals(request.getMethod().name())) {
            return chain.filter(exchange);
        }

        // 密钥未配置：降级为不签名（与未启用防护一致）
        if (StringUtils.isBlank(secret)) {
            return chain.filter(exchange);
        }

        Map<String, String> headers = new HashMap<>();
        request.getHeaders().forEach((k, v) -> {
            if (v != null && !v.isEmpty()) {
                headers.put(k.toLowerCase(), v.get(0));
            }
        });

        String signature = GatewaySignatureUtil.sign(
                request.getMethod().name(),
                request.getURI().getPath(),
                headers,
                secret);

        ServerHttpRequest mutatedRequest = request.mutate()
                .header(GatewaySignatureUtil.SIGNATURE_HEADER, signature)
                .build();
        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }

    @Override
    public int getOrder() {
        // 介于 TENANT_FILTER(-800) 与 LOGGING_FILTER(-700) 之间
        return -750;
    }
}
