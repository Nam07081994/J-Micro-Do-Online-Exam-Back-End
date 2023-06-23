package com.jnotificationservicedoonlineexam.config.amqp;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NotificationConfig {

	@Value("${app.rabbitmq.exchanges.internal.notify-exchange}")
	private String internalNotificationExchange;

	@Value("${app.rabbitmq.queues.notification-queue}")
	private String notificationQueue;

	@Value("${app.rabbitmq.routing-keys.internal-notification}")
	private String internalNotificationRoutingKey;

	@Bean
	public TopicExchange internalTopicNotificationExchange() {
		return new TopicExchange(this.internalNotificationExchange);
	}

	@Bean
	public Queue notificationQueue() {
		return new Queue(this.notificationQueue);
	}

	@Bean
	public Binding internalToNotificationBinding() {
		return BindingBuilder.bind(notificationQueue())
				.to(internalTopicNotificationExchange())
				.with(this.internalNotificationRoutingKey);
	}
}
