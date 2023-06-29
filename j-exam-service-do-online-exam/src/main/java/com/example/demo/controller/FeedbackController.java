package com.example.demo.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/exams/feedback")
public class FeedbackController {

	@PostMapping
	public ResponseEntity<?> makeFeedback() {
		return null;
	}
}
