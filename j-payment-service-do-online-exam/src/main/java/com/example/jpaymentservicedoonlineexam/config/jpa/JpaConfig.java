package com.example.jpaymentservicedoonlineexam.config.jpa;

import com.example.jpaymentservicedoonlineexam.common.jwt.JwtTokenUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.OffsetDateTime;
import java.util.Optional;

import static com.example.jpaymentservicedoonlineexam.constant.StringConstant.BEARER_PREFIX;

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
            String userName = null;
            HttpServletRequest request =
                    ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (accessToken != null && accessToken.startsWith(BEARER_PREFIX)) {
                var token = accessToken.substring(7);
                try {
                    userName = JwtTokenUtil.getuserNameFromToken(token);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
            return Optional.of(userName);
        };
    }
}