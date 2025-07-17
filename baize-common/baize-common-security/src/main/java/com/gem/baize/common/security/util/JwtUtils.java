package com.gem.baize.common.security.util;

import com.gem.baize.common.core.constant.HTTPHeaderConstant;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import java.util.Map;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;

/**
 * JWT 工具类 (线程安全)
 */
public final class JwtUtils {
    // 使用更安全的密钥生成方式
    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(
            "baize-a732659e-45e2-47a8-89ab-6c4b8af98bab".getBytes(StandardCharsets.UTF_8)
    );

    // 默认过期时间(2小时)
    private static final long DEFAULT_EXPIRATION_HOURS = 2;

    private JwtUtils() {
        throw new UnsupportedOperationException("工具类不允许实例化");
    }

    /**
     * 创建JWT令牌(使用默认过期时间)
     *
     * @param subject 主题(通常为用户ID)
     * @return JWT令牌
     */
    public static String createToken(String subject) {
        return createToken(subject, new HashMap<>());
    }

    /**
     * 创建JWT令牌(使用默认过期时间)
     *
     * @param subject 主题
     * @param claims  自定义声明
     * @return JWT令牌
     */
    public static String createToken(String subject, Map<String, Object> claims) {
        return createToken(subject, claims, DEFAULT_EXPIRATION_HOURS);
    }

    /**
     * 创建JWT令牌
     *
     * @param subject         主题
     * @param claims          自定义声明
     * @param expirationHours 过期时间(小时)
     * @return JWT令牌
     */
    public static String createToken(String subject, Map<String, Object> claims, long expirationHours) {
        Instant now = Instant.now();

        return Jwts.builder()
                .setSubject(subject)
                .addClaims(claims)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(expirationHours, ChronoUnit.HOURS)))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    /**
     * 从声明创建JWT令牌(自动提取subject)
     *
     * @param claims 必须包含USER_ID和TENANT_ID
     * @return JWT令牌
     * @throws IllegalArgumentException 如果claims不完整
     */
    public static String createToken(Map<String, Object> claims) {
        validateRequiredClaims(claims);

        String subject = claims.get(HTTPHeaderConstant.TENANT_ID) + ":" +
                claims.get(HTTPHeaderConstant.USER_ID);

        return createToken(subject, claims);
    }

    /**
     * 解析JWT令牌
     *
     * @param token JWT令牌
     * @return 声明内容
     * @throws io.jsonwebtoken.JwtException 如果令牌无效
     */
    public static Claims parseToken(String token) {
        if (StringUtils.isBlank(token)) {
            throw new IllegalArgumentException("Token不能为空");
        }

        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 从令牌获取用户ID
     *
     * @param token JWT令牌
     * @return 用户ID
     */
    public static String getUserId(String token) {
        return getClaim(token, HTTPHeaderConstant.USER_ID).toString();
    }

    /**
     * 从令牌获取租户ID
     *
     * @param token JWT令牌
     * @return 租户ID
     */
    public static String getTenantId(String token) {
        return getClaim(token, HTTPHeaderConstant.TENANT_ID).toString();
    }

    /**
     * 从令牌获取追踪ID
     *
     * @param token JWT令牌
     * @return 追踪ID
     */
    public static String getTraceId(String token) {
        return getClaim(token, HTTPHeaderConstant.TRACE_ID).toString();
    }

    /**
     * 从令牌获取用户名
     *
     * @param token JWT令牌
     * @return 用户名
     */
    public static String getUsername(String token) {
        return getClaim(token, HTTPHeaderConstant.USER_NAME).toString();
    }

    /**
     * 获取指定声明
     *
     * @param token     JWT令牌
     * @param claimName 声明名称
     * @return 声明值
     */
    public static Object getClaim(String token, String claimName) {
        return parseToken(token).get(claimName);
    }

    /**
     * 验证必需声明是否存在
     *
     * @param claims 声明Map
     * @throws IllegalArgumentException 如果缺少必需声明
     */
    private static void validateRequiredClaims(Map<String, Object> claims) {
        if (claims == null ||
                !claims.containsKey(HTTPHeaderConstant.USER_ID) ||
                !claims.containsKey(HTTPHeaderConstant.TENANT_ID)) {
            throw new IllegalArgumentException("创建Token需要USER_ID和TENANT_ID声明");
        }
    }
}
