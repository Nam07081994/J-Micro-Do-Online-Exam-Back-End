package com.example.demo.service;

import static com.example.demo.constant.StringConstant.DASH_STRING_CHARACTER;
import static com.example.demo.constant.StringConstant.EMPTY_STRING;
import static com.example.demo.constant.TranslationCodeConstant.CONTEST_TIME_ERROR;
import static com.example.demo.constant.TranslationCodeConstant.NOT_FOUND_ROLE_INFORMATION;
import static com.example.demo.constant.TranslationCodeConstant.NOT_FOUND_USER_INFORMATION;
import static com.example.demo.constant.TranslationCodeConstant.SEND_EMAIL_ERROR;
import static com.example.demo.constant.TranslationCodeConstant.SEND_EMAIL_SUCCESS;

import com.example.demo.command.CreateAccountsExamCommand;
import com.example.demo.command.LoginAccountExamCommand;
import com.example.demo.common.jwt.JwtTokenUtil;
import com.example.demo.common.mail.MailProperties;
import com.example.demo.common.response.GenerateResponseHelper;
import com.example.demo.constant.StringConstant;
import com.example.demo.entity.AccountExam;
import com.example.demo.entity.Role;
import com.example.demo.repository.AccountExamRepository;
import com.example.demo.repository.RoleRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class AccountExamService {

	private PasswordEncoder passwordEncoder;

	private RoleRepository roleRepository;

	private TranslationService translationService;

	private AccountExamRepository accountExamRepository;

	private MailProperties mailProperties;

	private JavaMailSender mailSender;

	public ResponseEntity<?> login(LoginAccountExamCommand command) {
		Optional<AccountExam> accountOpt =
				accountExamRepository.findAccountExamByUsernameAndPassword(
						command.getUsername(), passwordEncoder.encode(command.getPassword()));

		if (accountOpt.isEmpty()) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST, translationService.getTranslation(NOT_FOUND_USER_INFORMATION));
		}
		//TODO: check time start < time login < time end

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
		StringBuilder secret = new StringBuilder(EMPTY_STRING);
		// generate username
		secret.append(UUID.randomUUID().toString().substring(0,10).replaceAll(DASH_STRING_CHARACTER,EMPTY_STRING));
		secret.append(StringConstant.COMMA_STRING_CHARACTER);
		// generate password
		secret.append(UUID.randomUUID().toString().replaceAll(DASH_STRING_CHARACTER,EMPTY_STRING));
		return secret.toString();
	}

	@Transactional
	public ResponseEntity<?> sendEmails(List<String> to, String subject, String body) {
		for (String email : to) {
			ResponseEntity<?> response = sendEmail(email, subject, body);
			if (response.getStatusCode() != HttpStatus.OK) {
				return response; // Return immediately if an email fails to send
			}
		}
		return GenerateResponseHelper.generateMessageResponse(
				HttpStatus.OK, translationService.getTranslation(SEND_EMAIL_ERROR));
	}

	private ResponseEntity<?> sendEmail(String to, String subject, String body) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(to);
		message.setSubject(subject);
		message.setText(body);
		try {
			mailSender.send(message);
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.OK, translationService.getTranslation(SEND_EMAIL_SUCCESS));
		} catch (Exception e) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST, translationService.getTranslation(SEND_EMAIL_ERROR));
		}
	}


//	@Transactional
//	public ResponseEntity<?> sendEmails(List<String> to, String subject, String body) {
//		to.forEach(email -> sendEmail(email, subject, body));
//	}
//
//	private ResponseEntity<?> sendEmail(String to, String subject, String body) {
//		SimpleMailMessage message = new SimpleMailMessage();
//		message.setFrom(sender);
//		message.setTo(to);
//		message.setSubject(subject);
//		message.setText(body);
//		boolean isEmailSent = false;
//		try {
//			mailSender.send(message);
//			isEmailSent = true;
//		} catch (Exception e) {
//			isEmailSent = false;
//		}
//		if(isEmailSent == true){
//			return GenerateResponseHelper.generateMessageResponse(
//					HttpStatus.OK, translationService.getTranslation(CONTEST_TIME_ERROR));
//		}
//		return GenerateResponseHelper.generateMessageResponse(
//				HttpStatus.BAD_REQUEST, translationService.getTranslation(CONTEST_TIME_ERROR));
//	}


//	public void sendMails(List<String> emails) {
//		// Get properties object
//		Properties props = new Properties();
//		props.put("mail.smtp.auth", "true");
//		props.put("mail.smtp.host", mailProperties.getHostname());
//		props.put("mail.smtp.socketFactory.port", mailProperties.getSslport());
//		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
//		props.put("mail.smtp.port", mailProperties.getSslport());
//
//		// get Session
//		Session session =
//				Session.getDefaultInstance(
//						props,
//						new jakarta.mail.Authenticator() {
//							protected PasswordAuthentication getPasswordAuthentication() {
//								return new PasswordAuthentication(mailProperties.getEmail(), mailProperties.getPassword());
//							}
//						});
//
//		emails.forEach(email -> sendMail(email, session));
//	}
//
//	public void sendMail(String email, Session session) {
//
//		// compose message
//		try {
//			MimeMessage message = new MimeMessage(session);
//			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
//			message.setSubject("Testing Subject");
//			message.setText("Welcome to gpcoder.com");
//
//			// send message
//			Transport.send(message);
//
//			System.out.println("Message sent successfully");
//		} catch (MessagingException e) {
//			throw new RuntimeException(e);
//		}
//	}
}
