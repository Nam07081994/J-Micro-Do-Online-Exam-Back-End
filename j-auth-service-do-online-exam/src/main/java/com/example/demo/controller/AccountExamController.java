package com.example.demo.controller;

import com.example.demo.command.CreateAccountsExamCommand;
import com.example.demo.command.LoginAccountExamCommand;
import com.example.demo.command.SendEmailAccountExpiredCommand;
import com.example.demo.command.SendMailCommand;
import com.example.demo.service.AccountExamService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/auth/accounts-exam")
public class AccountExamController {

	private AccountExamService accountExamService;

	@PostMapping("/login")
	public ResponseEntity<?> loginAccountExam(@RequestBody @Valid LoginAccountExamCommand command) throws JsonProcessingException {
		return accountExamService.login(command);
	}

	@PostMapping("/registerAccountExam")
	public ResponseEntity<?> registerAccountsExam(
			@RequestBody @Valid CreateAccountsExamCommand command) {
		return accountExamService.registerAccountsExam(command);
	}

	@PostMapping("/sendEmail")
	public String sendEmails(@RequestBody @Valid SendEmailAccountExpiredCommand command){
		return accountExamService.sendMail(command.getEmail(), command.getBody(), command.getSubject());
	}
}
