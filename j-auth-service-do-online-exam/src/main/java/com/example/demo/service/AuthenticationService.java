package com.example.demo.service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.example.demo.constant.TranslationCodeConstant.INVALID_LOGIN_INFORMATION;
import static com.example.demo.constant.TranslationCodeConstant.INVALID_REGISTER_INFORMATION;
import static com.example.demo.constant.TranslationCodeConstant.VALID_REGISTER_INFORMATION;

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
            var user = userRepository.findByEmail(command.getEmail()).get();
            var userName = user.getUserName();
            var userRoles = user.getRoles();
            var roles = String.join(",", userRoles);
            return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.builder()
                    .body(Map.of("access-token", JwtTokenUtil.generateToken(command.getEmail(), roles, userName)))
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

}
