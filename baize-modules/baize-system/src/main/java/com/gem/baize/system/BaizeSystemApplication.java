package com.gem.baize.system;


import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.gem.**.mapper")
public class BaizeSystemApplication {
    private static final Logger log = LoggerFactory.getLogger(BaizeSystemApplication.class);
    public static void main(String[] args) {
        SpringApplication.run(BaizeSystemApplication.class, args);
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
