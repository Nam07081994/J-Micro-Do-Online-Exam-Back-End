package com.example.article.jarticleservicedoonlineexam.command;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateInfoArticleCommand {
	@NotEmpty(message = "Article title is mandatory")
	private String title;

	@NotEmpty(message = "Article author is mandatory")
	private String author;

	@NotEmpty(message = "Article content is mandatory")
	private String content;
}
