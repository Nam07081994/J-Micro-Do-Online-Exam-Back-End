package com.example.demo.config.security.SecurityCustom;

import static com.example.demo.constant.TranslationCodeConstant.INVALID_LOGIN_INFORMATION;

import com.example.demo.constant.StringConstant;
import com.example.demo.service.TranslationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

	@Override
	public void commence(
			HttpServletRequest request,
			HttpServletResponse response,
			AuthenticationException authException)
			throws IOException, ServletException {
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		Map<String, Object> body = new LinkedHashMap<>();
		TranslationService service = new TranslationService();
		body.put(StringConstant.MESSAGE_KEY, service.getTranslation(INVALID_LOGIN_INFORMATION));
		ObjectMapper mapper = new ObjectMapper();
		mapper.writeValue(response.getOutputStream(), body);
	}
}
