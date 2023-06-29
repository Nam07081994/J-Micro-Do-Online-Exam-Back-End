package com.example.demo.command.contest;

import com.example.demo.common.anotations.DateFormatCheck;
import com.example.demo.common.anotations.MultipleFileExtension;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MakeContestCommand {
	@NotEmpty(message = "Contest name is mandatory")
	private String name;

	@NotEmpty(message = "Contest description is mandatory")
	private String description;

	@DateFormatCheck(isChecked = true)
	private String startAt;

	@DateFormatCheck(isChecked = true)
	private String endAt;

	@NotNull(message = "ExamID is mandatory")
	private Long examID;

	@MultipleFileExtension(allowedTypes = {"csv"})
	private MultipartFile file;
}
