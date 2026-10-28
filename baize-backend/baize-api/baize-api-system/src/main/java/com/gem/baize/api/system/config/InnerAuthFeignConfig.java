package com.gem.baize.api.system.config;

import com.gem.baize.common.core.annotation.InnerAuth;
import feign.RequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

/**
 * 内部调用鉴权配置
 * <p>
 * 为所有 Feign 请求统一追加内部调用令牌请求头，配合服务端的 @InnerAuth 注解实现内部端点隔离。
 * 令牌从配置 baize.inner.token 读取，未配置时不追加（服务端对 @InnerAuth 端点 fail-closed 拒绝）。
 */
@Slf4j
@Configuration
public class InnerAuthFeignConfig {

    @Value("${baize.inner.token:}")
    private String innerToken;

    @Bean
    public RequestInterceptor innerAuthRequestInterceptor() {
        if (!StringUtils.hasText(innerToken)) {
            log.error("未配置 baize.inner.token，Feign 内部调用不会携带 {} 请求头，"
                    + "服务端 @InnerAuth 端点将全部拒绝（fail-closed）", InnerAuth.HEADER_NAME);
        }
        return template -> {
            if (StringUtils.hasText(innerToken)) {
                template.header(InnerAuth.HEADER_NAME, innerToken);
            }
        };
    }
}
