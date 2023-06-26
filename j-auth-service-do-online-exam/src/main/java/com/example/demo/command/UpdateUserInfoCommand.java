package com.example.demo.command;

import jakarta.validation.constraints.Email;
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

	@Email(message = "Invalid email format")
	@NotEmpty(message = "Email is mandatory")
	private String email;

	private String phone;

	private String birthday;

	private String address;
}
