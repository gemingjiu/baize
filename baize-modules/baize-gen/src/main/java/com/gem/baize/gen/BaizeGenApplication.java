package com.gem.baize.gen;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {"com.gem.baize"})
public class BaizeGenApplication {
    public static void main(String[] args) {
        SpringApplication.run(BaizeGenApplication.class, args);
    }
}
