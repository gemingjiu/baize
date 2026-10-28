package com.gem.baize.common.webmvc.interceptor;

import com.gem.baize.common.core.annotation.InnerAuth;
import com.gem.baize.common.core.exception.model.ForbiddenException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 内部接口鉴权拦截器（P1-E fail-closed）
 * <p>
 * 只拦截标注了 {@link InnerAuth} 的 Controller 方法（方法或类级别），校验请求头
 * {@link InnerAuth#HEADER_NAME} 与配置项 {@link InnerAuth#TOKEN_PROPERTY} 是否一致。
 * <p>
 * 安全策略：未配置令牌时对 @InnerAuth 端点<b>一律拒绝</b>（fail-closed），
 * 而不是放行——空令牌放行等于内部接口裸奔，攻击者直连后端即可调用
 * getPasswordByUsername 等敏感端点。各服务的共享配置 application-dev.yml
 * 已统一配置 baize.inner.token，Feign 侧 InnerAuthFeignConfig 自动附加令牌，
 * 正常内部调用不受影响。
 */
@Slf4j
public class InnerAuthInterceptor implements HandlerInterceptor {

    private final String innerToken;

    public InnerAuthInterceptor(String innerToken) {
        this.innerToken = innerToken;
        if (!StringUtils.hasText(innerToken)) {
            log.error("未配置 {}，标注 @InnerAuth 的内部接口将全部拒绝访问（fail-closed）；"
                            + "请为每个服务配置该令牌，且与 Feign 调用方保持一致",
                    InnerAuth.TOKEN_PROPERTY);
        }
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }
        // 只处理标注 @InnerAuth 的接口
        if (!handlerMethod.hasMethodAnnotation(InnerAuth.class)
                && !handlerMethod.getBeanType().isAnnotationPresent(InnerAuth.class)) {
            return true;
        }
        // P1-E fail-closed：未配置内部令牌时拒绝访问 @InnerAuth 端点，而非放行
        if (!StringUtils.hasText(innerToken)) {
            log.error("拒绝内部接口调用: uri={}，原因：服务端未配置 {}",
                    request.getRequestURI(), InnerAuth.TOKEN_PROPERTY);
            throw new ForbiddenException("内部接口未启用（服务端缺少内部令牌配置）");
        }
        String token = request.getHeader(InnerAuth.HEADER_NAME);
        if (!innerToken.equals(token)) {
            log.warn("内部接口鉴权失败: uri={}, remote={}", request.getRequestURI(), request.getRemoteAddr());
            throw new ForbiddenException("非法内部调用");
        }
        return true;
    }
}
