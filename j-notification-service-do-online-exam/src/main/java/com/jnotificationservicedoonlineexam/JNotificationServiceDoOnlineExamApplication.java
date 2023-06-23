package com.jnotificationservicedoonlineexam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class JNotificationServiceDoOnlineExamApplication {

	public static void main(String[] args) {
		SpringApplication.run(JNotificationServiceDoOnlineExamApplication.class, args);
	}
}
