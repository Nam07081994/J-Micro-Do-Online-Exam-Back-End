package com.example.demo.service;

import com.example.demo.command.LoginCommand;
import com.example.demo.command.RegisterCommand;
import com.example.demo.common.jwt.JwtTokenUtil;
import com.example.demo.common.response.CommonResponse;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.example.demo.constant.TranslationCodeConstant.INVALID_LOGIN_INFORMATION;
import static com.example.demo.constant.TranslationCodeConstant.VALID_LOGIN_INFORMATION;
import static com.example.demo.constant.TranslationCodeConstant.VALID_REGISTER_INFORMATION;

@Service
public class AuthenticationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private TranslationService translationService;

    public CommonResponse resister(RegisterCommand command) {
        var user = User.builder()
                .userName(command.getUserName())
                .email(command.getEmail())
                .password(passwordEncoder.encode(command.getPassword()))
                .build();

        return CommonResponse.builder()
                .message(translationService.getTranslation(VALID_REGISTER_INFORMATION)).status(HttpStatus.OK)
                .body(userRepository.save(user)).build();
    }

    public CommonResponse login(LoginCommand command) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(command.getEmail(), command.getPassword()));
        if (authentication.isAuthenticated()) {
            return CommonResponse.builder()
                    .message(translationService.getTranslation(VALID_LOGIN_INFORMATION)).status(HttpStatus.OK)
                    .body(jwtTokenUtil.generateToken(command.getEmail())).build();
        }
        return CommonResponse.builder()
                .message(translationService.getTranslation(INVALID_LOGIN_INFORMATION))
                .status(HttpStatus.UNAUTHORIZED).body(null).build();
    }

}
