package com.example.demo.service;

import static com.example.demo.constant.TranslationCodeConstant.NOT_FOUND_ROLE_INFORMATION;
import static com.example.demo.constant.TranslationCodeConstant.NOT_FOUND_USER_INFORMATION;

import com.example.demo.command.CreateAccountsExamCommand;
import com.example.demo.command.LoginAccountExamCommand;
import com.example.demo.common.jwt.JwtTokenUtil;
import com.example.demo.common.response.GenerateResponseHelper;
import com.example.demo.constant.StringConstant;
import com.example.demo.entity.AccountExam;
import com.example.demo.entity.Role;
import com.example.demo.repository.AccountExamRepository;
import com.example.demo.repository.RoleRepository;
import java.util.Map;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AccountExamService {

	private PasswordEncoder passwordEncoder;

	private RoleRepository roleRepository;

	private TranslationService translationService;

	private AccountExamRepository accountExamRepository;

	public ResponseEntity<?> login(LoginAccountExamCommand command) {
		Optional<AccountExam> accountOpt =
				accountExamRepository.findAccountExamByUsernameAndPassword(
						command.getUsername(), passwordEncoder.encode(command.getPassword()));

		if (accountOpt.isEmpty()) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST, translationService.getTranslation(NOT_FOUND_USER_INFORMATION));
		}
		//TODO: check time start < time login < time end

		return GenerateResponseHelper.generateDataResponse(
				HttpStatus.OK,
				Map.of(
						StringConstant.ACCESS_TOKEN_KEY,
						JwtTokenUtil.generateToken(
								accountOpt.get().getEmail(),
								StringConstant.USER_EXAM_ROLE_STRING,
								String.valueOf(accountOpt.get().getId()))));
	}

	public ResponseEntity<?> registerAccountsExam(CreateAccountsExamCommand command) {
		Optional<Role> userExamOpt =
				roleRepository.findByRoleName(StringConstant.USER_EXAM_ROLE_STRING);
		if (userExamOpt.isEmpty()) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST, translationService.getTranslation(NOT_FOUND_ROLE_INFORMATION));
		}

		command
				.getEmails()
				.forEach(
						email -> {
							String userNameAndPassword = generateUsernameAndPassword();
							String[] userNameAndPasswordArr =
									userNameAndPassword.split(StringConstant.COMMA_STRING_CHARACTER);
							AccountExam newAccount =
									AccountExam.builder()
											.username(userNameAndPasswordArr[0])
											.email(email)
											.password(passwordEncoder.encode(userNameAndPasswordArr[1]))
											.role(StringConstant.USER_EXAM_ROLE_STRING)
											.startAt(command.getStartAt())
											.endAt(command.getEndAt())
											.contestID(command.getContestID())
											.build();

							accountExamRepository.save(newAccount);
						});
		// TODO: send email to all AccountExam -> If error roll back previous step -> return BadRequest
		// status for Exam service

		return ResponseEntity.ok().build();
	}

	private void removeAccountsExam() {
		// TODO: get list account exam need to remove -> contestID end

	}

	// TODO: Create generateUsernameAndPassword func -> return "username,password" in string format
	private String generateUsernameAndPassword() {
		StringBuilder secret = new StringBuilder(StringConstant.EMPTY_STRING);
		// generate username

		secret.append(StringConstant.COMMA_STRING_CHARACTER);

		// generate password

		return secret.toString();
	}
}
