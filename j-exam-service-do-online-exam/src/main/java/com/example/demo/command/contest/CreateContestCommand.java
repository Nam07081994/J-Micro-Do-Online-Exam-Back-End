package com.example.demo.command.contest;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateContestCommand {

	@NotEmpty(message = "Contest name is mandatory")
	private String name;

	@NotEmpty(message = "Contest description is mandatory")
	private String description;

	private String duration;

	private LocalDateTime startAt;

	private LocalDateTime endAt;

	@Min(value = 1)
	private Long examId;
}
