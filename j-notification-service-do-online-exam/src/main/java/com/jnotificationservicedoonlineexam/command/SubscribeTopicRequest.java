package com.jnotificationservicedoonlineexam.command;

import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscribeTopicRequest {
	private String topicName;
	private String clientID;
}
