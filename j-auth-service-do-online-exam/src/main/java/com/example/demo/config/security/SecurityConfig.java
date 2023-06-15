package com.example.demo.config.security;

import com.example.demo.config.security.OauthCustom.CustomOAuth2UserService;
import com.example.demo.config.security.OauthCustom.OAuthLoginSuccessHandler;
import com.example.demo.config.security.SecurityCustom.CustomAuthenticationEntryPoint;
import com.example.demo.config.security.SecurityCustom.CustomUserDetailsService;
import com.example.demo.config.security.SecurityCustom.Filter.JwtTokenAuthenticationFilter;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.Collections;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Autowired private CustomOAuth2UserService oauth2UserService;

	@Autowired private OAuthLoginSuccessHandler oauthLoginSuccessHandler;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
//		CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
//		requestHandler.setCsrfRequestAttributeName("_csrf");
		httpSecurity
				.exceptionHandling()
				.authenticationEntryPoint(new CustomAuthenticationEntryPoint())
				.and()
				.addFilterBefore(
						new JwtTokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
				.securityContext()
				.requireExplicitSave(false)
				.and()
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.ALWAYS))
				.cors()
				.configurationSource(
						request -> {
							CorsConfiguration config = new CorsConfiguration();
							//
							config.setAllowedOrigins(Collections.singletonList("*"));
							config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS", "HEAD"));
							config.setAllowCredentials(true);
							config.setAllowedHeaders(Collections.singletonList("*"));
							config.setMaxAge(3600L);
							return config;
						})
				.and()
				.csrf().disable()
//						(
//						(csrf) -> csrf.disable()
//								csrf.requireCsrfProtectionMatcher(new RequestMatcher() {
//											private Pattern allowedMethods = Pattern.compile("^(GET|HEAD|TRACE|OPTIONS)$");
//
//											@Override
//											public boolean matches(HttpServletRequest request) {
//												// Ignoring CSRF token for specific request paths and methods
//												return !allowedMethods.matcher(request.getMethod()).matches()
//														&& !request.getRequestURI().equals("/api/v1/auth/register")
//														&& !request.getRequestURI().equals("/api/v1/auth/login");
//											}
//										})
//										.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
//				)
//								.csrf(
//										(csrf) ->
//												csrf.csrfTokenRequestHandler(requestHandler)
//														.ignoringRequestMatchers("/api/v1/auth/register", "/api/v1/auth/login")
//														.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
				.authorizeHttpRequests()
				.requestMatchers("/api/v1/auth/register", "/api/v1/auth/login", "/api/v1/auth/accounts-exam/sendEmail")
				.permitAll()
				.anyRequest()
				.authenticated()
				.and()
				.httpBasic()
				.and()
				.oauth2Login()
				.userInfoEndpoint()
				.userService(oauth2UserService)
				.and()
				.successHandler(oauthLoginSuccessHandler);

		return httpSecurity.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
			throws Exception {
		return configuration.getAuthenticationManager();
	}

	@Bean
	public UserDetailsService userDetailsService() {
		return new CustomUserDetailsService();
	}

	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userDetailsService());
		authenticationProvider.setPasswordEncoder(passwordEncoder());
		return authenticationProvider;
	}

	@Bean
	public AuthenticationManager authManager(HttpSecurity http) throws Exception {
		AuthenticationManagerBuilder authenticationManagerBuilder =
				http.getSharedObject(AuthenticationManagerBuilder.class);
		authenticationManagerBuilder.authenticationProvider(authenticationProvider());
		return authenticationManagerBuilder.build();
	}
}
