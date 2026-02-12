package com.gem.baize.common.security.util;

import com.gem.baize.common.core.constant.CustomHttpHeaders;
import com.gem.baize.common.security.domain.dto.Payload;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertAll;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = JwtUtils.class)
@TestPropertySource(properties = {
        "jwt.secret=baize-a732659e-45e2-47a8-89ab-6c4b8af98bab",
        "jwt.expiration=7200000"
})
class JwtUtilsTest {
    @Autowired
    private JwtUtils jwtUtils;

    @Test
    void testComponent() {
        assertThat(jwtUtils).isNotNull();
        // 其他测试逻辑
    }

    @Test
    void shouldGenerateValidToken() {
        // Given
        Map<String, Object> claims = new HashMap<>() {{
            put(CustomHttpHeaders.TENANT_ID, "1");
            put(CustomHttpHeaders.USER_ID, "1");
            put(CustomHttpHeaders.TRACE_ID, "2caa18f6-62ed-11f0-9fe2-0242ac120002");
            put(CustomHttpHeaders.USER_NAME, "admin");
            put(CustomHttpHeaders.ROLE, "admin");
        }};

        // When
        String token = jwtUtils.createToken( claims,true);
        System.out.println("Generated Token: " + token);

        // Then
        Claims parsedClaims = jwtUtils.parseToken(token);
        assertAll(
                () -> assertThat(parsedClaims.getSubject()).isEqualTo("1:1"),
                () -> assertThat(parsedClaims.get(CustomHttpHeaders.TENANT_ID)).isEqualTo("1"),
                () -> assertThat(parsedClaims.get(CustomHttpHeaders.USER_ID)).isEqualTo("1"),
                () -> assertThat(parsedClaims.get(CustomHttpHeaders.TRACE_ID)).isEqualTo("2caa18f6-62ed-11f0-9fe2-0242ac120002"),
                () -> assertThat(parsedClaims.get(CustomHttpHeaders.USER_NAME)).isEqualTo("admin"),
                () -> assertThat(parsedClaims.get(CustomHttpHeaders.ROLE)).isEqualTo("admin")
        );

        String tenantId = jwtUtils.getTenantId(token);
        assertThat(tenantId).isEqualTo("1");
        String userId = jwtUtils.getUserId(token);
        assertThat(userId).isEqualTo("1");
        String traceId = jwtUtils.getTraceId(token);
        assertThat(traceId).isEqualTo("2caa18f6-62ed-11f0-9fe2-0242ac120002");
        String userName = jwtUtils.getUserName(token);
        assertThat(userName).isEqualTo("admin");
    }

    @Test
    void shouldGenerateJwtPayload() {
        // Given
        Map<String, Object> claims = new HashMap<>() {{
            put(CustomHttpHeaders.TENANT_ID, "1");
            put(CustomHttpHeaders.USER_ID, "1");
            put(CustomHttpHeaders.TRACE_ID, "2caa18f6-62ed-11f0-9fe2-0242ac120002");
            put(CustomHttpHeaders.USER_NAME, "system");
            put(CustomHttpHeaders.ROLE, "system");
        }};

        // When
        String token = jwtUtils.createToken(claims);
        System.out.println("Generated Token: " + token);

        // Then
        Payload payload = jwtUtils.parsePayload(token);
        assertAll(
                () -> assertThat(payload.getSubject()).isEqualTo("1:1"),
                () -> assertThat(payload.getTenantId()).isEqualTo("1"),
                () -> assertThat(payload.getUserId()).isEqualTo("1"),
                () -> assertThat(payload.getTraceId()).isEqualTo("2caa18f6-62ed-11f0-9fe2-0242ac120002"),
                () -> assertThat(payload.getUserName()).isEqualTo("system"),
                () -> assertThat(payload.getRole()).isEqualTo("system")
        );
    }


    @Test
    void shouldThrowExceptionWhenTokenInvalid() {
        String invalidToken = "invalid.token.here";

        Throwable thrown = catchThrowable(() -> jwtUtils.parseToken(invalidToken));

        assertThat(thrown)
                .isInstanceOf(JwtException.class)
                .satisfies(ex -> assertThat(ex.getMessage())
                        .containsAnyOf("JWT", "Malformed", "Unexpected", "Failed"));
    }

}