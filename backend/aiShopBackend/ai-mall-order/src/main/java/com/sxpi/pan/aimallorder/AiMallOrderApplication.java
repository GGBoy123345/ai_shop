package com.sxpi.pan.aimallorder;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan("com.sxpi.pan")
@MapperScan("com.sxpi.pan.aimallorder.mapper")
public class AiMallOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiMallOrderApplication.class, args);
    }

}
