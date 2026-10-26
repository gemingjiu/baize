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
 * 内部接口鉴权拦截器
 * <p>
 * 只拦截标注了 {@link InnerAuth} 的 Controller 方法（方法或类级别），校验请求头
 * {@link InnerAuth#HEADER_NAME} 与配置项 {@link InnerAuth#TOKEN_PROPERTY} 是否一致。
 * 配置项为空表示不校验（仅告警），用于兼容未开启内部鉴权的开发环境。
 */
@Slf4j
public class InnerAuthInterceptor implements HandlerInterceptor {

    private final String innerToken;

    public InnerAuthInterceptor(String innerToken) {
        this.innerToken = innerToken;
        if (!StringUtils.hasText(innerToken)) {
            log.warn("未配置 {}，标注 @InnerAuth 的内部接口将不校验 {}，请仅在开发环境使用",
                    InnerAuth.TOKEN_PROPERTY, InnerAuth.HEADER_NAME);
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
        // 未配置内部令牌时不校验，避免破坏本地开发联调
        if (!StringUtils.hasText(innerToken)) {
            return true;
        }
        String token = request.getHeader(InnerAuth.HEADER_NAME);
        if (!innerToken.equals(token)) {
            log.warn("内部接口鉴权失败: uri={}, remote={}", request.getRequestURI(), request.getRemoteAddr());
            throw new ForbiddenException("非法内部调用");
        }
        return true;
    }
}
