package com.gem.baize.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableFeignClients(basePackages = "com.gem.baize.api.system")
public class BaizeAuthApplication {
    private static final Logger log = LoggerFactory.getLogger(BaizeAuthApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(BaizeAuthApplication.class, args);
        printWelcomeMessage();
    }

    private static void printWelcomeMessage() {
        if (log.isInfoEnabled()){
            log.info("\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89");
            log.info("\uD83C\uDF89 欢迎使用Baize系统 \uD83C\uDF89");
            log.info("\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89");
        }
    }
}