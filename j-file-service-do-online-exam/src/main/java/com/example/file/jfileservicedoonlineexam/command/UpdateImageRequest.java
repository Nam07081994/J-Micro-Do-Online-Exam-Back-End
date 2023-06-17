package com.example.file.jfileservicedoonlineexam.command;

import com.example.file.jfileservicedoonlineexam.common.annotations.MultipleFileExtension;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateImageRequest {
	@NotEmpty(message = "domain name is mandatory")
	private String domain;

	@NotEmpty(message = "old image path is mandatory")
	private String oldImagePath;

	@MultipleFileExtension
	private MultipartFile file;
}
