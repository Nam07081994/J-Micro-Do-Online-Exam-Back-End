package com.example.demo.controller;

import com.example.demo.command.feedback.FeedbackCommand;
import com.example.demo.exceptions.ExecuteSQLException;
import com.example.demo.service.FeedbackService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/exams/feedback")
public class FeedbackController {

	private FeedbackService feedbackService;

	@GetMapping("/exam")
	public ResponseEntity<?> getFeedbackByExam(
			@RequestParam(value = "vote", defaultValue = "0") int vote,
			@RequestParam(name = "page_size", defaultValue = "10") int page_size,
			@RequestParam(name = "page_index", defaultValue = "1") int page_index,
			@RequestParam("name") String name,
			@RequestHeader("Authorization") @Nullable String token)
			throws JsonProcessingException, ExecuteSQLException {
		return feedbackService.getFeedbackByExamName(token, name, vote, page_index, page_size);
	}

	@GetMapping("/check")
	public ResponseEntity<?> checkUserFeedback(
			@RequestParam("id") Long id, @RequestHeader("Authorization") @Nullable String token)
			throws JsonProcessingException {
		return feedbackService.checkUserFeedback(token, id);
	}

	@GetMapping("/calculate")
	public ResponseEntity<?> calculateExamRating(@RequestParam("name") String name) {
		return feedbackService.calculateExamRatingByName(name);
	}

	@PostMapping("/create")
	public ResponseEntity<?> makeFeedback(
			@RequestHeader("Authorization") @Nullable String token,
			@RequestBody @Valid FeedbackCommand command)
			throws JsonProcessingException {
		return feedbackService.makeFeedback(token, command);
	}

	@PutMapping("/edit")
	public ResponseEntity<?> editFeedback(
			@RequestParam("id") Long id,
			@RequestHeader("Authorization") @Nullable String token,
			@RequestBody @Valid FeedbackCommand command)
			throws JsonProcessingException {
		return feedbackService.editFeedback(token, command, id);
	}

	@DeleteMapping("/delete")
	public ResponseEntity<?> deleteFeedback(
			@RequestHeader("Authorization") @Nullable String token, @RequestParam("id") Long id)
			throws JsonProcessingException {
		return feedbackService.deleteFeedback(token, id);
	}
}
