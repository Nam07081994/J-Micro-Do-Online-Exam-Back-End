package com.example.demo.controller;

import static com.example.demo.constant.Constant.*;

import com.example.demo.command.QuerySearchCommand;
import com.example.demo.command.exam.CreateExamCommand;
import com.example.demo.command.exam.EditExamCommand;
import com.example.demo.command.exam.SubmitExamCommand;
import com.example.demo.command.exam.UpdateExamThumbnailCommand;
import com.example.demo.common.jwt.JwtTokenUtil;
import com.example.demo.common.response.GenerateResponseHelper;
import com.example.demo.exceptions.ExecuteSQLException;
import com.example.demo.exceptions.InvalidDateFormatException;
import com.example.demo.service.ExamService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/exams")
public class ExamController {
	private ExamService examService;

	@GetMapping("/get")
	public ResponseEntity<?> getExams(
			@RequestHeader("Authorization") @Nullable String token,
			@RequestParam(name = "name", required = false) String name,
			@RequestParam(name = "durations", required = false) String duration,
			@RequestParam(name = "category_ids", required = false) String category_ids,
			@RequestParam(name = "from_date", required = false) String from_date,
			@RequestParam(name = "to_date", required = false) String to_date,
			@RequestParam(name = "page_size", defaultValue = "10") int page_size,
			@RequestParam(name = "page_index", defaultValue = "-1") int page_index,
			@RequestParam(name = "order_by", defaultValue = "-1") int order_by)
			throws JsonProcessingException, ExecuteSQLException, InvalidDateFormatException {

		return examService.getAllExam(
				QuerySearchCommand.from(from_date, to_date, page_index, page_size, order_by),
				token,
				name,
				category_ids,
				duration);
	}

	@GetMapping("/hot/category")
	public ResponseEntity<?> getHotExamsByCategory() {
		return examService.fetchExamByCategory();
	}

	@GetMapping("/options")
	public ResponseEntity<?> getExamsOption(@RequestHeader("Authorization") String token)
			throws JsonProcessingException {
		return examService.getExamsOption(token);
	}

	@GetMapping("/random")
	public ResponseEntity<?> getRandomExams(@RequestParam("name") String name) {
		return examService.getRandomExams(name);
	}

	@GetMapping("/fetch")
	public ResponseEntity<?> fetchExamByName(
			@RequestHeader("Authorization") String token, @RequestParam("name") String name)
			throws JsonProcessingException {
		return examService.getExamByName(token, name, FETCH_EXAM);
	}

	@GetMapping("/name")
	public ResponseEntity<?> getExamByName(
			@RequestHeader("Authorization") @Nullable String token, @RequestParam("name") String name)
			throws JsonProcessingException {
		return examService.getExamByName(token, name, GET_EXAM_CARD);
	}

	@GetMapping("/detail")
	public ResponseEntity<?> getExamEdit(
			@RequestHeader("Authorization") String token, @RequestParam("id") Long id)
			throws JsonProcessingException {
		return examService.getExamEdit(token, id);
	}

	@PostMapping(
			value = "/create",
			consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
	public ResponseEntity<?> createExam(
			@RequestHeader("Authorization") String token,
			@ModelAttribute @Valid CreateExamCommand command)
			throws IOException {
		return examService.createExam(command, token);
	}

	@PostMapping("/edit")
	public ResponseEntity<?> editExam(
			@RequestHeader("Authorization") String token, @RequestBody @Valid EditExamCommand command)
			throws JsonProcessingException {
		return examService.editExam(token, command);
	}

	@PutMapping(
			value = "/update-thumbnail",
			consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
	public ResponseEntity<?> updateExamThumbnail(
			@RequestHeader("Authorization") String token,
			@ModelAttribute @Valid UpdateExamThumbnailCommand command)
			throws IOException {
		return examService.updateExamThumbnail(token, command);
	}

	@GetMapping("/durations")
	public ResponseEntity<?> getExamsDuration() {
		return examService.getExamsDurationOption();
	}

	@GetMapping("/orderByOptions")
	public ResponseEntity<?> getExamOrderByOptions(
			@RequestHeader("Authorization") @Nullable String token) {
		List<Object> orderOptions = new ArrayList<>();
		orderOptions.add(Map.of(1, "Order by name ASC"));
		orderOptions.add(Map.of(2, "Order by name DESC"));
		orderOptions.add(Map.of(3, "Order by duration ASC"));
		orderOptions.add(Map.of(4, "Order by duration DESC"));
		if (token != null && !JwtTokenUtil.getTokenWithoutBearer(token).equals("null")) {
			orderOptions.add(Map.of(5, "Order by createAt ASC"));
			orderOptions.add(Map.of(6, "Order by createAt DESC"));
		}

		return GenerateResponseHelper.generateDataResponse(
				HttpStatus.OK, Map.of(DATA_KEY, orderOptions));
	}

	@GetMapping("/downloadExam")
	public ResponseEntity<?> downloadExam(
			@RequestHeader("Authorization") String token, @RequestParam Long examId) throws IOException {
		return examService.generateAndDownloadExamPDF(token, examId);
	}

	@DeleteMapping("/delete")
	public ResponseEntity<?> deleteExam(
			@RequestHeader("Authorization") String token, @RequestParam("id") Long id)
			throws JsonProcessingException {
		return examService.deleteExam(id, token);
	}

	@PostMapping("/submit")
	public ResponseEntity<?> submitExam(
			@RequestHeader("Authorization") String token, @RequestBody @Valid SubmitExamCommand command)
			throws JsonProcessingException {
		return examService.checkExam(token, command);
	}
}
