package com.baize.job;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * 白泽定时任务服务启动类
 * @author gemj
 * @since 2023/12/11 19:36
 */

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class JobApplication {
    public static void main(String[] args) {
        SpringApplication.run(JobApplication.class, args);
        System.out.println("白泽定时任务服务启动成功......");
    }
}