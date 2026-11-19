package com.gem.baize.auth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * T1.3 登录态 HttpOnly Cookie 特性开关与参数。
 *
 * <p>默认 {@code enabled=false}：保持原有「JSON 返回 access/refresh + 前端 localStorage + Bearer」行为，
 * 认证链路零变更；置 true 后启用 refresh Cookie 下发、服务端哈希存储、family 轮转与 CSRF 双重提交。</p>
 *
 * <p>配置前缀：{@code baize.security.token-cookie}（见 baize-auth-dev.yml）。</p>
 */
@Data
@Component
@ConfigurationProperties(prefix = "baize.security.token-cookie")
public class AuthCookieProperties {

    /** 总开关，默认关闭。 */
    private boolean enabled = false;

    /** 存放 refresh token 的 HttpOnly Cookie 名称。 */
    private String refreshCookieName = "baize_refresh";

    /** 供 JS 读取、用于 CSRF 双重提交的 Cookie 名称（非 HttpOnly）。 */
    private String csrfCookieName = "XSRF-TOKEN";

    /** CSRF 请求头名称，值需与 csrfCookieName 的 Cookie 值一致。 */
    private String csrfHeaderName = "X-XSRF-TOKEN";

    /** Cookie Path，与 auth 服务 context-path 对齐：/api/v1/auth。 */
    private String path = "/api/v1/auth";

    /** dev 环境 http 下须为 false；生产 https 应置 true。 */
    private boolean secure = false;

    /** 同上，遵循 ADR-0002。 */
    private String sameSite = "Lax";

    /** refresh token 有效期（秒），默认 7 天。 */
    private long ttlSeconds = 7L * 24 * 60 * 60;
}
