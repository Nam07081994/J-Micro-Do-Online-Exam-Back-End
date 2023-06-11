package com.example.demo.command;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterCommand {
	@NotEmpty(message = "Username is mandatory")
	private String userName;

	@NotEmpty(message = "Email is mandatory")
	private String email;

	@NotEmpty(message = "Password is mandatory")
	private String password;
}
