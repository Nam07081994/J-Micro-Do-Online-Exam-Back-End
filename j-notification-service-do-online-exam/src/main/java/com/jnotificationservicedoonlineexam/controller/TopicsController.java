package com.jnotificationservicedoonlineexam.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jnotificationservicedoonlineexam.command.CreateTopicRequest;
import com.jnotificationservicedoonlineexam.command.QuerySearchCommand;
import com.jnotificationservicedoonlineexam.command.SubscribeTopicRequest;
import com.jnotificationservicedoonlineexam.exceptions.ExecuteSQLException;
import com.jnotificationservicedoonlineexam.exceptions.InvalidDateFormatException;
import com.jnotificationservicedoonlineexam.service.TopicService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/notify/topics")
public class TopicsController {

	private final TopicService topicService;

	@GetMapping
	public ResponseEntity<?> getTopics(
			@RequestParam(name = "name", required = false) String name,
			@RequestParam(name = "from_date", required = false) String from_date,
			@RequestParam(name = "to_date", required = false) String to_date,
			@RequestParam(name = "page_size", defaultValue = "10") int page_size,
			@RequestParam(name = "page_index", defaultValue = "-1") int page_index,
			@RequestParam(name = "order_by", defaultValue = "-1") int order_by)
			throws ExecuteSQLException, InvalidDateFormatException {

		return topicService.getTopics(
				QuerySearchCommand.from(from_date, to_date, page_index, page_size, order_by), name);
	}

	@PostMapping
	public ResponseEntity<?> makeTopic(@RequestBody CreateTopicRequest req) {
		return topicService.makeTopic(req);
	}

	@PutMapping("/subscribe")
	public ResponseEntity<?> subscribeTopic(
			@RequestHeader("Authorization") String token, @RequestBody SubscribeTopicRequest req)
			throws JsonProcessingException {

		return topicService.subscribeTopic(req, token);
	}

	@PutMapping("/unsubscribe")
	public ResponseEntity<?> unsubscribeTopic(
			@RequestHeader("Authorization") String token, @RequestBody SubscribeTopicRequest req)
			throws JsonProcessingException {
		return topicService.unsubscribeTopic(req, token);
	}

	@DeleteMapping("/delete")
	public ResponseEntity<?> deleteTopic(@RequestParam("id") Long id) {
		return topicService.deleteTopic(id);
	}
}
