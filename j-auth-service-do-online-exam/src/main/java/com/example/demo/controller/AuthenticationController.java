package com.example.demo.controller;

import com.example.demo.command.LoginCommand;
import com.example.demo.command.RegisterCommand;
import com.example.demo.common.response.CommonResponse;
import com.example.demo.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {
    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/register")
    public CommonResponse register(@RequestBody RegisterCommand command){
        return authenticationService.resister(command);
    }

    @PostMapping("/login")
    public CommonResponse login(@RequestBody LoginCommand command){
        return authenticationService.login(command);
    }
}
