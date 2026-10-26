package com.gem.baize.gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 网关白名单配置。
 * <p>
 * 使用 {@code @ConfigurationProperties} 而不是 {@code @Value} 绑定：{@code baize.gateway.whitelist}
 * 在 YAML 中是列表结构，{@code @Value} 只能读取单值（逗号分隔字符串），无法读取 YAML 序列，
 * 会导致白名单始终为空——登录等匿名接口会被认证/租户过滤器拦截。
 */
@Component
@ConfigurationProperties(prefix = "baize.gateway")
public class GatewayWhitelist {

    /**
     * 匿名放行路径，支持 {@code /**} 前缀匹配
     */
    private List<String> whitelist = new ArrayList<>();

    public List<String> getWhitelist() {
        return whitelist;
    }

    public void setWhitelist(List<String> whitelist) {
        this.whitelist = whitelist == null ? new ArrayList<>() : whitelist;
    }

    public boolean isEmpty() {
        return whitelist.isEmpty();
    }

    /**
     * 判断请求路径是否命中白名单。
     *
     * @param path 请求路径，如 {@code /api/v1/auth/login}
     * @return 命中返回 true
     */
    public boolean matches(String path) {
        return GatewayPathMatcher.matches(whitelist, path);
    }
}
