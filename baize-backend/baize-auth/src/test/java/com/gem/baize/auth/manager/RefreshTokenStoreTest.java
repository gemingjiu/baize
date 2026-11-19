package com.gem.baize.auth.manager;

import com.gem.baize.auth.config.AuthCookieProperties;
import com.gem.baize.auth.domain.RefreshTokenRecord;
import com.gem.baize.common.core.exception.model.UnauthorizedException;
import com.gem.baize.common.security.domain.vo.IssuedRefresh;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

/**
 * T1.3：refresh 令牌存储的轮转 / 复用检测单测。
 * 用内存 Map 模拟 StringRedisTemplate 的 value/set 操作，无需真实 Redis。
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RefreshTokenStoreTest {

    private static final String TENANT = "00000000-0000-0000-0000-000000000000";
    private static final String USER = "user-1";

    @Mock
    private StringRedisTemplate redis;
    @Mock
    private ValueOperations<String, String> valueOps;
    @Mock
    private SetOperations<String, String> setOps;

    private AuthCookieProperties props;

    @InjectMocks
    private RefreshTokenStore store;

    /** key → value 的内存存储，valueOps.set/get 直接读写它。 */
    private final Map<String, String> values = new HashMap<>();
    /** familyKey → 成员哈希集合。 */
    private final Map<String, Set<String>> families = new HashMap<>();

    @BeforeEach
    void setUp() {
        props = new AuthCookieProperties();
        props.setEnabled(true);
        ReflectionUtil.setField(store, "props", props);

        when(redis.opsForValue()).thenReturn(valueOps);
        when(redis.opsForSet()).thenReturn(setOps);

        when(valueOps.get(anyString())).thenAnswer(inv -> values.get(inv.getArgument(0)));
        doAnswer(inv -> {
            values.put(inv.getArgument(0), inv.getArgument(1));
            return null;
        }).when(valueOps).set(anyString(), anyString(), any(Duration.class));
        when(setOps.add(anyString(), anyString())).thenAnswer(inv -> {
            families.computeIfAbsent(inv.getArgument(0), k -> new HashSet<>()).add(inv.getArgument(1));
            return 1L;
        });
        when(setOps.members(anyString())).thenAnswer(inv -> families.get(inv.getArgument(0)));
        // delete 同步清理内存，模拟真实吊销效果
        when(redis.delete(any(java.util.Collection.class))).thenAnswer(inv -> {
            java.util.Collection<String> keys = inv.getArgument(0);
            for (String k : keys) {
                values.remove(k);
            }
            return (long) keys.size();
        });
        when(redis.delete(anyString())).thenAnswer(inv -> {
            String key = inv.getArgument(0);
            families.remove(key);
            return true;
        });
    }

    @Test
    void issueStoresHashOnlyAndReturnsRawToken() {
        IssuedRefresh issued = store.issue(TENANT, USER, "tester");

        assertThat(issued.getRawToken()).isNotBlank();
        assertThat(issued.getCsrfToken()).isNotBlank();
        assertThat(issued.getFamilyId()).isNotBlank();
        // 内存里只应存在「哈希 key」，不存在明文 key
        assertThat(values).hasSize(1);
        assertThat(values.keySet().iterator().next()).startsWith("baize:refresh:");
        assertThat(values.values().iterator().next()).doesNotContain(issued.getRawToken());
    }

    @Test
    void consumeMarksTokenUsed() {
        IssuedRefresh issued = store.issue(TENANT, USER, "tester");
        RefreshTokenRecord consumed = store.consume(issued.getRawToken());

        assertThat(consumed).isNotNull();
        assertThat(consumed.getUserId()).isEqualTo(USER);
        assertThat(consumed.getState()).isEqualTo(RefreshTokenRecord.STATE_USED);
    }

    @Test
    void reuseOfConsumedTokenDetectsAttackAndRevokesFamily() {
        IssuedRefresh issued = store.issue(TENANT, USER, "tester");
        // 第一次消费成功
        store.consume(issued.getRawToken());

        // 同一令牌再次出现 → 判定复用，抛异常
        assertThatThrownBy(() -> store.consume(issued.getRawToken()))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("会话已被吊销");
        // 整个 family 的令牌都已被清除
        assertThat(values).isEmpty();
        assertThat(families).isEmpty();
    }

    @Test
    void rotateKeepsSameFamily() {
        IssuedRefresh issued = store.issue(TENANT, USER, "tester");
        store.consume(issued.getRawToken());
        IssuedRefresh rotated = store.issueForFamily(issued.getFamilyId(), TENANT, USER, "tester");

        assertThat(rotated.getFamilyId()).isEqualTo(issued.getFamilyId());
        assertThat(rotated.getRawToken()).isNotEqualTo(issued.getRawToken());
    }

    /** 极简反射工具：把 @InjectMocks 未覆盖的 props 字段注入。 */
    private static final class ReflectionUtil {
        static void setField(Object target, String name, Object value) {
            try {
                var f = target.getClass().getDeclaredField(name);
                f.setAccessible(true);
                f.set(target, value);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
