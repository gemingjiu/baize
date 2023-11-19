package com.baize.system;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * 白泽代码生成服务启动类
 *
 * @author gemj
 */


@EnableFeignClients(basePackages = "com.baize")
// 指定要扫描的Mapper类的包的路径
@MapperScan("com.baize.**.mapper")
// 表示通过aop框架暴露该代理对象,AopContext能够访问
@EnableAspectJAutoProxy(exposeProxy = true)
@SpringBootApplication
public class SystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(SystemApplication.class, args);
        System.out.println("白泽系统服务启动成功......");
    }
}
