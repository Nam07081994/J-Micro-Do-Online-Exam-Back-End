package com.jnotificationservicedoonlineexam.config.amqp;

import com.jnotificationservicedoonlineexam.command.TopicEventRequest;
import com.jnotificationservicedoonlineexam.service.FCMService;
import java.util.Objects;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class TopicConsumer {

	private FCMService fcmService;

	@RabbitListener(queues = "${app.rabbitmq.queues.topic-queue}")
	public void topicConsumer(TopicEventRequest topicEventRequest) {
		System.out.println("Consume msg from ");
		if (Objects.equals(topicEventRequest.getMode(), "subscribe")) {
			fcmService.subscribeFCMTopic("", "");
		} else if (Objects.equals(topicEventRequest.getMode(), "unsubscribe")) {
			fcmService.unsubscribeFCMTopic("", "");
		} else {

		}
	}
}
