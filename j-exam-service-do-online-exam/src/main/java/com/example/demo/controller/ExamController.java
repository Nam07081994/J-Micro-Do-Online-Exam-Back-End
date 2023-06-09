package com.example.demo.controller;

import com.example.demo.command.CreateExamCommand;
import com.example.demo.service.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/exam/exam")
public class ExamController {
	@Autowired private ExamService examService;

	@PostMapping("/create")
	public ResponseEntity<?> createExam(@RequestBody CreateExamCommand command) {
		return examService.createExam(command);
	}

	@GetMapping("/downloadExam")
	public ResponseEntity<?> downloadExam(@RequestParam Long examId) {
		return examService.generateAndDownloadExamPDF(examId);
	}
}
