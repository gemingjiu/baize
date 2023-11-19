package com.baize.gateway;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;


/**
 * 白泽网关服务启动类
 *
 * @author gemj
 */


@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class GatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
        System.out.println("白泽网关启动成功......");
    }
}
