package com.jnotificationservicedoonlineexam.command;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateTopicRequest {
	@NotEmpty(message = "topic name is mandatory")
	private String topicName;
}
