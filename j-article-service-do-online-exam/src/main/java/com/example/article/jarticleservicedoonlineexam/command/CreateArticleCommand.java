package com.example.article.jarticleservicedoonlineexam.command;

import com.example.article.jarticleservicedoonlineexam.common.annotations.MultipleFileExtension;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateArticleCommand {
	@NotEmpty(message = "Article title is mandatory")
	private String title;

	@NotEmpty(message = "Article author is mandatory")
	private String author;

	@NotEmpty(message = "Article content is mandatory")
	private String content;

	@MultipleFileExtension private MultipartFile image;
}
