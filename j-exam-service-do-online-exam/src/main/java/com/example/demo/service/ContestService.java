package com.example.demo.service;

import static com.example.demo.constant.Constant.*;
import static com.example.demo.constant.SQLConstants.*;
import static com.example.demo.constant.TranslationCodeConstants.*;

import com.example.demo.command.QuerySearchCommand;
import com.example.demo.command.contest.CreateExamineeAccount;
import com.example.demo.command.contest.MakeContestCommand;
import com.example.demo.common.file.CsvUtil;
import com.example.demo.common.jwt.JwtTokenUtil;
import com.example.demo.common.query.QueryCondition;
import com.example.demo.common.query.QueryDateCondition;
import com.example.demo.common.response.GenerateResponseHelper;
import com.example.demo.dto.CreateExamineeAccountDto;
import com.example.demo.dto.contest.AccountExamDto;
import com.example.demo.dto.contest.ContestDetailDto;
import com.example.demo.dto.contest.ContestOwnerDto;
import com.example.demo.dto.contest.ContestUserDto;
import com.example.demo.entity.Contest;
import com.example.demo.entity.Exam;
import com.example.demo.exceptions.ExecuteSQLException;
import com.example.demo.exceptions.InvalidDateFormatException;
import com.example.demo.repository.ContestRepository;
import com.example.demo.repository.ExamRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class ContestService {

	@Value("${app.url.create-accounts-exam-endpoint}")
	private String CREATE_ACCOUNTS_EXAM_URI;

	@Value("${app.url.check-upload-exam-endpoint}")
	private String CHECK_USER_UPLOAD_URI;

	@Value("${app.format.date}")
	private String DEFAULT_DATE_FORMAT;

	@Autowired private RestTemplate restTemplate;

	@Autowired private ExamRepository examRepository;

	@Autowired private TranslationService translationService;

	@Autowired private ContestRepository contestRepository;

	// TODO: need test
	public ResponseEntity<?> getContestsByOwner(String token, QuerySearchCommand command, String name)
			throws JsonProcessingException, InvalidDateFormatException, ExecuteSQLException {
		Map<String, QueryCondition> searchParams = new HashMap<>();
		Long userID =
				Long.valueOf(
						JwtTokenUtil.getUserInfoFromToken(
								JwtTokenUtil.getTokenWithoutBearer(token), USER_ID_TOKEN_KEY));
		String userName =
				JwtTokenUtil.getUserInfoFromToken(
						JwtTokenUtil.getTokenWithoutBearer(token), USER_NAME_TOKEN_KEY);

		searchParams.put(
				CONTEST_OWNER_SEARCH_KEY,
				QueryCondition.builder().value(userID).operation(EQUAL_OPERATOR).build());

		if (!StringUtils.isEmpty(name)) {
			searchParams.put(
					CONTEST_NAME_SEARCH_KEY,
					QueryCondition.builder().value(name).operation(LIKE_OPERATOR).build());
		}

		if (QueryDateCondition.generate(command, searchParams))
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST, translationService.getTranslation(FROM_DATE_TO_DATE_INVALID));

		var result =
				contestRepository.search(
						searchParams,
						Map.of(),
						command.getOrder_by(),
						command.getPage_size(),
						command.getPage_index(),
						Contest.class);

		// TODO: convert to dto
		List<Contest> contests = (List<Contest>) result.get(DATA_KEY);
		var examsOwnerDto =
				contests.stream()
						.map(
								c -> {
									String examName = examRepository.findById(c.getExamId()).get().getExamName();
									return new ContestOwnerDto(c, userName, examName);
								})
						.toList();

		result.put(DATA_KEY, examsOwnerDto);

		return GenerateResponseHelper.generateDataResponse(HttpStatus.OK, result);
	}

	// TODO: need test
	public ResponseEntity<?> getContestByUser(String token) throws JsonProcessingException {
		String tokenWithoutBearer = JwtTokenUtil.getTokenWithoutBearer(token);
		Long contestID =
				Long.valueOf(JwtTokenUtil.getUserInfoFromToken(tokenWithoutBearer, CONTEST_ID_TOKEN_KEY));
		String userEmail = JwtTokenUtil.getUserInfoFromToken(tokenWithoutBearer, USER_EMAIL_TOKEN_KEY);
		Optional<Contest> contestOpt = contestRepository.findById(contestID);
		if (contestOpt.isEmpty()) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.OK, translationService.getTranslation(NOT_FOUND_CONTEST));
		}

		List<String> listResult =
				contestOpt.get().getParticipants().stream().filter(p -> p.contains(userEmail)).toList();
		if (listResult.size() == 0) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST, translationService.getTranslation(NOT_FOUND_USER_EXAM));
		}

		var contestUserDto =
				new ContestUserDto(
						contestOpt.get().getExamId(),
						contestOpt.get().getEndAt().toString(),
						contestOpt.get().getStartAt().toString(),
						contestOpt.get().getName());

		return GenerateResponseHelper.generateDataResponse(
				HttpStatus.OK, Map.of(DATA_KEY, contestUserDto));
	}

	// TODO need test
	@Transactional
	public ResponseEntity<?> createContest(String token, MakeContestCommand command)
			throws JsonProcessingException {
		Long userID =
				Long.valueOf(
						JwtTokenUtil.getUserInfoFromToken(
								JwtTokenUtil.getTokenWithoutBearer(token), USER_ID_TOKEN_KEY));
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT);
		Optional<Exam> examOpt = examRepository.findById(command.getExamID());

		if (examOpt.isEmpty()) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST, translationService.getTranslation(NOT_FOUND_EXAM_INFORMATION));
		}

		// check startAt & endAt
		LocalDateTime startAt = LocalDateTime.parse(command.getStartAt(), formatter);
		LocalDateTime endAt = LocalDateTime.parse(command.getEndAt(), formatter);

		if (startAt.minus(2, ChronoUnit.DAYS).compareTo(LocalDateTime.now()) < 0) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST,
					translationService.getTranslation(CONTEST_RANGE_TIME_INVALID)
							+ SPACE_STRING
							+ 2
							+ "days");
		}

		if (startAt.compareTo(endAt) > 0) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST, translationService.getTranslation(FROM_DATE_TO_DATE_INVALID));
		}

		// check duration
		long millis = startAt.until(endAt, ChronoUnit.MINUTES);
		if (examOpt.get().getDuration() + 10 > millis) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST, translationService.getTranslation(CONTEST_DURATION_INVALID));
		}

		// check a number upload contest
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.add(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token);
			HttpEntity<Object> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromUriString(CHECK_USER_UPLOAD_URI)
							.queryParam(FLAG_KEY, CREATE_CONTEST_FLAG);
			String url = builder.toUriString();
			ResponseEntity<String> resp =
					restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
			if (!resp.getStatusCode().is2xxSuccessful()) {
				return GenerateResponseHelper.generateMessageResponse(
						HttpStatus.BAD_REQUEST, translationService.getTranslation(NOT_ALLOW_CREATE_CONTEST));
			}

		} catch (Exception ex) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST, translationService.getTranslation(ERROR_CREATE_CONTEST));
		}

		// check list account
		var userAccountsInfo = CsvUtil.readFileCsv(command.getFile(), CreateExamineeAccount.class);
		AtomicReference<String> listEmail = new AtomicReference<>(EMPTY_STRING);
		AtomicReference<String> emailExist = new AtomicReference<>(EMPTY_STRING);
		List<String> participants = new ArrayList<>();
		userAccountsInfo.forEach(
				a -> {
					if (listEmail.get().contains(a.getEmail())) {
						emailExist.set(a.getEmail());
					}
					listEmail.set(listEmail + a.getEmail() + COMMA_STRING_CHARACTER);
					participants.add(a.getUsername() + COMMA_STRING_CHARACTER + a.getEmail());
				});

		if (!StringUtils.isEmpty(emailExist.get())) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST, translationService.getTranslation(USER_ACCOUNT_EXAM_EXIST));
		}

		var contest =
				Contest.builder()
						.name(command.getName())
						.description(command.getDescription())
						.startAt(startAt)
						.endAt(endAt)
						.ownerID(userID)
						.examId(command.getExamID())
						.participants(participants)
						.build();

		contestRepository.save(contest);

		// create account
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.set("Authorization", token);
			headers.setContentType(MediaType.APPLICATION_JSON);
			CreateExamineeAccountDto dto =
					CreateExamineeAccountDto.builder()
							.contestID(contest.getId())
							.startAt(contest.getStartAt())
							.endAt(contest.getEndAt())
							.userInfo(
									userAccountsInfo.stream()
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
				throw new Exception(EMPTY_STRING);
			}
		} catch (Exception ex) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST, translationService.getTranslation(ERROR_CREATE_CONTEST));
		}

		return GenerateResponseHelper.generateMessageResponse(
				HttpStatus.OK, translationService.getTranslation(CREATE_CONTEST_SUCCESS));
	}

	// TODO: need test
	public ResponseEntity<?> getContestById(Long id, String token) throws JsonProcessingException {
		String userName =
				JwtTokenUtil.getUserInfoFromToken(
						JwtTokenUtil.getTokenWithoutBearer(token), USER_NAME_TOKEN_KEY);
		String userRoles =
				JwtTokenUtil.getUserInfoFromToken(
						JwtTokenUtil.getTokenWithoutBearer(token), USER_ROLES_TOKEN_KEY);
		Long userID =
				Long.valueOf(
						JwtTokenUtil.getUserInfoFromToken(
								JwtTokenUtil.getTokenWithoutBearer(token), USER_ID_TOKEN_KEY));
		Optional<Contest> contestOpt = contestRepository.findById(id);
		if (contestOpt.isEmpty()) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST, translationService.getTranslation(NOT_FOUND_CONTEST));
		}

		if (!userRoles.contains(ADMIN_ROLE) && userID.compareTo(contestOpt.get().getOwnerID()) != 0) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST, translationService.getTranslation(NOT_ALLOW_ACCESS_CONTEST));
		}
		var examID = examRepository.findById(contestOpt.get().getExamId()).get().getExamName();
		List<AccountExamDto> examAccounts =
				contestOpt.get().getParticipants().stream()
						.map(
								p -> {
									String[] arrInfo = p.split(COMMA_STRING_CHARACTER);
									return new AccountExamDto(arrInfo[0], arrInfo[1]);
								})
						.toList();

		var contestDetailDto = new ContestDetailDto(contestOpt.get(), examAccounts, examID, userName);

		return GenerateResponseHelper.generateDataResponse(
				HttpStatus.OK, Map.of(DATA_KEY, contestDetailDto));
	}

	// TODO: need test
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
