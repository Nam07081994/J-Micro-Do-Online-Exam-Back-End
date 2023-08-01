package com.example.demo.service;

import static com.example.demo.constant.Constant.*;
import static com.example.demo.constant.SQLConstants.EQUAL_OPERATOR;

import com.example.demo.command.feedback.FeedbackCommand;
import com.example.demo.common.jwt.JwtTokenUtil;
import com.example.demo.common.query.QueryCondition;
import com.example.demo.common.response.GenerateResponseHelper;
import com.example.demo.dto.feedback.FeedbackDto;
import com.example.demo.dto.feedback.RatingDto;
import com.example.demo.dto.feedback.StarDto;
import com.example.demo.entity.Exam;
import com.example.demo.entity.Feedback;
import com.example.demo.exceptions.ExecuteSQLException;
import com.example.demo.repository.ExamRepository;
import com.example.demo.repository.FeedbackRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.*;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class FeedbackService {

	private FeedbackRepository feedbackRepository;

	private ExamRepository examRepository;

	@Transactional
	public ResponseEntity<?> makeFeedback(String token, FeedbackCommand command)
			throws JsonProcessingException {
		if (!(token != null && !JwtTokenUtil.getTokenWithoutBearer(token).equals("null"))) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST, "Can not make a feedback");
		}

		Optional<Exam> examOpt = examRepository.findById(command.getExamID());
		if (examOpt.isEmpty()) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST, "Not found exam information");
		}

		Long userID =
				Long.valueOf(
						JwtTokenUtil.getUserInfoFromToken(
								JwtTokenUtil.getTokenWithoutBearer(token), USER_ID_TOKEN_KEY));
		String userRoles =
				JwtTokenUtil.getUserInfoFromToken(
						JwtTokenUtil.getTokenWithoutBearer(token), USER_ROLES_TOKEN_KEY);

		Feedback newFeedback =
				Feedback.builder()
						.voteNumber(command.getVote())
						.comment(command.getComment())
						.examName(examOpt.get().getExamName())
						.examId(command.getExamID())
						.build();

		if (userRoles.contains(ADMIN_ROLE)) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST, "Can not make a feedback");
		}

		if (userRoles.contains(USER_EXAM_ROLE)) {
			newFeedback.setUsername("Anonymous exam participant");
			newFeedback.setUserID(0L);
		} else if (userRoles.contains(USER_ROLE) || userRoles.contains(USER_PREMIUM_ROLE)) {
			Optional<Feedback> feedbackOpt =
					feedbackRepository.findFeedbackByExamIdAndAndUserID(command.getExamID(), userID);
			if (feedbackOpt.isPresent()) {
				return GenerateResponseHelper.generateMessageResponse(
						HttpStatus.BAD_REQUEST, "You already have a feedback");
			}
			newFeedback.setUsername(
					JwtTokenUtil.getUserInfoFromToken(
							JwtTokenUtil.getTokenWithoutBearer(token), USER_NAME_TOKEN_KEY));
			newFeedback.setUserID(userID);
		}

		feedbackRepository.save(newFeedback);

		return GenerateResponseHelper.generateMessageResponse(
				HttpStatus.OK, "Make feedback successfully");
	}

	public ResponseEntity<?> getFeedbackByExamName(
			String token, String name, int vote, int pageIndex, int pageSize)
			throws JsonProcessingException, ExecuteSQLException {
		if (StringUtils.isEmpty(name)) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST, "Exam name is mandatory");
		}

		Optional<Exam> examOpt = examRepository.findExamByExamName(name);
		if (examOpt.isEmpty()) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST, "Not found exam information");
		}

		if (token != null && !JwtTokenUtil.getTokenWithoutBearer(token).equals("null")) {
			Long userID =
					Long.valueOf(
							JwtTokenUtil.getUserInfoFromToken(
									JwtTokenUtil.getTokenWithoutBearer(token), USER_ID_TOKEN_KEY));

			Page<Feedback> feedbackPage;
			Pageable feedBackPage = PageRequest.of((pageIndex - 1), pageSize);
			if (vote > 0 && vote < 6) {
				feedbackPage = feedbackRepository.getFeedbackByUserVote(userID, name, vote, feedBackPage);
			} else {
				feedbackPage = feedbackRepository.getFeedbackByUser(userID, name, feedBackPage);
			}
			List<FeedbackDto> feedbackList =
					feedbackPage.getContent().stream().map(FeedbackDto::new).toList();

			return GenerateResponseHelper.generateDataResponse(
					HttpStatus.OK,
					Map.of(
							DATA_KEY,
							feedbackList,
							PAGINATION_KEY,
							Map.of(
									PAGES_KEY,
									feedbackPage.getTotalPages(),
									PAGE_INDEX,
									pageIndex,
									TOTAL_RECORDS_KEY,
									feedbackPage.getTotalElements())));
		}
		Map<String, QueryCondition> searchParams = new HashMap<>();

		if (vote > 0 && vote < 6) {
			searchParams.put(
					"voteNumber", QueryCondition.builder().value(vote).operation(EQUAL_OPERATOR).build());
		}

		searchParams.put(
				"examName", QueryCondition.builder().value(name).operation(EQUAL_OPERATOR).build());

		var result = feedbackRepository.search(searchParams, EMPTY_STRING, 10, 1, Feedback.class);

		var feedbacks = (List<Feedback>) result.get(DATA_KEY);
		result.put(DATA_KEY, feedbacks.stream().map(FeedbackDto::new).collect(Collectors.toList()));

		return GenerateResponseHelper.generateDataResponse(HttpStatus.OK, result);
	}

	public ResponseEntity<?> deleteFeedback(String token, Long id) throws JsonProcessingException {
		Optional<Feedback> feedbackOpt = feedbackRepository.findById(id);
		if (feedbackOpt.isEmpty()) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST, "Not found feedback information");
		}

		Long userID =
				Long.valueOf(
						JwtTokenUtil.getUserInfoFromToken(
								JwtTokenUtil.getTokenWithoutBearer(token), USER_ID_TOKEN_KEY));
		String userRoles =
				JwtTokenUtil.getUserInfoFromToken(
						JwtTokenUtil.getTokenWithoutBearer(token), USER_ROLES_TOKEN_KEY);

		if (userRoles.contains(USER_EXAM_ROLE)) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST, "Can not delete a feedback");
		} else if (userRoles.contains(USER_ROLE) || userRoles.contains(USER_PREMIUM_ROLE)) {
			if (userID.compareTo(feedbackOpt.get().getUserID()) != 0) {
				return GenerateResponseHelper.generateMessageResponse(
						HttpStatus.BAD_REQUEST, "Feedback is not belong to you");
			}
		}

		feedbackRepository.delete(feedbackOpt.get());

		return GenerateResponseHelper.generateMessageResponse(
				HttpStatus.OK, "Delete feedback successfully");
	}

	public ResponseEntity<?> editFeedback(String token, FeedbackCommand command, Long id)
			throws JsonProcessingException {
		Optional<Feedback> feedbackOpt = feedbackRepository.findById(id);
		if (feedbackOpt.isEmpty()) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST, "Not found feedback information");
		}

		String userRoles =
				JwtTokenUtil.getUserInfoFromToken(
						JwtTokenUtil.getTokenWithoutBearer(token), USER_ROLES_TOKEN_KEY);

		if (userRoles.contains(ADMIN_ROLE) || userRoles.contains(USER_EXAM_ROLE)) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST, "Request is not valid");
		}
		Long userID =
				Long.valueOf(
						JwtTokenUtil.getUserInfoFromToken(
								JwtTokenUtil.getTokenWithoutBearer(token), USER_ID_TOKEN_KEY));

		if (userID.compareTo(feedbackOpt.get().getUserID()) != 0) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST, "Feedback not belong to you");
		}

		feedbackOpt.get().setComment(command.getComment());
		feedbackOpt.get().setVoteNumber(command.getVote());

		feedbackRepository.save(feedbackOpt.get());

		return GenerateResponseHelper.generateMessageResponse(
				HttpStatus.OK, "Edit feedback successfully");
	}

	public ResponseEntity<?> checkUserFeedback(String token, Long id) throws JsonProcessingException {
		Optional<Exam> examOpt = examRepository.findById(id);
		if (examOpt.isEmpty()) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST, "Not found exam information");
		}

		Long userID =
				Long.valueOf(
						JwtTokenUtil.getUserInfoFromToken(
								JwtTokenUtil.getTokenWithoutBearer(token), USER_ID_TOKEN_KEY));
		String userRoles =
				JwtTokenUtil.getUserInfoFromToken(
						JwtTokenUtil.getTokenWithoutBearer(token), USER_ROLES_TOKEN_KEY);

		Optional<Feedback> feedbackOpt =
				feedbackRepository.findFeedbackByExamIdAndAndUserID(id, userID);

		if (userRoles.contains(USER_EXAM_ROLE)) {
			return GenerateResponseHelper.generateDataResponse(HttpStatus.OK, Map.of(DATA_KEY, false));
		} else if (userRoles.contains(ADMIN_ROLE)) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST, "Request is not valid");
		}

		return GenerateResponseHelper.generateDataResponse(
				HttpStatus.OK, Map.of(DATA_KEY, feedbackOpt.isPresent()));
	}

	public RatingDto calculateRatingExam(Long id) {
		Map<Integer, Float> ratingData = new HashMap<>();
		Integer totalVote = 0;
		List<Feedback> feedbacks = feedbackRepository.findAllByExamId(id);
		var stars = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));
		var starsValue = new ArrayList<>(Arrays.asList(0f, 0f, 0f, 0f, 0f));
		if (feedbacks.size() == 0) {
			return RatingDto.builder()
					.ranking("Not yet")
					.ratingData(new StarDto(stars, starsValue))
					.totalRating(0)
					.build();
		}

		for (Feedback fb : feedbacks) {
			totalVote = totalVote + fb.getVoteNumber();
			if (ratingData.containsKey(fb.getVoteNumber())) {
				float oldValue = ratingData.get(fb.getVoteNumber());
				ratingData.put(fb.getVoteNumber(), oldValue + 1);
			} else {
				ratingData.put(fb.getVoteNumber(), 1f);
			}
		}

		for (int num : stars) {
			if (ratingData.containsKey(num)) {
				var timeOccur = ratingData.get(num);
				ratingData.put(num, timeOccur * num / totalVote * 100);
			} else {
				ratingData.put(num, 0f);
			}
		}

		var totalRating = totalVote / feedbacks.size();
		var ranking = "";
		if (totalRating < 2.5) {
			ranking = "Average";
		} else if (totalRating < 4) {
			ranking = "Good";
		} else if (totalRating <= 5) {
			ranking = "Excellence";
		}

		return RatingDto.builder()
				.totalRating(totalRating)
				.ranking(ranking)
				.ratingData(
						new StarDto(
								ratingData.keySet().stream().toList(), new ArrayList<>(ratingData.values())))
				.build();
	}

	public ResponseEntity<?> calculateExamRatingByName(String name) {
		Optional<Exam> examOpt = examRepository.findExamByExamName(name);
		if (examOpt.isEmpty()) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST, "Not found exam information");
		}

		var result = calculateRatingExam(examOpt.get().getId());

		return GenerateResponseHelper.generateDataResponse(HttpStatus.OK, Map.of(DATA_KEY, result));
	}
}
