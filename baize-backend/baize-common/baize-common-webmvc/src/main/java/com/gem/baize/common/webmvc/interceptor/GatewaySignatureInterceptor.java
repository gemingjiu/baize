package com.gem.baize.common.webmvc.interceptor;

import com.gem.baize.common.core.util.GatewaySignatureUtil;
import com.gem.baize.common.core.exception.model.ForbiddenException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 网关签名校验拦截器（P0-A）。
 * <p>
 * 与网关 {@code SignatureFilter} 配对：后端在信任身份头之前，先校验
 * {@link GatewaySignatureUtil#SIGNATURE_HEADER} 签名是否由网关生成（密钥仅网关与后端共享）。
 * </p>
 * <p>
 * 判定逻辑（区分两类请求）：
 * <ul>
 *   <li>携带身份头（来自网关/前端透传）→ 必须附带合法签名，否则 403（防直连后端伪造租户/用户）；</li>
 *   <li>未携带身份头（匿名请求、内部 Feign 调用 {@code @InnerAuth}，Feign 不会转发网关身份头）→ 放行；
 *       内部调用由 {@link InnerAuthInterceptor} 另行保护。</li>
 * </ul>
 * </p>
 * <p>
 * 降级：{@code baize.gateway.signature-secret} 未配置时 {@code verify} 返回 false，但本拦截器对
 * 「无身份头」的请求直接放行，对「有身份头但降级」的场景也放行（避免密钥未配置时整站 401）。
 * 只有「已配置密钥」且「有身份头但签名不合法」才拒绝（fail-closed）。
 * </p>
 */
public class GatewaySignatureInterceptor implements HandlerInterceptor {

    private final String secret;

    public GatewaySignatureInterceptor(@Nullable String secret) {
        this.secret = secret;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        Map<String, String> headers = toMap(request);

        // 没有身份头：匿名 / 内部 Feign 调用，放行（由 InnerAuth 等其他机制保护）
        if (!GatewaySignatureUtil.hasIdentityHeaders(headers)) {
            return true;
        }

        // 配置未下发密钥：降级放行（与网关未签名时一致），仅告警
        if (!StringUtils.hasText(secret)) {
            return true;
        }

        String signature = request.getHeader(GatewaySignatureUtil.SIGNATURE_HEADER);
        boolean ok = GatewaySignatureUtil.verify(
                request.getMethod(),
                request.getRequestURI(),
                headers,
                signature,
                secret);

        if (!ok) {
            throw new ForbiddenException("请求身份头未通过网关签名校验，疑似伪造（直连后端绕过网关）");
        }
        return true;
    }

    private Map<String, String> toMap(HttpServletRequest request) {
        Map<String, String> map = new HashMap<>();
        Enumeration<String> names = request.getHeaderNames();
        if (names != null) {
            while (names.hasMoreElements()) {
                String name = names.nextElement();
                String value = request.getHeader(name);
                if (value != null) {
                    map.put(name, value);
                }
            }
        }
        return map;
    }
}
