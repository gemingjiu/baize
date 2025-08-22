package com.gem.baize.common.security.util;


import com.gem.baize.common.core.constant.CustomHttpHeaders;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 工具类 (线程安全)
 */
@Component
public class JwtUtils {

    @Value("${jwt.secret:baize-a732659e-45e2-47a8-89ab-6c4b8af98bab}")
    private String jwtSecret;

    @Value("${jwt.expiration:7200000}")
    private long jwtExpirationMs;

    private SecretKey signingKey;

    @PostConstruct
    public void init() {
        if (StringUtils.isBlank(jwtSecret)) {
            throw new IllegalArgumentException("jwt.secret 配置不能为空");
        }
        this.signingKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    private SecretKey getSigningKey() {
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
     * 从声明中创建JWT（必须包含 tenantId 和 userId）
     */
    public String createToken(Map<String, Object> claims) {
        validateRequiredClaims(claims);
        String subject = claims.get(CustomHttpHeaders.TENANT_ID) + ":" + claims.get(CustomHttpHeaders.USER_ID);
        return createToken(subject, claims);
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
        return String.valueOf(getClaim(token, CustomHttpHeaders.USER_ID));
    }

    public String getTenantId(String token) {
        return String.valueOf(getClaim(token, CustomHttpHeaders.TENANT_ID));
    }

    public String getTraceId(String token) {
        return String.valueOf(getClaim(token, CustomHttpHeaders.TRACE_ID));
    }

    public String getUserName(String token) {
        return String.valueOf(getClaim(token, CustomHttpHeaders.USER_NAME));
    }

    public Payload parsePayload(String token) {
        Claims claims = parseToken(token);
        Payload payload = new Payload();
        payload.setSubject(String.valueOf(claims.getSubject()));
        payload.setTenantId(String.valueOf(claims.get(CustomHttpHeaders.TENANT_ID)));
        payload.setUserId(String.valueOf(claims.get(CustomHttpHeaders.USER_ID)));
        payload.setTraceId(String.valueOf(claims.get(CustomHttpHeaders.TRACE_ID)));
        payload.setUserName(String.valueOf(claims.get(CustomHttpHeaders.USER_NAME)));
        payload.setRole(String.valueOf(claims.get(CustomHttpHeaders.ROLE)));
        return payload;
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


    @Data
    public static class Payload {
        private Long tenantId;
        private String userId;
        private String traceId;
        private String subject;
        private String userName;
        private String role;
    }
}

