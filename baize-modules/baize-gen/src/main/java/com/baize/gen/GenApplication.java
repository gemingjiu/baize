package com.baize.gen;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 白泽代码生成服务启动类
 *
 * @author gemj
 */
@EnableFeignClients(basePackages = "com.baize")
@MapperScan("com.baize.**.mapper")
@SpringBootApplication
public class GenApplication {
    public static void main(String[] args) {
        SpringApplication.run(GenApplication.class, args);
        System.out.println("白泽代码生成服务启动成功......");
    }
}
