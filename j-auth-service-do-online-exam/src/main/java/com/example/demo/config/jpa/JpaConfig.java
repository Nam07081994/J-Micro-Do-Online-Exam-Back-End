package com.example.demo.config.jpa;

import java.time.OffsetDateTime;
import java.util.Optional;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing(
		auditorAwareRef = "auditorProvider",
		dateTimeProviderRef = "auditingDateTimeProvider")
public class JpaConfig {
	private static final ThreadLocal<String> REGISTERED_USER = new ThreadLocal<>();

	public static void setRegisteredUser(String username) {
		REGISTERED_USER.set(username);
	}

	public static void removeRegisteredUser() {
		REGISTERED_USER.remove();
	}

	@Bean(name = "auditingDateTimeProvider")
	public DateTimeProvider dateTimeProvider() {
		return () -> Optional.of(OffsetDateTime.now());
	}

	@Bean(name = "auditorProvider")
	public AuditorAware<String> auditorAware() {
		return () -> {
			String username = REGISTERED_USER.get();

			return Optional.of(username);
		};
	}
}
