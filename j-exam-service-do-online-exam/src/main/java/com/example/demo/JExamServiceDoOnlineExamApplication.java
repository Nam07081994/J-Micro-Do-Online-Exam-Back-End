package com.example.demo;

import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableDiscoveryClient
public class JExamServiceDoOnlineExamApplication {

	public static void main(String[] args) {
		SpringApplication.run(JExamServiceDoOnlineExamApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate() {
		CloseableHttpClient httpClient =
				HttpClients.custom().setDefaultCookieStore(new BasicCookieStore()).build();

		ClientHttpRequestFactory requestFactory =
				new HttpComponentsClientHttpRequestFactory(httpClient);

		return new RestTemplate(requestFactory);
	}
}
