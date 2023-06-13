package com.example.demo.command;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LoginAccountExamCommand {
	@NotEmpty(message = "Username is mandatory")
	private String username;

	@NotEmpty(message = "Password is mandatory")
	private String password;
}
