package com.jnotificationservicedoonlineexam.dto;

import com.jnotificationservicedoonlineexam.entity.Topic;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TopicDto {
	private Long id;
	private String topicName;
	private String createdAt;

	public TopicDto(Topic topic) {
		this.id = topic.getId();
		this.topicName = topic.getTopicName();
		this.createdAt = topic.getCreatedAt().toString();
	}
}
