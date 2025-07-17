package com.gem.baize.admin;


import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.gem.**.mapper")
public class BaizeAdminApplication {
    private static final Logger log = LoggerFactory.getLogger(BaizeAdminApplication.class);
    public static void main(String[] args) {
        SpringApplication.run(BaizeAdminApplication.class, args);
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
