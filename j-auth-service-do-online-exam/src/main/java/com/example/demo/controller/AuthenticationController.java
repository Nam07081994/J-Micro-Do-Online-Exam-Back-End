package com.example.demo.controller;

import com.example.demo.command.CommonSearchCommand;
import com.example.demo.command.LoginCommand;
import com.example.demo.command.RegisterCommand;
import com.example.demo.common.annotations.MultipleFileExtension;
import com.example.demo.common.response.CommonResponse;
import com.example.demo.exceptions.ExecuteSQLException;
import com.example.demo.service.AuthenticationService;
import jakarta.validation.Valid;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {
	@Autowired private AuthenticationService authenticationService;

	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestBody @Valid RegisterCommand command) {
		return authenticationService.resister(command);
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody @Valid LoginCommand command) {
		// TODO: subscribe new user login -> notification service
		return authenticationService.login(command);
	}

	@PostMapping("/logout")
	public ResponseEntity<?> logout(@RequestParam("clientID") String clientID) {
		// TODO: remove clientID out of notify topic -> push event to queue
		return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.builder().build().getBody());
	}

	@GetMapping("/users")
	public ResponseEntity<?> getUsers(
			@RequestParam(name = "username", required = false) String username,
			@RequestParam(name = "email", required = false) String email,
			@RequestParam(name = "from_date", required = false) String from_date,
			@RequestParam(name = "to_date", required = false) String to_date,
			@RequestParam(name = "page_size", defaultValue = "10") int page_size,
			@RequestParam(name = "page_index", defaultValue = "-1") int page_index,
			@RequestParam(name = "order_by", defaultValue = "-1") int order_by)
			throws ExecuteSQLException {
		return authenticationService.getUsers(
				CommonSearchCommand.from(from_date, to_date, page_index, page_size, order_by),
				email,
				username);
	}

	@GetMapping("/getEndPointsByRoles")
	public Set<String> getEndPoints(@RequestParam(name = "roles") String roles) {
		return authenticationService.getEndPoint(roles);
	}

	@GetMapping("/user/info")
	public ResponseEntity<?> getUserInfo(@RequestHeader("Authorization") String token) {
		return authenticationService.getUserInfo(token);
	}

	@PostMapping("/update/thumbnail")
	public ResponseEntity<?> updateUserThumbnail(
			@RequestHeader("Authorization") String token,
			@RequestParam("file") @MultipleFileExtension MultipartFile file) {
		return authenticationService.updateUserThumbnail(token, file);
	}

	@PostMapping("/refreshToken")
	public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String token) {
		return authenticationService.refreshToken(token);
	}
}
