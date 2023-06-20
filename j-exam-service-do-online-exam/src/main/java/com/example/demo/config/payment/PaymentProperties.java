package com.example.demo.config.payment;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "app.payment")
public class PaymentProperties {
    private String publicKey;
    private String secretKey;
}
