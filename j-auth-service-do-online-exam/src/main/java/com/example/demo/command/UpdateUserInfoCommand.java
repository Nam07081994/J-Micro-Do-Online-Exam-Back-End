package com.example.demo.command;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserInfoCommand {
	@NotEmpty(message = "Username is mandatory")
	private String userName;

	@NotEmpty(message = "Email is mandatory")
	private String email;
}
