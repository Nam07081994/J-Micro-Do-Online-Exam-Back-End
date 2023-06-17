package com.example.file.jfileservicedoonlineexam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class JFileServiceDoOnlineExamApplication {

	public static void main(String[] args) {
		SpringApplication.run(JFileServiceDoOnlineExamApplication.class, args);
	}
}
