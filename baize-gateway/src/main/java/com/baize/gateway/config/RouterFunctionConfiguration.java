package com.baize.gateway.config;

import com.baize.gateway.handler.CaptchaHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;

/**
 * 路由功能配置
 * @author gemj
 * @since 2023/12/13 21:59
 */
@Configuration
public class RouterFunctionConfiguration {

    @Autowired
    private CaptchaHandler captchaHandler;
    @Bean
    public RouterFunction routerFunction()
    {
        // 验证码功能
        return RouterFunctions.route(
                RequestPredicates.GET("/code").and(RequestPredicates.accept(MediaType.TEXT_PLAIN)),
                captchaHandler);
    }
}
