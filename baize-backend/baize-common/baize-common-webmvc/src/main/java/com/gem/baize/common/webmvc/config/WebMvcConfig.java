package com.gem.baize.common.webmvc.config;

import com.gem.baize.common.core.annotation.InnerAuth;
import com.gem.baize.common.core.util.GatewaySignatureUtil;
import com.gem.baize.common.webmvc.interceptor.GatewaySignatureInterceptor;
import com.gem.baize.common.webmvc.interceptor.HeaderInterceptor;
import com.gem.baize.common.webmvc.interceptor.InnerAuthInterceptor;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * WebMvc拦截器配置
 * <p>
 * 本类通过 META-INF/spring/...AutoConfiguration.imports 注册为配置类（无 @Configuration 注解），
 * 请勿改为 @Component，否则会破坏现有自动装配机制。
 */
public class WebMvcConfig implements WebMvcConfigurer, EnvironmentAware {
    /**
     * 不需要拦截地址
     */
    public static final String[] excludeUrls = {"/login", "/logout", "/refresh"};

    private Environment environment;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 顺序：签名校验(-11) → 身份头读取(-10) → 内部接口鉴权(-9)
        registry.addInterceptor(getGatewaySignatureInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(excludeUrls)
                .order(-11);
        registry.addInterceptor(getHeaderInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(excludeUrls)
                .order(-10);
        // 内部接口鉴权：只拦截标注 @InnerAuth 的方法
        registry.addInterceptor(getInnerAuthInterceptor())
                .addPathPatterns("/**")
                .order(-9);
    }

    /**
     * 自定义请求头拦截器
     */
    public HeaderInterceptor getHeaderInterceptor() {
        return new HeaderInterceptor();
    }

    /**
     * 网关签名校验拦截器（P0-A）：校验身份头是否由网关签发，防直连后端伪造。
     * 密钥从 baize.gateway.signature-secret 读取，未配置时不校验（降级放行）。
     */
    public GatewaySignatureInterceptor getGatewaySignatureInterceptor() {
        String secret = environment == null ? "" : environment.getProperty(GatewaySignatureUtil.SECRET_PROPERTY, "");
        return new GatewaySignatureInterceptor(secret);
    }

    /**
     * 内部接口鉴权拦截器（内部调用令牌从配置 baize.inner.token 读取）
     */
    public InnerAuthInterceptor getInnerAuthInterceptor() {
        String innerToken = environment == null ? "" : environment.getProperty(InnerAuth.TOKEN_PROPERTY, "");
        return new InnerAuthInterceptor(innerToken);
    }
}
