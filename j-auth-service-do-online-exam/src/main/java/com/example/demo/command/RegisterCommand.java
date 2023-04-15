package com.example.demo.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterCommand {
    private String userName;
    private String email;
    private String password;
}
