package com.gem.baize.common.security.util;

import com.gem.baize.common.core.constant.HTTPHeaderConstant;
import io.jsonwebtoken.Claims;
import org.junit.Test;


import java.util.HashMap;
import java.util.Map;

import static com.gem.baize.common.security.util.JwtUtils.createToken;
import static com.gem.baize.common.security.util.JwtUtils.parseToken;

public class JwtUtilsTest {

    @Test
    public void createTokenTest() {
        Map<String, Object> claims = new HashMap<>();
        claims.put(HTTPHeaderConstant.TENANT_ID, "1");
        claims.put(HTTPHeaderConstant.USER_ID, "1");
        claims.put(HTTPHeaderConstant.TRACE_ID, "2caa18f6-62ed-11f0-9fe2-0242ac120002");
        claims.put(HTTPHeaderConstant.USER_NAME, "admin");
        claims.put(HTTPHeaderConstant.ROLE,"admin");
        String token = createToken(claims);
        System.out.println(token);
    }

    @Test
    public void parseTokenTest() {
        Claims claims = parseToken("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxOjEiLCJYLVJvbGUiOiJhZG1pbiIsIlgtVGVuYW50LUlkIjoiMSIsIlgtVXNlci1JZCI6IjEiLCJYLVVzZXItTmFtZSI6ImFkbWluIiwiWC1UcmFjZS1JZCI6IjJjYWExOGY2LTYyZWQtMTFmMC05ZmUyLTAyNDJhYzEyMDAwMiIsImlhdCI6MTc1Mjc0NDM0OCwiZXhwIjoxNzUyNzUxNTQ4fQ.h-oRHwncjgtjHXyn9cuS5TzTZbvsJw4zKVx6uEMfM_Y");
        System.out.println(claims);
    }
}