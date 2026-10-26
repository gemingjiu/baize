package com.gem.baize.gateway.config;

import java.util.List;

/**
 * 网关路径匹配工具。
 * <p>
 * 白名单与黑名单共用同一套匹配语义，避免两处规则出现分歧：
 * 精确匹配，或以 {@code /**} 结尾的前缀匹配。
 */
public final class GatewayPathMatcher {

    private static final String WILDCARD_SUFFIX = "/**";

    private GatewayPathMatcher() {
    }

    /**
     * 判断请求路径是否命中给定的模式列表。
     *
     * @param patterns 模式列表，如 {@code /api/v1/auth/login}、{@code /api/v1/auth/**}
     * @param path     请求路径，如 {@code /api/v1/auth/login}
     * @return 命中返回 true
     */
    public static boolean matches(List<String> patterns, String path) {
        if (path == null || patterns == null || patterns.isEmpty()) {
            return false;
        }
        for (String pattern : patterns) {
            if (pattern == null || pattern.isBlank()) {
                continue;
            }
            String trimmed = pattern.trim();
            if (trimmed.endsWith(WILDCARD_SUFFIX)) {
                String prefix = trimmed.substring(0, trimmed.length() - WILDCARD_SUFFIX.length());
                if (path.startsWith(prefix)) {
                    return true;
                }
            } else if (path.equals(trimmed)) {
                return true;
            }
        }
        return false;
    }
}
