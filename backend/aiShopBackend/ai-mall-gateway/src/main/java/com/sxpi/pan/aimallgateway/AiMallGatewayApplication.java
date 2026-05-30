package com.sxpi.pan.aimallgateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class AiMallGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(AiMallGatewayApplication.class, args);
    }
}
