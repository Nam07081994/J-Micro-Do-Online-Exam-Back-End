package com.jnotificationservicedoonlineexam.config.amqp;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TopicConfig {

	@Value("${app.rabbitmq.exchanges.internal.topic-exchange}")
	private String internalTopicExchange;

	@Value("${app.rabbitmq.queues.topic-queue}")
	private String topicQueue;

	@Value("${app.rabbitmq.routing-keys.internal-topic}")
	private String internalTopicRoutingKey;

	@Bean
	public TopicExchange internalTopicExchange() {
		return new TopicExchange(this.internalTopicExchange);
	}

	@Bean
	public Queue topicQueue() {
		return new Queue(this.topicQueue);
	}

	@Bean
	public Binding internalToTopicBinding() {
		return BindingBuilder.bind(topicQueue())
				.to(internalTopicExchange())
				.with(this.internalTopicRoutingKey);
	}
}
