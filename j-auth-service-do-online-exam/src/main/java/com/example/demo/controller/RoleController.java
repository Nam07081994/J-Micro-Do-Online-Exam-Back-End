package com.example.demo.controller;

import com.example.demo.command.RoleCommand;
import com.example.demo.service.RoleService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/auth/roles")
public class RoleController {

	private final RoleService roleService;

	@GetMapping
	public ResponseEntity<?> getRoles() {
		return roleService.getRoles();
	}

	@GetMapping("/detail")
	public ResponseEntity<?> getRoleByName(@RequestParam("name") @NotEmpty String name) {
		return roleService.getRoleByName(name);
	}

	@PostMapping("/create")
	public ResponseEntity<?> makeRole(@RequestBody @Valid RoleCommand command) {
		return roleService.makeRole(command);
	}

	@PutMapping("/edit")
	public ResponseEntity<?> editRole(
			@RequestBody @Valid RoleCommand command, @RequestParam("id") Long id) {
		return roleService.editRole(command, id);
	}
}
