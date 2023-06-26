package com.jnotificationservicedoonlineexam.command;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscribeTopicRequest {
	@NotEmpty(message = "Topic name is mandatory")
	private String topicName;

	@NotEmpty(message = "ClientID is mandatory")
	private String clientID;
}
