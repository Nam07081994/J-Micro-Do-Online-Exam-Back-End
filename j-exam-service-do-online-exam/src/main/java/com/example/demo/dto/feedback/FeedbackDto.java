package com.example.demo.dto.feedback;

import com.example.demo.entity.Feedback;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackDto {
	private Long id;
	private Long userID;
	private Integer vote;
	private String comment;
	private String createdAt;
	private String username;

	public FeedbackDto(Feedback feedback) {
		this.id = feedback.getId();
		this.userID = feedback.getUserID();
		this.vote = feedback.getVoteNumber();
		this.comment = feedback.getComment();
		this.username = feedback.getUsername();
		this.createdAt = feedback.getCreatedAt().toString();
	}
}
