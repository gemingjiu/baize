package com.gem.baize.common.core.context;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.gem.baize.common.core.constant.CustomHttpHeaders;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 获取当前线程变量中的 用户id、用户名称、Token等信息
 * 注意： 必须在网关通过请求头的方法传入，同时在HeaderInterceptor拦截器设置值。 否则这里无法获取
 */
public class RequestContextHolder {
    private static final TransmittableThreadLocal<Map<String, Object>> THREAD_LOCAL = TransmittableThreadLocal.withInitial(ConcurrentHashMap::new);

    public static void set(String key, Object value) {
        getLocalMap().put(key, value);
    }

    public static String get(String key) {
        return Optional.ofNullable(getLocalMap().get(key))
                .map(Object::toString).orElse(null);
    }

    public static <T> T get(String key, Class<T> clazz) {
        return Optional.ofNullable(getLocalMap().get(key))
                .filter(clazz::isInstance)
                .map(clazz::cast).orElse(null);
    }

    public static Map<String, Object> getLocalMap() {
        return THREAD_LOCAL.get();
    }

    public static void setLocalMap(Map<String, Object> threadLocalMap) {
        THREAD_LOCAL.set(threadLocalMap);
    }

    public static String getTenantId() {
        return get(CustomHttpHeaders.TENANT_ID);
    }

    public static void setTenantId(String tenant) {
        set(CustomHttpHeaders.TENANT_ID, tenant);
    }

    public static String getUserId() {
        return get(CustomHttpHeaders.USER_ID);
    }

    public static void setUserId(String user) {
        set(CustomHttpHeaders.USER_ID, user);
    }

    public static String getUserName() {
        return get(CustomHttpHeaders.USER_NAME);
    }

    public static void setUserName(String username) {
        set(CustomHttpHeaders.USER_NAME, username);
    }

    public static String getUserKey() {
        return get(CustomHttpHeaders.SUBJECT_ID);
    }

    public static void setUserKey(String userKey) { set(CustomHttpHeaders.SUBJECT_ID, userKey); }

    public static String getTraceId() { return get(CustomHttpHeaders.TRACE_ID); }

    public static void setTraceId(String traceId) { set(CustomHttpHeaders.TRACE_ID, traceId); }

    public static String getRole() { return get(CustomHttpHeaders.ROLE); }

    public static void setRole(String role) { set(CustomHttpHeaders.ROLE, role); }

    public static void remove() {
        THREAD_LOCAL.remove();
    }
}
