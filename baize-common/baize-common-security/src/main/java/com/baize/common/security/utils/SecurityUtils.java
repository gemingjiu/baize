package com.baize.common.security.utils;

import com.baize.common.core.constant.SecurityConstants;
import com.baize.common.core.constant.TokenConstants;
import com.baize.common.core.context.SecurityContext;
import com.baize.common.core.utils.ServletUtils;
import com.baize.common.core.utils.text.StringUtils;
import com.baize.system.api.domain.vo.LoginUser;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.servlet.http.HttpServletRequest;

/**
 * @author gemj
 * @since 2023/08/22 14:08
 */
public class SecurityUtils {
    private static final String  AdminId = "1";
    /**
     * 获取用户ID
     */
    public static String getId() {
        return SecurityContext.getId();
    }

    /**
     * 获取用户名称
     */
    public static String getUsername() {
        return SecurityContext.getUserName();
    }

    /**
     * 获取用户key
     */
    public static String getUserKey() {
        return SecurityContext.getUserKey();
    }

    /**
     * 获取登录用户信息
     */
    public static LoginUser getLoginUser() {
        return SecurityContext.get(SecurityConstants.LOGIN_USER, LoginUser.class);
    }

    /**
     * 获取请求token
     */
    public static String getToken() {
        return getToken(ServletUtils.getRequest());
    }

    /**
     * 根据request获取请求token
     */
    public static String getToken(HttpServletRequest request) {
        // 从header获取token标识
        String token = request.getHeader(TokenConstants.AUTHENTICATION);
        return replaceTokenPrefix(token);
    }

    /**
     * 裁剪token前缀
     */
    public static String replaceTokenPrefix(String token) {
        // 如果前端设置了令牌前缀，则裁剪掉前缀
        if (StringUtils.isNotEmpty(token) && token.startsWith(TokenConstants.PREFIX)) {
            token = token.replaceFirst(TokenConstants.PREFIX, "");
        }
        return token;
    }

    /**
     * 是否为管理员
     *
     * @param userId 用户ID
     * @return 结果
     */
    public static boolean isAdmin(String userId) {
        return StringUtils.isNotBlank(userId) && StringUtils.equals(userId, AdminId);
    }

    /**
     * 生成BCryptPasswordEncoder密码
     *
     * @param password 密码
     * @return 加密字符串
     */
    public static String encryptPassword(String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }

    /**
     * 判断密码是否相同
     *
     * @param rawPassword     真实密码
     * @param encodedPassword 加密后字符
     * @return 结果
     */
    public static boolean matchesPassword(String rawPassword, String encodedPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
