package com.jnotificationservicedoonlineexam.service;

import static com.jnotificationservicedoonlineexam.constants.Constants.DATA_KEY;
import static com.jnotificationservicedoonlineexam.constants.Constants.USER_ID_TOKEN_KEY;
import static com.jnotificationservicedoonlineexam.constants.SQLConstants.EQUAL_OPERATOR;
import static com.jnotificationservicedoonlineexam.constants.SQLConstants.NOTIFICATION_USER_ID_KEY;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jnotificationservicedoonlineexam.command.NotificationEventRequest;
import com.jnotificationservicedoonlineexam.command.QuerySearchCommand;
import com.jnotificationservicedoonlineexam.common.jwt.JwtTokenUtil;
import com.jnotificationservicedoonlineexam.common.query.QueryCondition;
import com.jnotificationservicedoonlineexam.common.response.GenerateResponseHelper;
import com.jnotificationservicedoonlineexam.dto.NotificationDto;
import com.jnotificationservicedoonlineexam.entity.Notification;
import com.jnotificationservicedoonlineexam.exceptions.ExecuteSQLException;
import com.jnotificationservicedoonlineexam.repository.NotificationRepository;
import com.jnotificationservicedoonlineexam.repository.TopicRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class NotificationService {

	private FCMService fcmService;

	private TopicRepository topicRepository;

	private NotificationRepository notificationRepository;

	public void send(NotificationEventRequest notificationRequest) {
		// TODO: specified what data need to save into db
		if (notificationRequest.getIsSave()) {}

		// send fcm notify
		if (StringUtils.isEmpty(notificationRequest.getTopicName())
				&& Objects.equals(notificationRequest.getMode(), "direct")) {

		} else if (Objects.equals(notificationRequest.getMode(), "topic")) {

		}
	}

	public ResponseEntity<?> getNotificationsByUser(QuerySearchCommand command, String token)
			throws JsonProcessingException, ExecuteSQLException {
		Map<String, QueryCondition> searchParams = new HashMap<>();
		Long userID =
				Long.valueOf(
						JwtTokenUtil.getUserInfoFromToken(
								JwtTokenUtil.getTokenWithoutBearer(token), USER_ID_TOKEN_KEY));

		searchParams.put(
				NOTIFICATION_USER_ID_KEY,
				QueryCondition.builder().operation(EQUAL_OPERATOR).value(userID).build());

		var result =
				notificationRepository.search(
						searchParams,
						Map.of(),
						command.getOrder_by(),
						command.getPage_size(),
						command.getPage_index(),
						Notification.class);

		List<Notification> notifications = (List<Notification>) result.get(DATA_KEY);

		result.put(
				DATA_KEY, notifications.stream().map(NotificationDto::new).collect(Collectors.toList()));

		return GenerateResponseHelper.generateDataResponse(HttpStatus.OK, result);
	}
}
