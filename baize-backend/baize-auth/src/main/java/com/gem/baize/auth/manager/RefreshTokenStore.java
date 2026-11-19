package com.gem.baize.auth.manager;

import com.gem.baize.auth.config.AuthCookieProperties;
import com.gem.baize.auth.domain.RefreshTokenRecord;
import com.gem.baize.common.core.exception.model.UnauthorizedException;
import com.gem.baize.common.security.domain.vo.IssuedRefresh;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Base64;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * T1.3 刷新令牌存储（feature flag 开启时生效）。
 *
 * <ul>
 *   <li>明文 refresh 仅经 HttpOnly Cookie 下发；Redis 只存 SHA-256 哈希作为 key、记录作为 value。</li>
 *   <li>每次刷新走 family 轮转链：旧令牌标记 used，签发同家族新令牌；used 令牌再次出现判定为复用，
 *       立即吊销整个 family（RFC 9700 / 10017）。</li>
 * </ul>
 */
@Slf4j
@Component
public class RefreshTokenStore {

    private static final String HASH_PREFIX = "baize:refresh:";
    private static final String FAMILY_PREFIX = "baize:refresh:family:";
    private static final SecureRandom RANDOM = new SecureRandom();

    @Autowired
    private StringRedisTemplate redis;

    @Autowired
    private AuthCookieProperties props;

    /**
     * 新会话：生成一个全新 family 的首枚 refresh token。
     */
    public IssuedRefresh issue(String tenantId, String userId, String username) {
        return issueForFamily(UUID.randomUUID().toString(), tenantId, userId, username);
    }

    /**
     * 轮转：在当前 family 内签发新令牌（供 refresh 使用）。
     */
    public IssuedRefresh issueForFamily(String familyId, String tenantId, String userId, String username) {
        String rawToken = secureToken();
        String csrfToken = secureToken();
        String hash = sha256(rawToken);
        long expiresAt = System.currentTimeMillis() + props.getTtlSeconds() * 1000L;

        RefreshTokenRecord record = new RefreshTokenRecord();
        record.setFamilyId(familyId);
        record.setTenantId(tenantId);
        record.setUserId(userId);
        record.setUsername(username);
        record.setState(RefreshTokenRecord.STATE_ACTIVE);
        record.setExpiresAt(expiresAt);

        Duration ttl = Duration.ofSeconds(props.getTtlSeconds());
        redis.opsForValue().set(HASH_PREFIX + hash, record.encode(), ttl);
        String familyKey = FAMILY_PREFIX + familyId;
        redis.opsForSet().add(familyKey, hash);
        redis.expire(familyKey, ttl);
        return new IssuedRefresh(rawToken, familyId, expiresAt, csrfToken);
    }

    /**
     * 校验并消费 refresh token：标记 used；若为 used 令牌（复用攻击）则吊销整个 family。
     *
     * @return 被消费令牌的记录（含 familyId / tenantId / userId / username），供调用方校验身份并重签
     */
    public RefreshTokenRecord consume(String rawToken) {
        if (!StringUtils.hasText(rawToken)) {
            throw new UnauthorizedException("刷新令牌无效或已过期");
        }
        String hash = sha256(rawToken);
        String value = redis.opsForValue().get(HASH_PREFIX + hash);
        RefreshTokenRecord record = RefreshTokenRecord.decode(value);
        if (record == null) {
            throw new UnauthorizedException("刷新令牌无效或已过期");
        }
        long now = System.currentTimeMillis();
        if (record.getExpiresAt() <= now) {
            throw new UnauthorizedException("刷新令牌无效或已过期");
        }
        if (RefreshTokenRecord.STATE_USED.equals(record.getState())) {
            // 复用检测：used 令牌再次出现 → 整族吊销
            log.warn("检测到 refresh token 复用，吊销整个 family: familyId={}", record.getFamilyId());
            revokeFamily(record.getFamilyId());
            throw new UnauthorizedException("刷新令牌已使用，会话已被吊销，请重新登录");
        }
        // 标记 used，TTL 收敛到令牌剩余寿命
        record.setState(RefreshTokenRecord.STATE_USED);
        redis.opsForValue().set(HASH_PREFIX + hash, record.encode(),
                Duration.ofMillis(record.getExpiresAt() - now));
        return record;
    }

    /**
     * 登出：按明文 refresh token 吊销其整个 family。
     */
    public void revokeByToken(String rawToken) {
        if (!StringUtils.hasText(rawToken)) {
            return;
        }
        String value = redis.opsForValue().get(HASH_PREFIX + sha256(rawToken));
        RefreshTokenRecord record = RefreshTokenRecord.decode(value);
        if (record != null) {
            revokeFamily(record.getFamilyId());
        }
    }

    /**
     * 吊销整个 family：删除族内所有令牌哈希与族集合。
     */
    public void revokeFamily(String familyId) {
        if (!StringUtils.hasText(familyId)) {
            return;
        }
        String familyKey = FAMILY_PREFIX + familyId;
        Set<String> hashes = redis.opsForSet().members(familyKey);
        if (!CollectionUtils.isEmpty(hashes)) {
            List<String> hashKeys = hashes.stream().map(h -> HASH_PREFIX + h).toList();
            redis.delete(hashKeys);
        }
        redis.delete(familyKey);
    }

    private static String secureToken() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private static String sha256(String raw) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(raw.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 不可用", e);
        }
    }
}
