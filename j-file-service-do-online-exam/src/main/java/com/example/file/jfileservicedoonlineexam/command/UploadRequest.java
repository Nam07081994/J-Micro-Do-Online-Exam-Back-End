package com.example.file.jfileservicedoonlineexam.command;

import com.example.file.jfileservicedoonlineexam.common.annotations.MultipleFileExtension;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UploadRequest {
	@NotEmpty(message = "domain name is mandatory")
	private String domain;

	@NotEmpty(message = "fileType is mandatory")
	private String fileType;

	@MultipleFileExtension private MultipartFile file;
}
