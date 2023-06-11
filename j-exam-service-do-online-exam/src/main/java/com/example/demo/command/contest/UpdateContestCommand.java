package com.example.demo.command.contest;

import jakarta.annotation.Nonnull;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class UpdateContestCommand {
	@Nonnull private Long id;

	private String name;

	private Long categoryId;

	private String description;

	private String duration;

	private LocalDateTime startAt;

	private LocalDateTime endAt;

	private Long examId;
}
