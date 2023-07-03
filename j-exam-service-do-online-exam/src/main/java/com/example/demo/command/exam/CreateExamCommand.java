package com.example.demo.command.exam;

import com.example.demo.Enum.ExamType;
import com.example.demo.common.anotations.MultipleFileExtension;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateExamCommand {
	@NotEmpty(message = "Exam title is mandatory")
	private String title;

	@NotNull(message = "Exam category is mandatory")
	private Long categoryId;

	@NotNull(message = "Exam duration is mandatory")
	@Min(value = 1, message = "Exam duration must be at least 1 minutes")
	private Integer duration;

	@NotEmpty(message = "Exam description is mandatory")
	private String description;

	@MultipleFileExtension private MultipartFile file;

	@NotEmpty(message = "Exam questions is mandatory")
	private String questions;

	private ExamType examType = ExamType.PREMIUM;
}
