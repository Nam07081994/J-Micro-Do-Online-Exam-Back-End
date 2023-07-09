package com.example.demo.command.feedback;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackCommand {
	private Long examID;

	@NotNull(message = "Feedback vote is mandatory")
	@Min(value = 1)
	@Max(value = 5)
	private Integer vote;

	@NotEmpty(message = "Feedback comment is mandatory")
	private String comment;
}
