package com.gem.baize.common.security.util;

import com.gem.baize.common.security.vo.UserVO;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {
    public static UserVO getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserVO)) {
            throw new RuntimeException("未登录或token失效");
        }
        return (UserVO) authentication.getPrincipal();
    }

    public static Long getUserId() {
        return getCurrentUser().getUserId();
    }

    public static String getUsername() {
        return getCurrentUser().getUsername();
    }
}
