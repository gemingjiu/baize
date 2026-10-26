package com.gem.baize.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 配置
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * 密码编码器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 安全过滤链配置
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 禁用 CSRF（认证服务通常不需要 CSRF 保护）
                .csrf(AbstractHttpConfigurer::disable)
                // 禁用 CORS 预检检查
                .cors(AbstractHttpConfigurer::disable)
                // 配置请求授权
                .authorizeHttpRequests(auth -> auth
                        // 登录、登出、刷新令牌、切换租户接口允许匿名访问。
                        // 这些接口都在网关 skipAuth 路由下，到达本服务时没有网关注入的用户上下文，
                        // 身份只能由请求体 / Authorization 头自己携带（切换租户在 Service 层校验归属）。
                        .requestMatchers("/auth/login", "/auth/logout", "/auth/refresh", "/auth/switchTenant").permitAll()
                        // 错误页必须放行，否则异常转发到 /error 时会再次被拦截，最终返回 403 空响应体
                        .requestMatchers("/error").permitAll()
                        // Swagger API文档相关路径允许匿名访问
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        // 其他所有请求需要认证
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}
