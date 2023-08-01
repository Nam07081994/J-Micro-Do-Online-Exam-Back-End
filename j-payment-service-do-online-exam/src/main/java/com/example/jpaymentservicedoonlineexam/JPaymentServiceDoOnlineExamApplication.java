package com.example.jpaymentservicedoonlineexam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableDiscoveryClient
@EnableScheduling
public class JPaymentServiceDoOnlineExamApplication {

    public static void main(String[] args) {
        SpringApplication.run(JPaymentServiceDoOnlineExamApplication.class, args);
    }
}
