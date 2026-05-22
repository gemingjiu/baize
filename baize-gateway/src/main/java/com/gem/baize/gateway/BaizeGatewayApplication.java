package com.gem.baize.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;


@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration.class})
public class BaizeGatewayApplication {
    private static final Logger log = LoggerFactory.getLogger(BaizeGatewayApplication.class);
    public static void main(String[] args) {
        SpringApplication.run(BaizeGatewayApplication.class, args);
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
