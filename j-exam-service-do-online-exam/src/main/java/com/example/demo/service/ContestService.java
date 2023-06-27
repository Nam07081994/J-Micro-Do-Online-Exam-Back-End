package com.example.demo.service;

import static com.example.demo.constant.Constant.USER_ID_TOKEN_KEY;
import static com.example.demo.constant.SQLConstants.*;
import static com.example.demo.constant.TranslationCodeConstants.*;

import com.example.demo.command.QuerySearchCommand;
import com.example.demo.command.contest.CreateExamineeAccount;
import com.example.demo.common.jwt.JwtTokenUtil;
import com.example.demo.common.query.QueryCondition;
import com.example.demo.common.response.GenerateResponseHelper;
import com.example.demo.dto.CreateExamineeAccountDto;
import com.example.demo.entity.Contest;
import com.example.demo.repository.ContestRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.ws.rs.BadRequestException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ContestService {

	@Value("${app.url.create-accounts-exam-endpoint}")
	private String CREATE_ACCOUNTS_EXAM_URI;

	@Autowired private TranslationService translationService;

	@Autowired private ContestRepository contestRepository;

	public ResponseEntity<?> getContestsByOwner(String token, QuerySearchCommand command, String name)
			throws JsonProcessingException {
		Map<String, QueryCondition> searchParams = new HashMap<>();
		Long userID =
				Long.valueOf(
						JwtTokenUtil.getUserInfoFromToken(
								JwtTokenUtil.getTokenWithoutBearer(token), USER_ID_TOKEN_KEY));

		searchParams.put(
				CONTEST_OWNER_SEARCH_KEY,
				QueryCondition.builder().value(userID).operation(EQUAL_OPERATOR).build());

		if (!StringUtils.isEmpty(name)) {
			searchParams.put(
					CONTEST_NAME_SEARCH_KEY,
					QueryCondition.builder().value(name).operation(LIKE_OPERATOR).build());
		}

		return null;
	}

	public ResponseEntity<?> getContestsByUser(String token) {
		// TODO: token -> get email user -> loop through list participants -> collection contest
		// available -> return {contestName,startAt,endAt, examID}

		return null;
	}

	public Contest getContestById(Long id) {
		return contestRepository
				.findById(id)
				.orElseThrow(() -> new BadRequestException("Cannot find contestId"));
	}

	public Contest createContest(Contest contest) {
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

		ResponseEntity<String> response =
				restTemplate.exchange(
						CREATE_ACCOUNTS_EXAM_URI, HttpMethod.POST, requestEntity, String.class);

		if (response.getStatusCode() != HttpStatus.OK) {
			return new ResponseEntity<>("Cannot create ExamineeAccount", HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(response.getBody(), HttpStatus.OK);
	}

	public ResponseEntity<?> deleteContest(String token, Long id) throws JsonProcessingException {
		Long userID =
				Long.valueOf(
						JwtTokenUtil.getUserInfoFromToken(
								JwtTokenUtil.getTokenWithoutBearer(token), USER_ID_TOKEN_KEY));
		Optional<Contest> contestOpt = contestRepository.findById(id);
		if (contestOpt.isEmpty()) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST, translationService.getTranslation(NOT_FOUND_CONTEST));
		}

		if (userID.compareTo(contestOpt.get().getOwnerID()) != 0) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST, translationService.getTranslation(NOT_YOUR_OWNER_CONTEST));
		}

		if (contestOpt.get().getEndAt().isBefore(LocalDateTime.now())) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST, translationService.getTranslation(CONTEST_NOT_FINISH));
		}

		contestRepository.deleteById(id);

		return GenerateResponseHelper.generateMessageResponse(
				HttpStatus.OK, translationService.getTranslation(DELETE_CONTEST_SUCCESS));
	}
}
