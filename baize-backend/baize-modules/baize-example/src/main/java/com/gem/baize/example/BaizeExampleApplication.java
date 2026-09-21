package com.gem.baize.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;


@EnableFeignClients(basePackages = "com.gem")
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class BaizeExampleApplication {
    private static final Logger log = LoggerFactory.getLogger(BaizeExampleApplication.class);
    public static void main(String[] args) {
        SpringApplication.run(BaizeExampleApplication.class, args);
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
