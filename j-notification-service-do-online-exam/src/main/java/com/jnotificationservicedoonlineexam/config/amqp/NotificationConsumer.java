package com.jnotificationservicedoonlineexam.config.amqp;

import com.jnotificationservicedoonlineexam.command.NotificationEventRequest;
import com.jnotificationservicedoonlineexam.service.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class NotificationConsumer {
	private final NotificationService notificationService;

	@RabbitListener(queues = "${app.rabbitmq.queues.notification-queue}")
	public void notifyConsumer(NotificationEventRequest notificationRequest) {
		System.out.println("Consume msg from ");
		notificationService.send(notificationRequest);
	}
}
