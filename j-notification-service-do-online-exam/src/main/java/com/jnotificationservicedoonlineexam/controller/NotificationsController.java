package com.jnotificationservicedoonlineexam.controller;

import static com.jnotificationservicedoonlineexam.constants.Constants.EMPTY_STRING;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jnotificationservicedoonlineexam.command.QuerySearchCommand;
import com.jnotificationservicedoonlineexam.exceptions.ExecuteSQLException;
import com.jnotificationservicedoonlineexam.service.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/notify")
public class NotificationsController {

	private NotificationService notificationService;

	@GetMapping("/user")
	public ResponseEntity<?> getNotificationsByUser(
			@RequestHeader("Authorization") String token,
			@RequestParam(name = "page_size", defaultValue = "10") int page_size,
			@RequestParam(name = "page_index", defaultValue = "-1") int page_index)
			throws JsonProcessingException, ExecuteSQLException {

		return notificationService.getNotificationsByUser(
				QuerySearchCommand.from(EMPTY_STRING, EMPTY_STRING, page_size, page_index, 3), token);
	}
}
