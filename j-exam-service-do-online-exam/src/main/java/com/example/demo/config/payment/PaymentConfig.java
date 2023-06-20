package com.example.demo.config.payment;

import com.example.demo.config.mail.MailProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(PaymentProperties.class)
public class PaymentConfig {
}
