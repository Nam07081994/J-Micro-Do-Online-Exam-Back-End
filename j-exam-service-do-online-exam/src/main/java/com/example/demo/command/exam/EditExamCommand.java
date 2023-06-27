package com.example.demo.command.exam;

import com.example.demo.dto.question.QuestionDto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EditExamCommand {
	@NotNull private Long id;

	@NotEmpty(message = "Exam title is mandatory")
	private String title;

	@NotNull(message = "Exam category is mandatory")
	private Long categoryId;

	@NotNull(message = "Exam duration is mandatory")
	@Min(value = 1, message = "Exam duration must be at least 1 minutes")
	private Integer duration;

	@NotEmpty(message = "Exam description is mandatory")
	private String description;

	private List<QuestionDto> questions;
}
