package com.example.demo.controller;

import com.example.demo.command.CommonSearchCommand;
import com.example.demo.command.RoleCommand;
import com.example.demo.exceptions.ExecuteSQLException;
import com.example.demo.exceptions.InvalidDateFormatException;
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
	public ResponseEntity<?> getRoles(
			@RequestParam(name = "name", required = false) String name,
			@RequestParam(name = "from_date", required = false) String from_date,
			@RequestParam(name = "to_date", required = false) String to_date,
			@RequestParam(name = "page_size", defaultValue = "10") int page_size,
			@RequestParam(name = "page_index", defaultValue = "-1") int page_index,
			@RequestParam(name = "order_by", defaultValue = "-1") int order_by)
			throws ExecuteSQLException, InvalidDateFormatException {

		return roleService.getRoles(
				CommonSearchCommand.from(from_date, to_date, page_index, page_size, order_by), name);
	}

	@GetMapping("/getEndPointsByRole")
	public ResponseEntity<?> getEndpointsByRole(@RequestParam("id") Long id) {
		return roleService.getEndpointsByRole(id);
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

	@DeleteMapping("/delete")
	public ResponseEntity<?> deleteRole(@RequestParam("id") Long id) {
		return roleService.softDeleteRole(id);
	}
}
