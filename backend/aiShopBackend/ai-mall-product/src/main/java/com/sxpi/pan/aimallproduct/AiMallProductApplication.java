package com.sxpi.pan.aimallproduct;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableDiscoveryClient
@EnableScheduling
@ComponentScan("com.sxpi.pan")
@MapperScan("com.sxpi.pan.aimallproduct.mapper")
public class AiMallProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiMallProductApplication.class, args);
    }

}
