package com.sxpi.pan.aimalluser;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.sxpi.pan.aimalluser.mapper")
@ComponentScan(basePackages = {"com.sxpi.pan.aimalluser", "com.sxpi.pan.aimallcommon"})
public class AiMallUserApplication {
    public static void main(String[] args) {
        SpringApplication.run(AiMallUserApplication.class, args);
    }
}
