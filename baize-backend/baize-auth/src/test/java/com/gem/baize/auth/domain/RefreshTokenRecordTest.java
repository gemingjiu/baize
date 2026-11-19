package com.gem.baize.auth.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * T1.3：refresh 记录编解码单测（无中间件，回归 '|' 分隔符正则转义 bug）。
 */
class RefreshTokenRecordTest {

    @Test
    void encodeDecodeRoundTrip() {
        RefreshTokenRecord r = new RefreshTokenRecord();
        r.setFamilyId("family-123");
        r.setTenantId("00000000-0000-0000-0000-000000000000");
        r.setUserId("user-456");
        r.setUsername("tester");
        r.setState(RefreshTokenRecord.STATE_ACTIVE);
        r.setExpiresAt(1893456000000L);

        String encoded = r.encode();
        RefreshTokenRecord decoded = RefreshTokenRecord.decode(encoded);

        assertThat(decoded).isNotNull();
        assertThat(decoded.getFamilyId()).isEqualTo(r.getFamilyId());
        assertThat(decoded.getTenantId()).isEqualTo(r.getTenantId());
        assertThat(decoded.getUserId()).isEqualTo(r.getUserId());
        assertThat(decoded.getUsername()).isEqualTo(r.getUsername());
        assertThat(decoded.getState()).isEqualTo(r.getState());
        assertThat(decoded.getExpiresAt()).isEqualTo(r.getExpiresAt());
    }

    @Test
    void decodeHandlesPipeInUsernameAsLastField() {
        // 用户名含 '|'：split 限长 6，末位应完整保留
        RefreshTokenRecord r = new RefreshTokenRecord();
        r.setFamilyId("f");
        r.setTenantId("t");
        r.setUserId("u");
        r.setState(RefreshTokenRecord.STATE_ACTIVE);
        r.setExpiresAt(123L);
        r.setUsername("a|b|c");

        RefreshTokenRecord decoded = RefreshTokenRecord.decode(r.encode());
        assertThat(decoded).isNotNull();
        assertThat(decoded.getUsername()).isEqualTo("a|b|c");
    }

    @Test
    void decodeReturnsNullForMalformedInput() {
        assertThat(RefreshTokenRecord.decode(null)).isNull();
        assertThat(RefreshTokenRecord.decode("")).isNull();
        // 字段数不足
        assertThat(RefreshTokenRecord.decode("f|t|u")).isNull();
        // 过期时间非数字
        assertThat(RefreshTokenRecord.decode("f|t|u|active|not-a-number|name")).isNull();
    }
}
