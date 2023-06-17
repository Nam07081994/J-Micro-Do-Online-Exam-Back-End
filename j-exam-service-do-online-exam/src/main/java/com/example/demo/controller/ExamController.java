package com.example.demo.controller;

import static com.example.demo.constant.Constant.FETCH_EXAM;
import static com.example.demo.constant.Constant.GET_EXAM_CARD;

import com.example.demo.command.QuerySearchCommand;
import com.example.demo.command.exam.CreateExamCommand;
import com.example.demo.command.exam.EditExamCommand;
import com.example.demo.exceptions.ExecuteSQLException;
import com.example.demo.service.ExamService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import java.io.IOException;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/exams")
public class ExamController {
	private ExamService examService;

	@GetMapping("/get")
	public ResponseEntity<?> getExams(
			@RequestHeader("Authorization") String token,
			@RequestParam(name = "name", required = false) String name,
			@RequestParam(name = "duration", defaultValue = "-1") int duration,
			@RequestParam(name = "category_ids", defaultValue = "-1") String category_ids,
			@RequestParam(name = "from_date", required = false) String from_date,
			@RequestParam(name = "to_date", required = false) String to_date,
			@RequestParam(name = "page_size", defaultValue = "10") int page_size,
			@RequestParam(name = "page_index", defaultValue = "-1") int page_index,
			@RequestParam(name = "order_by", defaultValue = "-1") int order_by)
			throws JsonProcessingException, ExecuteSQLException {

		return examService.getAllExam(
				QuerySearchCommand.from(from_date, to_date, page_index, page_size, order_by),
				token,
				name,
				category_ids,
				duration);
	}

	@GetMapping("/options")
	public ResponseEntity<?> getExamsOption(@RequestHeader("Authorization") String token)
			throws JsonProcessingException {
		return examService.getExamsOption(token);
	}

	@GetMapping("/fetch")
	public ResponseEntity<?> fetchExamByName(
			@RequestHeader("Authorization") String token, @RequestParam("name") String name)
			throws JsonProcessingException {
		return examService.getExamByName(token, name, FETCH_EXAM);
	}

	@GetMapping("/name")
	public ResponseEntity<?> getExamByName(
			@RequestHeader("Authorization") String token, @RequestParam("name") String name)
			throws JsonProcessingException {
		return examService.getExamByName(token, name, GET_EXAM_CARD);
	}

	@PostMapping("/create")
	public ResponseEntity<?> createExam(
			@RequestHeader("Authorization") String token,
			@ModelAttribute @Valid CreateExamCommand command)
			throws IOException {
		return examService.createExam(command, token);
	}

	@PostMapping("/edit")
	public ResponseEntity<?> editExam(
			@RequestHeader("Authorization") String token, @ModelAttribute EditExamCommand command)
			throws JsonProcessingException {
		return examService.editExam(token, command);
	}

	@GetMapping("/durations")
	public ResponseEntity<?> getExamsDuration() {
		return examService.getExamsDurationOption();
	}

	@GetMapping("/downloadExam")
	public ResponseEntity<?> downloadExam(
			@RequestHeader("Authorization") String token, @RequestParam Long examId)
			throws JsonProcessingException {
		return examService.generateAndDownloadExamPDF(token, examId);
	}

	@DeleteMapping("/delete")
	public ResponseEntity<?> deleteExam(
			@RequestHeader("Authorization") String token, @RequestParam("id") Long id)
			throws JsonProcessingException {
		return examService.deleteExam(id, token);
	}
}
