package com.example.demo.controller;

import com.example.demo.command.QuerySearchCommand;
import com.example.demo.command.contest.MakeContestCommand;
import com.example.demo.exceptions.ExecuteSQLException;
import com.example.demo.exceptions.InvalidDateFormatException;
import com.example.demo.service.ContestService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/exams/contests")
@AllArgsConstructor
public class ContestController {

	private ContestService contestService;

	// owner fetch contest
	@GetMapping("/owner")
	public ResponseEntity<?> getAllContest(
			@RequestHeader("Authorization") String token,
			@RequestParam(name = "name", required = false) String name,
			@RequestParam(name = "from_date", required = false) String from_date,
			@RequestParam(name = "to_date", required = false) String to_date,
			@RequestParam(name = "page_size", defaultValue = "10") int page_size,
			@RequestParam(name = "page_index", defaultValue = "-1") int page_index,
			@RequestParam(name = "order_by", defaultValue = "-1") int order_by)
			throws JsonProcessingException, InvalidDateFormatException, ExecuteSQLException {
		return contestService.getContestsByOwner(
				token, QuerySearchCommand.from(from_date, to_date, page_index, page_size, order_by), name);
	}

	//user exam fetch contest exam
	@GetMapping("/user")
	public ResponseEntity<?> fetchContest(@RequestHeader("Authorization") String token)
			throws JsonProcessingException {
		return contestService.getContestByUser(token);
	}

	//get detail contest
	@GetMapping("/get")
	public ResponseEntity<?> getContestById(
			@RequestHeader("Authorization") String token, @RequestParam("id") Long id)
			throws JsonProcessingException {
		return contestService.getContestById(id, token);
	}

	@PostMapping(
			value = "/create",
			consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
	public ResponseEntity<?> createContest(
			@RequestHeader("Authorization") String token,
			@ModelAttribute @Valid MakeContestCommand command)
			throws JsonProcessingException {

		return contestService.createContest(token, command);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteContest(
			@RequestHeader("Authorization") String token, @PathVariable("id") Long id)
			throws JsonProcessingException {

		return contestService.deleteContest(token, id);
	}
}
