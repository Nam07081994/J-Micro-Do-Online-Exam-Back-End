package com.example.demo.dto;

import com.example.demo.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RoleDto {
	private Long id;
	private String roleName;
	private String createdAt;

	public RoleDto(Role role) {
		this.id = role.getId();
		this.roleName = role.getRoleName();
		this.createdAt = role.getCreatedAt().toString();
	}
}
