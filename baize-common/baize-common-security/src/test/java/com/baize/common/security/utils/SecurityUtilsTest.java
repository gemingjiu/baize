package com.baize.common.security.utils;

import org.junit.Test;



/**
 * @author gemj
 * @since 2023/12/15 16:00
 */
public class SecurityUtilsTest {
    @Test
    public void encryptPassword() {
        String encryptPassword = SecurityUtils.encryptPassword("admin");
        System.out.println(encryptPassword);
    }

    @Test
    public void matchesPassword() {
        String encryptPassword = SecurityUtils.encryptPassword("admin");
        boolean matchesPassword = SecurityUtils.matchesPassword("admin", encryptPassword);
        System.out.println(matchesPassword);
    }

    // 测试密码匹配成功的情况
    @Test
    public void testMatchesPassword_Success() {
        boolean result = SecurityUtils.matchesPassword("correctPassword123", "correctPassword123");
        System.out.println (result+ "密码匹配成功时应返回 true");
    }

    // 测试密码不匹配的情况
    @Test
    public void testMatchesPassword_Failure() {
        boolean result = SecurityUtils.matchesPassword("correctPassword123", "wrongPassword456");
        System.out.println (result+"密码不匹配时应返回 false");
    }

    // 测试空密码的情况
    @Test
    public void testMatchesPassword_EmptyPasswords() {
        boolean result = SecurityUtils.matchesPassword("", "");
        System.out.println (result+"空密码应返回 false");
    }

    // 测试一个为空的情况
    @Test
    public void testMatchesPassword_OneEmpty() {
        boolean result1 = SecurityUtils.matchesPassword("", "password123");
        boolean result2 = SecurityUtils.matchesPassword("password123", "");
        assertFalse(result1, "一个密码为空时应返回 false");
        assertFalse(result2, "一个密码为空时应返回 false");
    }

    // 测试特殊字符的情况
    @Test
    public void testMatchesPassword_SpecialCharacters() {
        boolean result = SecurityUtils.matchesPassword("!@#$$%^&*()", "!@#$$%^&*()");
        assertTrue(result, "包含特殊字符的密码匹配成功时应返回 true");
    }

    // 测试边界条件：非常短的密码
    @Test
    public void testMatchesPassword_VeryShortPasswords() {
        boolean result = SecurityUtils.matchesPassword("a", "a");
        assertTrue(result, "非常短的密码匹配成功时应返回 true");
    }

    // 测试边界条件：非常长的密码
    @Test
    public void testMatchesPassword_VeryLongPasswords() {
        String longPassword = "a".repeat(100);
        boolean result = SecurityUtils.matchesPassword(longPassword, longPassword);
        assertTrue(result, "非常长的密码匹配成功时应返回 true");
    }
}