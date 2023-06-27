package com.example.demo.service;

import com.example.demo.command.contest.CreateExamineeAccount;
import com.example.demo.command.contest.UpdateContestCommand;
import com.example.demo.dto.CreateExamineeAccountDto;
import com.example.demo.entity.Contest;
import com.example.demo.mapper.ContestMapper;
import com.example.demo.repository.ContestRepository;
import jakarta.ws.rs.BadRequestException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@AllArgsConstructor
public class ContestService {
	private ContestRepository contestRepository;

	public List<Contest> getContestsByUserName(String username) {
		return contestRepository.getContestsByCreatedBy(username);
	}

	public Contest getContestById(Long id) {
		return contestRepository
				.findById(id)
				.orElseThrow(() -> new BadRequestException("Cannot find contestId"));
	}

	public Contest createContest(Contest contest) {
		return contestRepository.save(contest);
	}

	public Contest updateContest(UpdateContestCommand command) {
		var contest =
				contestRepository
						.findById(command.getId())
						.orElseThrow(() -> new BadRequestException("Contest's id cannot be null"));
		ContestMapper.INSTANCE.updateContest(command, contest);
		return contestRepository.save(contest);
	}

	public ResponseEntity<?> createExamineeAccount(
			List<CreateExamineeAccount> list, String bearerToken, Long contestId) {
		var contest = contestRepository.findById(contestId).orElse(null);
		if (contest == null) {
			return new ResponseEntity<>("Invalid contestId", HttpStatus.BAD_REQUEST);
		}

		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", bearerToken);
		headers.setContentType(MediaType.APPLICATION_JSON);

		CreateExamineeAccountDto dto =
				CreateExamineeAccountDto.builder()
						.contestID(contestId)
						.startAt(contest.getStartAt())
						.endAt(contest.getEndAt())
						.userInfo(
								list.stream()
										.map(
												account ->
														CreateExamineeAccountDto.User.builder()
																.username(account.getUsername())
																.email(account.getEmail())
																.build())
										.collect(Collectors.toList()))
						.build();
		HttpEntity<CreateExamineeAccountDto> requestEntity = new HttpEntity<>(dto, headers);

		String url = "http://localhost:8764/api/v1/auth/accounts-exam/registerAccountExam";
		ResponseEntity<String> response =
				restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

		if (response.getStatusCode() != HttpStatus.OK) {
			return new ResponseEntity<>("Cannot create ExamineeAccount", HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(response.getBody(), HttpStatus.OK);
	}
}
