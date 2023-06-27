package com.example.demo.controller;

import com.example.demo.command.QuerySearchCommand;
import com.example.demo.command.contest.CreateContestCommand;
import com.example.demo.command.contest.CreateExamineeAccount;
import com.example.demo.common.file.CsvUtil;
import com.example.demo.common.jwt.JwtTokenUtil;
import com.example.demo.common.response.CommonResponse;
import com.example.demo.mapper.ContestMapper;
import com.example.demo.service.ContestService;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.Objects;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
			throws JsonProcessingException {
		return contestService.getContestsByOwner(
				token, QuerySearchCommand.from(from_date, to_date, page_index, page_size, order_by), name);
	}

	@GetMapping("/user")
	public ResponseEntity<?> fetchContest(@RequestHeader("Authorization") String token) {
		return contestService.getContestsByUser(token);
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getContestById(
			@PathVariable("id") Long id, @RequestHeader("Authorization") String token)
			throws JsonProcessingException {
		var username = JwtTokenUtil.getuserNameFromToken(token);
		var contest = contestService.getContestById(id);
		if (Objects.nonNull(username) && Objects.equals(contest.getCreatedBy(), username)) {
			return ResponseEntity.ok(contest);
		}
		return ResponseEntity.badRequest()
				.body(CommonResponse.message("You cannot access to this contest"));
	}

	@PostMapping("/create")
	public ResponseEntity<?> createContest(@RequestBody CreateContestCommand command) {
		var contest = ContestMapper.INSTANCE.toContest(command);
		return ResponseEntity.ok(contestService.createContest(contest));
	}

	@PostMapping("/add/examinee/csv")
	public ResponseEntity<?> addExaminee(
			@RequestHeader("Authorization") String bearerToken,
			@RequestParam("file") MultipartFile file,
			@RequestParam Long contestId) {
		var mails = CsvUtil.readFileCsv(file, CreateExamineeAccount.class);
		return contestService.createExamineeAccount(mails, bearerToken, contestId);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteContest(
			@RequestHeader("Authorization") String token, @PathVariable("id") Long id)
			throws JsonProcessingException {

		return contestService.deleteContest(token, id);
	}
}
