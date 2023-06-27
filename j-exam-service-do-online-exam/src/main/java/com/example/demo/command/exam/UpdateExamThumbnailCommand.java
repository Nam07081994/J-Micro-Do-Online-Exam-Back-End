package com.example.demo.command.exam;

import com.example.demo.common.anotations.MultipleFileExtension;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateExamThumbnailCommand {
	@NotNull(message = "Exam id is mandatory")
	private Long id;

	@MultipleFileExtension private MultipartFile file;
}
