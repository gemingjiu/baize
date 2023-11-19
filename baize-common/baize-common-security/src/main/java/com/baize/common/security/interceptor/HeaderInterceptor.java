package com.baize.common.security.interceptor;

import com.baize.common.core.constant.SecurityConstants;
import com.baize.common.core.context.SecurityContext;
import com.baize.common.core.utils.ServletUtils;
import com.baize.common.core.utils.text.StringUtils;
import com.baize.common.security.service.AuthService;
import com.baize.common.security.utils.SecurityUtils;
import com.baize.system.api.domain.vo.LoginUser;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 请求头拦截器
 *
 * @author gemj
 * @since 2023/08/22 14:13
 */
public class HeaderInterceptor implements AsyncHandlerInterceptor {

    private AuthService authService;

    public HeaderInterceptor(AuthService authService){
        this.authService =  authService;
    }
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        SecurityContext.setId(ServletUtils.getHeader(request, SecurityConstants.DETAILS_ID));
        SecurityContext.setUserName(ServletUtils.getHeader(request, SecurityConstants.DETAILS_USERNAME));
        SecurityContext.setUserKey(ServletUtils.getHeader(request, SecurityConstants.USER_KEY));

        String token = SecurityUtils.getToken();
        if (StringUtils.isNotEmpty(token)) {
            LoginUser loginUser = authService.getLoginUser(token);
            if (StringUtils.isNotNull(loginUser)) {
                authService.verifyLoginUserExpire(loginUser);
                SecurityContext.set(SecurityConstants.LOGIN_USER, loginUser);
            }
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        SecurityContext.remove();
    }
}
