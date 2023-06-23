package com.jnotificationservicedoonlineexam.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEventRequest {
	private String title;
	private String mode;
	private Boolean isSave;
	private String content;
	private String topicName;
}
