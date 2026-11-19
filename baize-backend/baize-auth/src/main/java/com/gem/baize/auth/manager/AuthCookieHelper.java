package com.gem.baize.auth.manager;

import com.gem.baize.auth.config.AuthCookieProperties;
import com.gem.baize.common.security.domain.vo.IssuedRefresh;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * T1.3：HttpOnly refresh Cookie 与 CSRF 双重提交 Cookie 的读写工具。
 */
@Component
public class AuthCookieHelper {

    @Autowired
    private AuthCookieProperties props;

    /**
     * 写 refresh（HttpOnly）+ CSRF（JS 可读）两个 Cookie。
     */
    public void writeCookies(HttpServletResponse response, IssuedRefresh issued) {
        Duration maxAge = Duration.ofSeconds(props.getTtlSeconds());
        ResponseCookie refresh = ResponseCookie.from(props.getRefreshCookieName(), issued.getRawToken())
                .httpOnly(true)
                .secure(props.isSecure())
                .sameSite(props.getSameSite())
                .path(props.getPath())
                .maxAge(maxAge)
                .build();
        ResponseCookie csrf = ResponseCookie.from(props.getCsrfCookieName(), issued.getCsrfToken())
                .httpOnly(false)
                .secure(props.isSecure())
                .sameSite(props.getSameSite())
                .path(props.getPath())
                .maxAge(maxAge)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, refresh.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, csrf.toString());
    }

    /**
     * 登出：清空两个 Cookie（Path 必须与写入时一致）。
     */
    public void clearCookies(HttpServletResponse response) {
        ResponseCookie refresh = ResponseCookie.from(props.getRefreshCookieName(), "")
                .httpOnly(true).secure(props.isSecure()).sameSite(props.getSameSite())
                .path(props.getPath()).maxAge(Duration.ZERO).build();
        ResponseCookie csrf = ResponseCookie.from(props.getCsrfCookieName(), "")
                .httpOnly(false).secure(props.isSecure()).sameSite(props.getSameSite())
                .path(props.getPath()).maxAge(Duration.ZERO).build();
        response.addHeader(HttpHeaders.SET_COOKIE, refresh.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, csrf.toString());
    }

    public String readCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (name.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    /**
     * CSRF 双重提交校验：请求头值须与 CSRF Cookie 值一致且非空。
     */
    public boolean validCsrf(HttpServletRequest request) {
        String cookie = readCookie(request, props.getCsrfCookieName());
        String header = request.getHeader(props.getCsrfHeaderName());
        return cookie != null && !cookie.isEmpty() && cookie.equals(header);
    }
}
