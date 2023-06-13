package com.example.demo.dto;

import com.example.demo.entity.User;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
	private Long id;
	private String email;
	private String username;
	private String createAt;
	private List<String> roles;

	public UserDto(User user, List<String> roles) {
		this.roles = roles;
		this.id = user.getId();
		this.email = user.getEmail();
		this.username = user.getUserName();
		this.createAt = user.getCreatedAt().toString();
	}
}
