package com.baize.common.security.utils;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.baize.common.core.constant.SecurityConstants;
import com.baize.common.core.constant.TokenConstants;
import com.baize.common.core.context.SecurityContext;
import com.baize.common.core.utils.ServletUtils;
import com.baize.common.core.utils.algorithm.Base64;
import com.baize.common.core.utils.text.StringUtils;
import com.baize.system.api.domain.vo.LoginUser;

/**
 * @author gemj
 * @since 2023/08/22 14:08
 */
public class SecurityUtils {
    private static final String AdminId = "1";

    private static final String ALGORITHMS = "RSA";
    private static final String PRIVATEKEY = "MIIBVAIBADANBgkqhkiG9w0BAQEFAASCAT4wggE6AgEAAkEAqhHyZfSsYourNxaY\n"
        + "7Nt+PrgrxkiA50efORdI5U5lsW79MmFnusUA355oaSXcLhu5xxB38SMSyP2KvuKN\n"
        + "PuH3owIDAQABAkAfoiLyL+Z4lf4Myxk6xUDgLaWGximj20CUf+5BKKnlrK+Ed8gA\n"
        + "kM0HqoTt2UZwA5E2MzS4EI2gjfQhz5X28uqxAiEA3wNFxfrCZlSZHb0gn2zDpWow\n"
        + "cSxQAgiCstxGUoOqlW8CIQDDOerGKH5OmCJ4Z21v+F25WaHYPxCFMvwxpcw99Ecv\n"
        + "DQIgIdhDTIqD2jfYjPTY8Jj3EDGPbH2HHuffvflECt3Ek60CIQCFRlCkHpi7hthh\n"
        + "YhovyloRYsM+IS9h/0BzlEAuO0ktMQIgSPT3aFAgJYwKpqRYKlLDVcflZFCKY7u3\n" + "UP8iWi1Qw0Y=";

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
     * 获取TokenID
     */
    public static String getTokenId() {
        return SecurityContext.getTokenId();
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
        if (StringUtils.isNotEmpty(token) && token.startsWith(TokenConstants.TOKEN_PREFIX)) {
            token = token.replaceFirst(TokenConstants.TOKEN_PREFIX, "");
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
     * @param rawPassword 真实密码
     * @param encodedPassword 加密后字符
     * @return 结果
     */
    public static boolean matchesPassword(String rawPassword, String encodedPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    /**
     * RSA非对称解密算法，解密
     * 
     * @throws Exception 解密失败会抛出该异常
     * @param encodedPassword 加密的密文
     * @return 明文
     * 
     */
    public static String decryptPassword(String encodedPassword) throws Exception {
        // 解密
        byte[] decodePrivateKey = Base64.decode(PRIVATEKEY);
        return RSAEncryptUtil.decrypt(encodedPassword, decodePrivateKey);
    }
}
