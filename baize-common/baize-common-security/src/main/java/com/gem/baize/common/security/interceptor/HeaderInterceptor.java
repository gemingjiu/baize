package com.gem.baize.common.security.interceptor;


import com.gem.baize.common.core.constant.CustomHttpHeaders;
import com.gem.baize.common.core.context.RequestContextHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

/**
 * 请求头拦截器 处理传递进来的租户、用户、权限等信息
 */
public class HeaderInterceptor implements AsyncHandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String tenantId = request.getHeader(CustomHttpHeaders.TENANT_ID);
        String traceId = request.getHeader(CustomHttpHeaders.TRACE_ID);
        String userId = request.getHeader(CustomHttpHeaders.USER_ID);
        String userKey = request.getHeader(CustomHttpHeaders.SUBJECT_ID);
        String userName = request.getHeader(CustomHttpHeaders.USER_NAME);
        String role = request.getHeader(CustomHttpHeaders.ROLE);


        // 存到上下文里，方便业务层使用
        RequestContextHolder.setTenantId(tenantId);
        RequestContextHolder.setTraceId(traceId);
        RequestContextHolder.setUserId(userId);
        RequestContextHolder.setUserKey(userKey);
        RequestContextHolder.setUserName(userName);
        RequestContextHolder.setUserKey(userKey);
        RequestContextHolder.setRole(role);


        return true;
    }

    @Override
    public void afterConcurrentHandlingStarted(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // ⚡ 当请求被异步处理时（比如返回 Callable、DeferredResult），会调用这里
        // 一般可以做清理上下文、日志等操作
        RequestContextHolder.remove();
    }
}
