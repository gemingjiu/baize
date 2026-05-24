package com.gem.baize.common.webmvc.interceptor;


import com.gem.baize.common.core.constant.CustomHttpHeaders;
import com.gem.baize.common.webmvc.context.RequestContextHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
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
        String dataScope = request.getHeader(CustomHttpHeaders.DATA_SCOPE);


        // 存到上下文里，方便业务层使用
        if (StringUtils.isNotBlank(tenantId)) {
            RequestContextHolder.setTenantId(tenantId);
        }
        if (StringUtils.isNotBlank(traceId)) {
            RequestContextHolder.setTraceId(traceId);
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
}
