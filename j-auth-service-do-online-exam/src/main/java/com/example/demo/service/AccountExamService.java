package com.example.demo.service;

import static com.example.demo.constant.StringConstant.EMAIL_BODY;
import static com.example.demo.constant.StringConstant.EMAIL_SENDING_SUCCESS;
import static com.example.demo.constant.StringConstant.EMAIL_SUBJECT;
import static com.example.demo.constant.StringConstant.EMAIL_WHILE_SENDING_ERROR;
import static com.example.demo.constant.StringConstant.EMPTY_STRING;
import static com.example.demo.constant.StringConstant.EXAM_PASSWORD_PREFIX;
import static com.example.demo.constant.StringConstant.EXAM_USERNAME_PREFIX;
import static com.example.demo.constant.StringConstant.HYPHEN_STRING_CHARACTER;
import static com.example.demo.constant.StringConstant.LOGIN_CONTEST_LINK;
import static com.example.demo.constant.TranslationCodeConstant.CONTEST_TIME_ERROR;
import static com.example.demo.constant.TranslationCodeConstant.EMPTY_USER_INFO;
import static com.example.demo.constant.TranslationCodeConstant.NOT_FOUND_ROLE_INFORMATION;
import static com.example.demo.constant.TranslationCodeConstant.NOT_FOUND_USER_INFORMATION;
import static com.example.demo.constant.TranslationCodeConstant.SEND_EMAIL_ERROR;

import com.example.demo.command.CreateAccountsExamCommand;
import com.example.demo.command.LoginAccountExamCommand;
import com.example.demo.common.jwt.JwtTokenUtil;
import com.example.demo.common.response.GenerateResponseHelper;
import com.example.demo.constant.StringConstant;
import com.example.demo.entity.AccountExam;
import com.example.demo.entity.Role;
import com.example.demo.repository.AccountExamRepository;
import com.example.demo.repository.RoleRepository;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountExamService {
	@Value("${spring.mail.username}") private String sender;

	@Autowired private PasswordEncoder passwordEncoder;

	@Autowired private RoleRepository roleRepository;

	@Autowired private TranslationService translationService;

	@Autowired private AccountExamRepository accountExamRepository;

	@Autowired private JavaMailSender mailSender;

	public ResponseEntity<?> login(LoginAccountExamCommand command) {
		Optional<AccountExam> accountOpt =
				accountExamRepository.findAccountExamByUsernameAndPassword(
						command.getUsername(), passwordEncoder.encode(command.getPassword()));

		if (accountOpt.isEmpty()) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST, translationService.getTranslation(NOT_FOUND_USER_INFORMATION));
		}
		LocalDateTime loginTime = LocalDateTime.now();
		if(accountOpt.get().getStartAt().isAfter(loginTime) || accountOpt.get().getEndAt().isAfter(loginTime)) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST, translationService.getTranslation(CONTEST_TIME_ERROR));
		}

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
		if(command.getUserInfo().isEmpty()){
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST, translationService.getTranslation(EMPTY_USER_INFO));
		}
		Map<String, List<String>> emailMapBody = new HashMap<>();
		command.getUserInfo().forEach((userName, email) -> {
			String userNameAndPassword = generateUsernameAndPassword();
			String[] userNameAndPasswordArr =
					userNameAndPassword.split(StringConstant.COMMA_STRING_CHARACTER);
			AccountExam newAccount =
					AccountExam.builder()
							.username(userNameAndPasswordArr[0])
							.email(email)
							.password(userNameAndPasswordArr[1])
							.role(StringConstant.USER_EXAM_ROLE_STRING)
							.startAt(command.getStartAt())
							.endAt(command.getEndAt())
							.contestID(command.getContestID())
							.build();
			accountExamRepository.save(newAccount);
			List<String> infoList = new ArrayList<>();
			infoList.add(newAccount.getPassword());
			infoList.add(userNameAndPasswordArr[0]);
			infoList.add(newAccount.getEmail());
			emailMapBody.put(userName, infoList);
		});
		for (Map.Entry<String, List<String>> entry : emailMapBody.entrySet()) {
			String userName = entry.getKey();
			List<String> value = entry.getValue();

			String emailBody = String.format(EMAIL_BODY, userName, command.getContestID(), value.get(1), value.get(0), LOGIN_CONTEST_LINK, LOGIN_CONTEST_LINK, formatDateTime(command.getStartAt()), formatDateTime(command.getEndAt()));
			var emailSend = sendMail(value.get(2), emailBody, EMAIL_SUBJECT);

			if (emailSend.equals(EMAIL_WHILE_SENDING_ERROR)) {
				return GenerateResponseHelper.generateMessageResponse(
						HttpStatus.BAD_REQUEST, translationService.getTranslation(SEND_EMAIL_ERROR));
			}
		}
		return ResponseEntity.ok().build();
	}

	public void removeAccountsExam() {
		accountExamRepository.deleteAll();
	}

	private String generateUsernameAndPassword() {
		StringBuilder secret = new StringBuilder(EMPTY_STRING);
		secret.append(EXAM_USERNAME_PREFIX + UUID.randomUUID().toString().replaceAll(HYPHEN_STRING_CHARACTER, EMPTY_STRING).substring(0,12));
		secret.append(StringConstant.COMMA_STRING_CHARACTER);
		secret.append(EXAM_PASSWORD_PREFIX + UUID.randomUUID().toString().replaceAll(HYPHEN_STRING_CHARACTER, EMPTY_STRING).substring(0,12));
		return secret.toString();
	}

	public String sendEmails(List<String>emails, String body, String subject) {
			var successfulCount = 0;
			for (String email : emails) {
				String result = sendMail(email, body, subject);
				if (result == null) {
					successfulCount++;
				}
			}

			if (successfulCount == emails.size()) {
				return EMAIL_SENDING_SUCCESS;
			} else {
				return EMAIL_WHILE_SENDING_ERROR;
			}
	}

	@Transactional
	public String sendMail(String email, String body, String subject) {
		boolean html = true;
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		MimeMessageHelper mimeMessageHelper;
		try {
			mimeMessageHelper
					= new MimeMessageHelper(mimeMessage, true);
			mimeMessageHelper.setFrom(sender);
			mimeMessageHelper.setTo(email);
			mimeMessageHelper.setText(body, true);
			mimeMessageHelper.setSubject(subject);
			mailSender.send(mimeMessage);
			return EMAIL_SENDING_SUCCESS;
		}
		catch (MessagingException e) {
			return EMAIL_WHILE_SENDING_ERROR;
		}
	}

	private String formatDateTime(LocalDateTime inputDateTime) {
		try {
			DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("h:mm a 'on' MMMM d, yyyy");
			return inputDateTime.format(inputFormatter);
		} catch (DateTimeParseException e) {
			e.printStackTrace();
			return "";
		}
	}
}
