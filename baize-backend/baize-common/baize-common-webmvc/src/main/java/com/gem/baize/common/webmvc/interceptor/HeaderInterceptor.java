package com.gem.baize.common.webmvc.interceptor;


import com.gem.baize.common.core.constant.CustomHttpHeaders;
import com.gem.baize.common.webmvc.context.RequestContextHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

/**
 * 请求头拦截器 处理传递进来的租户、用户、权限等信息
 */
public class HeaderInterceptor implements AsyncHandlerInterceptor {

    /** logback MDC 中 traceId 的键名，与各模块 logback-spring.xml 的 %X{traceId} 对应。 */
    private static final String MDC_TRACE_ID_KEY = "traceId";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String tenantId = request.getHeader(CustomHttpHeaders.TENANT_ID);
        String traceId = request.getHeader(CustomHttpHeaders.TRACE_ID);
        String userId = request.getHeader(CustomHttpHeaders.USER_ID);
        String userKey = request.getHeader(CustomHttpHeaders.SUBJECT_ID);
        String userName = request.getHeader(CustomHttpHeaders.USER_NAME);
        String role = request.getHeader(CustomHttpHeaders.ROLE);
        String dataScope = request.getHeader(CustomHttpHeaders.DATA_SCOPE);


        // 存到上下文里，方便业务层使用
        if (StringUtils.isNotBlank(tenantId)) {
            RequestContextHolder.setTenantId(tenantId);
        }
        if (StringUtils.isNotBlank(traceId)) {
            RequestContextHolder.setTraceId(traceId);
            // 补齐 logback MDC：OTel bridge 会自动写入由 traceparent 传播来的 traceId，
            // 仅在其缺失时用上游透传的 X-Trace-Id 兜底，避免覆盖 OTel 的权威值。
            // 修复前只写 ThreadLocal，导致下游日志 traceId 字段恒空、无法与网关串联。
            if (StringUtils.isBlank(MDC.get(MDC_TRACE_ID_KEY))) {
                MDC.put(MDC_TRACE_ID_KEY, traceId);
            }
        }
        if (StringUtils.isNotBlank(userId)) {
            RequestContextHolder.setUserId(userId);
        }
        if (StringUtils.isNotBlank(userKey)) {
            RequestContextHolder.setUserKey(userKey);
        }
        if (StringUtils.isNotBlank(userName)) {
            RequestContextHolder.setUserName(userName);
        }
        if (StringUtils.isNotBlank(role)) {
            RequestContextHolder.setRole(role);
        }
        if (StringUtils.isNotBlank(dataScope)) {
            RequestContextHolder.setDataScope(dataScope);
        }

        return true;
    }

    @Override
    public void afterConcurrentHandlingStarted(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 当请求被异步处理时（比如返回 Callable、DeferredResult），会调用这里
        // 一般可以做清理上下文、日志等操作
        RequestContextHolder.remove();
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // P1-A：同步请求结束后清理 ThreadLocal，避免线程复用把上一请求的租户/角色泄漏给下一请求
        RequestContextHolder.remove();
        // 同样清理 MDC 中的 traceId 兜底值，防止线程复用污染下一请求的日志
        MDC.remove(MDC_TRACE_ID_KEY);
    }
}
