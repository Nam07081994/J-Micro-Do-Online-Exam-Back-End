package com.jnotificationservicedoonlineexam.dto;

import com.jnotificationservicedoonlineexam.entity.Notification;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDto {
	private String title;
	private String content;
	private Boolean isChecked;

	public NotificationDto(Notification notify) {
		this.isChecked = notify.getIsChecked();
		this.title = notify.getNotificationTitle();
		this.content = notify.getNotificationContent();
	}
}
