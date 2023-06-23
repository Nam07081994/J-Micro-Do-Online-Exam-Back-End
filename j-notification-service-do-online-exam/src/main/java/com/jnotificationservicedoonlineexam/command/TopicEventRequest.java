package com.jnotificationservicedoonlineexam.command;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TopicEventRequest implements Serializable {
	private String mode;
	private String clientID;
	private String topicName;
	private String fromService;
}
