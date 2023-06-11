package com.example.demo.service;

import com.example.demo.config.mail.MailProperties;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.List;
import java.util.Properties;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MailService {

	private MailProperties mailProperties;

	public void sendMails(List<String> emails) {
		// Get properties object
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.host", mailProperties.getHostname());
		props.put("mail.smtp.socketFactory.port", mailProperties.getSslport());
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.port", mailProperties.getSslport());

		// get Session
		Session session =
				Session.getDefaultInstance(
						props,
						new jakarta.mail.Authenticator() {
							protected PasswordAuthentication getPasswordAuthentication() {
								return new PasswordAuthentication(mailProperties.getEmail(), mailProperties.getPassword());
							}
						});

		emails.forEach(email -> sendMail(email, session));
	}

	public void sendMail(String email, Session session) {

		// compose message
		try {
			MimeMessage message = new MimeMessage(session);
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
			message.setSubject("Testing Subject");
			message.setText("Welcome to gpcoder.com");

			// send message
			Transport.send(message);

			System.out.println("Message sent successfully");
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}
}
