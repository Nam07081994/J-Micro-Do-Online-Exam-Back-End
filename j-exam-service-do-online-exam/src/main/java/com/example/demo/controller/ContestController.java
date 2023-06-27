package com.example.demo.controller;

import com.example.demo.command.contest.CreateContestCommand;
import com.example.demo.command.contest.CreateExamineeAccount;
import com.example.demo.command.contest.UpdateContestCommand;
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
@RequestMapping("/api/v1/contest")
@AllArgsConstructor
public class ContestController {

	private ContestService contestService;

	@GetMapping("/all")
	public ResponseEntity<?> getAllContest(@RequestHeader("Authorization") String token)
			throws JsonProcessingException {
		var username = JwtTokenUtil.getuserNameFromToken(token);
		return ResponseEntity.ok(contestService.getContestsByUserName(username));
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

	@PostMapping("/update")
	public ResponseEntity<?> updateContest(@RequestBody UpdateContestCommand command) {
		return ResponseEntity.ok(contestService.updateContest(command));
	}
}
