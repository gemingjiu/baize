package com.gem.baize.common.security.util;


import com.gem.baize.common.core.constant.CustomHttpHeaders;
import com.gem.baize.common.security.domain.dto.Payload;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 工具类 (线程安全)
 */
@Slf4j
@Component
public class JwtUtils {

    /**
     * JWT 密钥不允许硬编码默认值，必须由配置中心/环境变量注入。
     * 校验分两层：
     * <ul>
     *   <li>配置了但长度不足 256 位 → 启动即失败（一定是配错了）；</li>
     *   <li>完全没有配置 → 只告警。本类被 baize-common-security 以自动配置方式注册到所有依赖它的
     *       服务，而 baize-system / baize-gen / baize-example 只透传网关注入的请求头、并不签发或校验
     *       令牌；如果在 init() 里硬失败，这些服务会被迫配置一个它们根本不用的密钥。
     *       真正调用签发/校验时由 {@link #getSigningKey()} 抛异常，仍然 fail-closed。</li>
     * </ul>
     */
    @Value("${jwt.secret:}")
    private String jwtSecret;

    @Value("${jwt.expiration:7200000}")
    private long jwtExpirationMs;

    /**
     * HS256 要求密钥长度不少于 256 位（32 字节）
     */
    private static final int MIN_SECRET_LENGTH = 32;

    private SecretKey signingKey;

    @PostConstruct
    public void init() {
        if (StringUtils.isBlank(jwtSecret)) {
            log.warn("JWT 密钥未配置（jwt.secret / 环境变量 BAIZE_JWT_SECRET 为空）："
                    + "本服务一旦签发或校验令牌将立即失败；若该服务不需要 JWT 能力（如 baize-system 只透传请求头）可忽略本条告警");
            return;
        }
        if (jwtSecret.getBytes(StandardCharsets.UTF_8).length < MIN_SECRET_LENGTH) {
            throw new IllegalStateException("JWT 密钥长度不足：jwt.secret 至少需要 " + MIN_SECRET_LENGTH
                    + " 个字节（256 位），请通过环境变量 BAIZE_JWT_SECRET 注入足够长度的随机密钥");
        }
        this.signingKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    private SecretKey getSigningKey() {
        if (this.signingKey == null) {
            throw new IllegalStateException("JWT 密钥未配置：请设置 jwt.secret（推荐通过环境变量 BAIZE_JWT_SECRET 注入），"
                    + "禁止使用仓库中的默认密钥，服务拒绝签发/校验令牌");
        }
        return this.signingKey;
    }

    /**
     * 创建JWT令牌（默认过期时间）
     */
    public String createToken(String subject) {
        return createToken(subject, new HashMap<>());
    }

    /**
     * 创建JWT令牌（带自定义声明）
     */
    public String createToken(String subject, Map<String, Object> claims) {
        return Jwts.builder()
                .subject(subject)
                .claims(claims)
                .signWith(getSigningKey())
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .compact();
    }

    /**
     * 创建JWT令牌（带自定义声明）
     */
    public String createToken(String subject, Map<String, Object> claims,boolean neverExpire) {
        if (neverExpire) {
            return Jwts.builder()
                    .subject(subject)
                    .claims(claims)
                    .signWith(getSigningKey())
                    .compact();
        }
        return createToken(subject,claims);
    }

    /**
     * 从声明中创建JWT（必须包含 tenantId 和 userId）
     */
    public String createToken(Map<String, Object> claims) {
        validateRequiredClaims(claims);
        String subject = claims.get(CustomHttpHeaders.TENANT_ID) + ":" + claims.get(CustomHttpHeaders.USER_ID);
        return createToken(subject, claims);
    }

    public String createToken(Map<String, Object> claims,boolean neverExpire) {
        validateRequiredClaims(claims);
        String subject = claims.get(CustomHttpHeaders.TENANT_ID) + ":" + claims.get(CustomHttpHeaders.USER_ID);
        return createToken(subject, claims,neverExpire);
    }

    /**
     * 解析令牌获取 Claims
     */
    public Claims parseToken(String token) {
        if (StringUtils.isBlank(token)) {
            throw new IllegalArgumentException("Token不能为空");
        }

        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 获取指定声明
     */
    public Object getClaim(String token, String claimName) {
        return parseToken(token).get(claimName);
    }

    public String getUserId(String token) {
        return getClaimAsString(token, CustomHttpHeaders.USER_ID);
    }

    public String getTenantId(String token) {
        return getClaimAsString(token, CustomHttpHeaders.TENANT_ID);
    }

    public String getTraceId(String token) {
        return getClaimAsString(token, CustomHttpHeaders.TRACE_ID);
    }

    public String getUserName(String token) {
        return getClaimAsString(token, CustomHttpHeaders.USER_NAME);
    }

    /**
     * 解析令牌载荷（缺失的声明返回 null，而不是字符串 "null"）
     */
    public Payload parsePayload(String token) {
        Claims claims = parseToken(token);
        Payload payload = new Payload();
        payload.setSubject(claimAsString(claims, Claims.SUBJECT));
        payload.setTenantId(claimAsString(claims, CustomHttpHeaders.TENANT_ID));
        payload.setUserId(claimAsString(claims, CustomHttpHeaders.USER_ID));
        payload.setTraceId(claimAsString(claims, CustomHttpHeaders.TRACE_ID));
        payload.setUserName(claimAsString(claims, CustomHttpHeaders.USER_NAME));
        payload.setRole(claimAsString(claims, CustomHttpHeaders.ROLE));
        payload.setDataScope(claimAsString(claims, CustomHttpHeaders.DATA_SCOPE));
        return payload;
    }

    /**
     * 获取令牌剩余有效期（秒），用于登出时设置黑名单 TTL。
     * 对于无过期时间的令牌（neverExpire），按默认有效期 jwt.expiration 计算。
     */
    public long getRemainingExpirationSeconds(String token) {
        Date expiration = parseToken(token).getExpiration();
        long remainingMillis = expiration == null
                ? jwtExpirationMs
                : expiration.getTime() - System.currentTimeMillis();
        return Math.max(remainingMillis / 1000, 0L);
    }

    /**
     * 获取字符串类型的声明，缺失时返回 null
     */
    private String getClaimAsString(String token, String claimName) {
        return claimAsString(parseToken(token), claimName);
    }

    /**
     * 获取字符串类型的声明，缺失时返回 null（避免 String.valueOf(null) 产生 "null" 字符串）
     */
    private String claimAsString(Claims claims, String claimName) {
        Object value = claims.get(claimName);
        return value == null ? null : String.valueOf(value);
    }

    /**
     * 校验是否包含必须字段
     */
    private void validateRequiredClaims(Map<String, Object> claims) {
        if (claims == null ||
                !claims.containsKey(CustomHttpHeaders.USER_ID) ||
                !claims.containsKey(CustomHttpHeaders.TENANT_ID)) {
            throw new IllegalArgumentException("创建Token需要USER_ID和TENANT_ID声明");
        }
    }



}

