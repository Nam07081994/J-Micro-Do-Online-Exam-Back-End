package com.example.demo.config.security.SecurityCustom;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.example.demo.service.TranslationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import static com.example.demo.constant.TranslationCodeConstant.INVALID_LOGIN_INFORMATION;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // create custom response
        Map<String, Object> body = new LinkedHashMap<>();
        TranslationService service = new TranslationService();
        body.put("message", service.getTranslation(INVALID_LOGIN_INFORMATION));

        // pass response to client
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), body);
    }
}
