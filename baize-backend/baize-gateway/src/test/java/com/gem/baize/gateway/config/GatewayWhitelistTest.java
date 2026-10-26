package com.gem.baize.gateway.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 校验网关白名单能从 YAML 列表结构正确绑定（回归：@Value 无法绑定列表导致登录被拦截）。
 */
class GatewayWhitelistTest {

    private GatewayWhitelist bind(Map<String, Object> source) {
        Binder binder = new Binder(new MapConfigurationPropertySource(source));
        GatewayWhitelist properties = binder.bind("baize.gateway", Bindable.of(GatewayWhitelist.class)).orElseGet(GatewayWhitelist::new);
        return properties;
    }

    @Test
    @DisplayName("YAML 列表可以绑定为 List<String>")
    void bindsYamlList() {
        GatewayWhitelist properties = bind(Map.of(
                "baize.gateway.whitelist", List.of("/api/v1/auth/**", "/swagger-ui.html")));

        assertFalse(properties.isEmpty(), "白名单必须绑定成功，否则匿名接口会被拦截");
        assertTrue(properties.getWhitelist().contains("/api/v1/auth/**"));
    }

    @Test
    @DisplayName("前缀通配与精确匹配")
    void matchesPaths() {
        GatewayWhitelist properties = bind(Map.of(
                "baize.gateway.whitelist", List.of("/api/v1/auth/**", "/swagger-ui.html", "/v3/api-docs/**")));

        assertTrue(properties.matches("/api/v1/auth/login"), "登录接口应命中白名单");
        assertTrue(properties.matches("/api/v1/auth/refresh"));
        assertTrue(properties.matches("/swagger-ui.html"), "精确匹配应生效");
        assertTrue(properties.matches("/v3/api-docs/swagger-config"));
        assertFalse(properties.matches("/api/v1/system/user/1"), "业务接口不应命中白名单");
        assertFalse(properties.matches("/swagger-ui.htmlx"), "精确匹配不应退化为前缀匹配");
    }

    @Test
    @DisplayName("未配置白名单时不放行任何路径")
    void emptyWhitelistAllowsNothing() {
        GatewayWhitelist properties = bind(Map.of());

        assertTrue(properties.isEmpty());
        assertFalse(properties.matches("/api/v1/auth/login"));
    }
}
