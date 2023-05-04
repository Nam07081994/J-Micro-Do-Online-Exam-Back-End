package com.example.demo.controller;

import java.util.Set;

import com.example.demo.command.LoginCommand;
import com.example.demo.command.RegisterCommand;
import com.example.demo.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {
    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterCommand command){
        return authenticationService.resister(command);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginCommand command){
        return authenticationService.login(command);
    }

    @GetMapping("/getEndPoints")
    public Set<String> getEndPoints(@RequestParam(name = "email") String email){
        return authenticationService.getEndPoint(email);
    }
}
