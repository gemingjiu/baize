package com.gem.baize.common.core.annotation;

import java.lang.annotation.*;

/**
 * 内部接口鉴权注解
 * <p>
 * 标注在仅允许服务间内部调用的接口上，由 InnerAuthInterceptor 校验请求头
 * {@link #HEADER_NAME} 与配置项 {@link #TOKEN_PROPERTY} 是否一致。
 * 配置项为空时服务端对标注该注解的端点一律拒绝（fail-closed，P1-E）。
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface InnerAuth {

    /**
     * 内部调用令牌请求头
     */
    String HEADER_NAME = "X-Inner-Token";

    /**
     * 内部调用令牌配置项
     */
    String TOKEN_PROPERTY = "baize.inner.token";
}
