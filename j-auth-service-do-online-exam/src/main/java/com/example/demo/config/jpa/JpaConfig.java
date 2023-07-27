package com.example.demo.config.jpa;

import java.time.OffsetDateTime;
import java.util.Optional;

import com.example.demo.common.jwt.JwtTokenUtil;
import com.example.demo.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static com.example.demo.constant.StringConstant.BEARER_KEY;

@Configuration
@EnableJpaAuditing(
		auditorAwareRef = "auditorProvider",
		dateTimeProviderRef = "auditingDateTimeProvider")
public class JpaConfig {
	@Autowired
	private UserRepository userRepository;
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
			String userId;
			String userName = null;
			HttpServletRequest request =
					((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
			String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);
			if (accessToken != null && accessToken.startsWith(BEARER_KEY)) {
				var token = accessToken.substring(7);
				try {
					userId = JwtTokenUtil.getUserInfoFromToken1(token, "aud");
					userName = userRepository.findById(Long.valueOf(userId)).get().getUserName();
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
			}
			return Optional.of(userName);
		};
	}
}
