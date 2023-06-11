package com.example.demo.command.contest;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class CreateContestCommand {

	private String name;

	private Long categoryId;

	private String description;

	private String duration;

	private LocalDateTime startAt;

	private LocalDateTime endAt;

	private Long examId;
}
