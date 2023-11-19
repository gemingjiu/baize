package com.baize.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 白泽认证授权启动类
 *
 * @author gemj
 * @since 2023/08/22 10:56
 */
@EnableFeignClients(basePackages = "com.baize")
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class}, scanBasePackages = "com.baize")
public class AuthApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
        System.out.println("白泽认证授权中心启动成功......");
    }
}
