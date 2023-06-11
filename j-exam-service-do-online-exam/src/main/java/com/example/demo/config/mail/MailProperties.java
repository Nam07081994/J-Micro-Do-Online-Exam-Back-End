package com.example.demo.config.mail;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "app.mail")
public class MailProperties {
	private String hostname;
	private String sslport;
	private String email;
	private String password;
}
