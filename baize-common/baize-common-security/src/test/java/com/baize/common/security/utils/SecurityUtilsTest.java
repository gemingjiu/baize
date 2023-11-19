package com.baize.common.security.utils;

/**
 * @author gemj
 * @since 2023/12/15 16:00
 */
class SecurityUtilsTest {

    @org.junit.jupiter.api.Test
    void encryptPassword() {
        String encryptPassword = SecurityUtils.encryptPassword("admin");
        System.out.println(encryptPassword);
    }

    @org.junit.jupiter.api.Test
    void matchesPassword() {
        String encryptPassword = SecurityUtils.encryptPassword("admin");
        boolean matchesPassword = SecurityUtils.matchesPassword("admin", encryptPassword);
        System.out.println(matchesPassword);
    }
}