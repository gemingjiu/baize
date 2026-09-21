package com.gem.baize.auth;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "admin123";
        String encodedPassword = encoder.encode(rawPassword);

        System.out.println("========== 生成的加密密码 ==========");
        System.out.println("原始密码: " + rawPassword);
        System.out.println("加密密码: " + encodedPassword);
        System.out.println("验证匹配: " + encoder.matches(rawPassword, encodedPassword));
        System.out.println("==================================");
        System.out.println();
        System.out.println("SQL语句:");
        System.out.println("UPDATE sys_user SET password = '" + encodedPassword + "' WHERE user_name = 'admin';");
    }
}
