package com.gem.baize.common.core.constant;

import org.springframework.http.HttpHeaders;

public final class CustomHttpHeaders extends HttpHeaders {
    public static final String SUBJECT_ID = "X-Subject-Id";
    public static final String TENANT_ID = "X-Tenant-Id";
    public static final String TRACE_ID = "X-Trace-Id";
    public static final String USER_ID = "X-User-Id";
    public static final String ROLE = "X-Role";
    public static final String USER_NAME = "X-User-Name";

    private CustomHttpHeaders() {
        throw new UnsupportedOperationException("常量类禁止实例化");
    }
}
