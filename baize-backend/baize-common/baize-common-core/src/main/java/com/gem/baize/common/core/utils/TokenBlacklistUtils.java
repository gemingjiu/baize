package com.gem.baize.common.core.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 访问令牌黑名单工具类
 * <p>
 * Redis key 统一由本类生成，保证认证服务（写入黑名单）与网关（查询黑名单）使用相同规则。
 */
public final class TokenBlacklistUtils {

    /**
     * 黑名单 key 前缀
     */
    public static final String BLACKLIST_KEY_PREFIX = "baize:token:blacklist:";

    private static final char[] HEX = "0123456789abcdef".toCharArray();

    private TokenBlacklistUtils() {
        throw new UnsupportedOperationException("工具类禁止实例化");
    }

    /**
     * 生成令牌在 Redis 中的黑名单 key（对令牌做 SHA-256 摘要，避免 key 过长）
     *
     * @param token 访问令牌
     * @return 黑名单 key
     */
    public static String buildBlacklistKey(String token) {
        return BLACKLIST_KEY_PREFIX + sha256Hex(token);
    }

    /**
     * SHA-256 摘要（小写十六进制）
     */
    public static String sha256Hex(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            char[] chars = new char[bytes.length * 2];
            for (int i = 0; i < bytes.length; i++) {
                int b = bytes[i] & 0xFF;
                chars[i * 2] = HEX[b >>> 4];
                chars[i * 2 + 1] = HEX[b & 0x0F];
            }
            return new String(chars);
        } catch (NoSuchAlgorithmException e) {
            // JDK 必然支持 SHA-256，理论上不会走到这里
            throw new IllegalStateException("当前 JDK 不支持 SHA-256 算法", e);
        }
    }
}
