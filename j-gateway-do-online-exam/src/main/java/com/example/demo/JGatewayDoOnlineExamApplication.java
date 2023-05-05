package com.example.demo;

import java.time.Duration;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Mono;

@SpringBootApplication
@EnableDiscoveryClient
public class JGatewayDoOnlineExamApplication {

	public static void main(String[] args) {
    SpringApplication.run(JGatewayDoOnlineExamApplication.class, args);
	}

    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> Mono.just("1");
    }

    @Bean
    public RedisRateLimiter redisRateLimiter()
    {
        return new RedisRateLimiter(1,2);
    }

//    @Bean
//    public Customizer<ReactiveResilience4JCircuitBreakerFactory> defaultCustomizer()
//    {
//        return factory->factory.configureDefault(id ->new Resilience4JConfigBuilder(id)
//            .circuitBreakerConfig(CircuitBreakerConfig.ofDefaults())
//            .timeLimiterConfig(TimeLimiterConfig.custom()
//                .timeoutDuration(Duration.ofSeconds(2)).build()).build());
//    }

}
