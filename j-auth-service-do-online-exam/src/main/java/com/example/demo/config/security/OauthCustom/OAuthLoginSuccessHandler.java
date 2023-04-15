package com.example.demo.config.security.OauthCustom;

import java.util.Map;

import com.example.demo.repository.UserRepository;
import io.jsonwebtoken.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class OAuthLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    @Autowired
    UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException, java.io.IOException {
        super.onAuthenticationSuccess(request, response, authentication);
        CustomOAuth2User oauth2User = (CustomOAuth2User) authentication.getPrincipal();
        String oauth2ClientName = oauth2User.getOauthClientName();
        String username = oauth2User.getEmail();
        Map<String,Object> allInfo = oauth2User.getAll();

        userRepository.saveUserLoginByGoogle(username, oauth2ClientName);
    }
}
