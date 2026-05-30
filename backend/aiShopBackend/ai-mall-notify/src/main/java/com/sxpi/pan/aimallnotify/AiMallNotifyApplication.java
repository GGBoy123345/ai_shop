package com.sxpi.pan.aimallnotify;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan("com.sxpi.pan")
@MapperScan("com.sxpi.pan.aimallnotify.mapper")
public class AiMallNotifyApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiMallNotifyApplication.class, args);
    }

}
