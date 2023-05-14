package com.example.demo.service;

import java.util.*;
import java.util.stream.Collectors;

import com.example.demo.command.LoginCommand;
import com.example.demo.command.RegisterCommand;
import com.example.demo.common.jwt.JwtTokenUtil;
import com.example.demo.common.response.CommonResponse;
import com.example.demo.config.jpa.JpaConfig;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.example.demo.constant.TranslationCodeConstant.*;

@Service
public class AuthenticationService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TranslationService translationService;

    public ResponseEntity<?> resister(RegisterCommand command) {
        var userExist = userRepository.findByEmail(command.getEmail());
        if(userExist.isPresent()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CommonResponse.builder()
                    .body(Map.of("message", translationService.getTranslation(INVALID_REGISTER_INFORMATION)))
                    .build()
                    .getBody());
        }
        var user = User.builder()
                .userName(command.getUserName())
                .email(command.getEmail())
                .password(passwordEncoder.encode(command.getPassword()))
                .roles(List.of("USER"))
                .build();
        JpaConfig.setRegisteredUser(user.getUserName());
        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.builder()
                .body(Map.of("message", translationService.getTranslation(VALID_REGISTER_INFORMATION)))
                .build()
                .getBody());
    }

    public ResponseEntity<?> login(LoginCommand command) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(command.getEmail(), command.getPassword()));
        if (authentication.isAuthenticated()) {
            var userRoles = userRepository.findByEmail(command.getEmail()).get().getRoles();
            var roles = String.join(",", userRoles);
            return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.builder()
                    .body(Map.of("access-token", JwtTokenUtil.generateToken(command.getEmail(), roles)))
                    .build()
                    .getBody());
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(CommonResponse.builder()
                .body(Map.of("message", translationService.getTranslation(INVALID_LOGIN_INFORMATION)))
                .build()
                .getBody());
    }

    public Set<String> getEndPoint(String email){
        var userExist = userRepository.findByEmail(email);
        if(!userExist.isPresent()){
            return null;
        }
        var userRoles = userExist.get().getRoles();

        var roles = userRoles.stream()
                .flatMap(roleName -> roleRepository.findByRoleName(roleName)
                .stream())
                .collect(Collectors.toList());

        var endPoints = roles.stream()
                .map(Role::getEndPoint)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        Set<String> targetEndPoints = new HashSet<>(endPoints);
        return targetEndPoints;
    }

    public ResponseEntity<?> refreshToken(String token) throws JsonProcessingException {
        var tokenExpired = JwtTokenUtil.isTokenExpired(token);
        if (!tokenExpired) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CommonResponse.builder()
                    .body(Map.of("message", translationService.getTranslation(INVALID_TOKEN_INFORMATION)))
                    .build()
                    .getBody());
        }
        var email = JwtTokenUtil.getEmailFromToken(token);
        var userRoles = userRepository.findByEmail(email);
        if (!userRoles.isPresent()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CommonResponse.builder()
                    .body(Map.of("message", translationService.getTranslation(INVALID_USER_DUPLICATE_INFORMATION)))
                    .build()
                    .getBody());
        }
        var roles = String.join(",", userRoles.get().getRoles());
        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.builder()
                .body(Map.of("refresh-token", JwtTokenUtil.generateToken(email, roles)))
                .build()
                .getBody());
    }
}
