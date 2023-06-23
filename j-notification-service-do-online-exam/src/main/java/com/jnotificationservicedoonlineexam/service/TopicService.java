package com.jnotificationservicedoonlineexam.service;

import static com.jnotificationservicedoonlineexam.constants.Constants.DATA_KEY;
import static com.jnotificationservicedoonlineexam.constants.Constants.USER_ID_TOKEN_KEY;
import static com.jnotificationservicedoonlineexam.constants.SQLConstants.LIKE_OPERATOR;
import static com.jnotificationservicedoonlineexam.constants.SQLConstants.TOPIC_NAME_KEY;
import static com.jnotificationservicedoonlineexam.constants.TranslationCodeConstants.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jnotificationservicedoonlineexam.command.CreateTopicRequest;
import com.jnotificationservicedoonlineexam.command.QuerySearchCommand;
import com.jnotificationservicedoonlineexam.command.SubscribeTopicRequest;
import com.jnotificationservicedoonlineexam.common.jwt.JwtTokenUtil;
import com.jnotificationservicedoonlineexam.common.query.QueryCondition;
import com.jnotificationservicedoonlineexam.common.query.QueryDateCondition;
import com.jnotificationservicedoonlineexam.common.response.GenerateResponseHelper;
import com.jnotificationservicedoonlineexam.dto.TopicDto;
import com.jnotificationservicedoonlineexam.entity.Topic;
import com.jnotificationservicedoonlineexam.exceptions.ExecuteSQLException;
import com.jnotificationservicedoonlineexam.repository.TopicRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TopicService {

	private FCMService fcmService;

	private TopicRepository topicRepository;

	private TranslationService translationService;

	public ResponseEntity<?> getTopics(QuerySearchCommand command, String name)
			throws ExecuteSQLException {
		Map<String, QueryCondition> searchParams = new HashMap<>();

		if (!name.isEmpty()) {
			searchParams.put(
					TOPIC_NAME_KEY, QueryCondition.builder().operation(LIKE_OPERATOR).value(name).build());
		}

		if (QueryDateCondition.generate(command, searchParams))
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST, translationService.getTranslation(FROM_DATE_TO_DATE_INVALID));

		var result =
				topicRepository.search(
						searchParams,
						Map.of(),
						command.getOrder_by(),
						command.getPage_size(),
						command.getPage_index(),
						Topic.class);

		List<Topic> topics = (List<Topic>) result.get(DATA_KEY);

		result.put(DATA_KEY, topics.stream().map(TopicDto::new).collect(Collectors.toList()));

		return GenerateResponseHelper.generateDataResponse(HttpStatus.OK, result);
	}

	public ResponseEntity<?> makeTopic(CreateTopicRequest req) {
		Optional<Topic> topicOpt = topicRepository.findByTopicName(req.getTopicName());
		if (topicOpt.isPresent()) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST, translationService.getTranslation(TOPIC_NAME_EXIST));
		}

		Topic newTopic = Topic.builder().topicName(req.getTopicName()).build();
		topicRepository.save(newTopic);

		return GenerateResponseHelper.generateMessageResponse(
				HttpStatus.OK, translationService.getTranslation(CREATE_TOPIC_SUCCESS));
	}

	public ResponseEntity<?> subscribeTopic(SubscribeTopicRequest req, String token)
			throws JsonProcessingException {
		Optional<Topic> topicOpt = topicRepository.findByTopicName(req.getTopicName());
		Long userID = Long.valueOf(JwtTokenUtil.getUserInfoFromToken(token, USER_ID_TOKEN_KEY));

		if (topicOpt.isEmpty()) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST, translationService.getTranslation(NOT_FOUND_TOPIC));
		}

		List<Long> userSubscribeTopic = topicOpt.get().getSubscribersID();
		for (Long id : userSubscribeTopic) {
			if (userID.compareTo(id) == 0) {
				return GenerateResponseHelper.generateMessageResponse(
						HttpStatus.BAD_REQUEST,
						translationService.getTranslation(USER_ALREADY_SUBSCRIBE_TOPIC));
			}
		}

		userSubscribeTopic.add(userID);
		topicOpt.get().setSubscribersID(userSubscribeTopic);
		topicRepository.save(topicOpt.get());
		// TODO: add client id of user to fcm
		// fcmService.subscribeFCMTopic(req.getClientID(),req.getTopicName());

		return GenerateResponseHelper.generateMessageResponse(
				HttpStatus.OK, translationService.getTranslation(SUBCRIBE_TOPIC_SUCCESS));
	}

	public ResponseEntity<?> deleteTopic(Long id) {
		Optional<Topic> topicOpt = topicRepository.findById(id);
		if (topicOpt.isEmpty()) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST, translationService.getTranslation(NOT_FOUND_TOPIC));
		}

		topicRepository.delete(topicOpt.get());

		return GenerateResponseHelper.generateMessageResponse(
				HttpStatus.OK, translationService.getTranslation(DELETE_TOPIC_SUCCESS));
	}

	public ResponseEntity<?> unsubscribeTopic(SubscribeTopicRequest req, String token)
			throws JsonProcessingException {
		Optional<Topic> topicOpt = topicRepository.findByTopicName(req.getTopicName());
		Long userID = Long.valueOf(JwtTokenUtil.getUserInfoFromToken(token, USER_ID_TOKEN_KEY));

		if (topicOpt.isEmpty()) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST, translationService.getTranslation(NOT_FOUND_TOPIC));
		}

		List<Long> userSubscribeTopic = topicOpt.get().getSubscribersID();
		userSubscribeTopic.removeIf(id -> id.equals(userID));
		topicOpt.get().setSubscribersID(userSubscribeTopic);
		topicRepository.save(topicOpt.get());
		// TODO: remove clientID of user to fcm
		// fcmService.unsubscribeFCMTopic(req.getClientID(),req.getTopicName());

		return GenerateResponseHelper.generateMessageResponse(
				HttpStatus.OK, translationService.getTranslation(UNSUBSCRIBE_TOPIC_SUCCESS));
	}
}
