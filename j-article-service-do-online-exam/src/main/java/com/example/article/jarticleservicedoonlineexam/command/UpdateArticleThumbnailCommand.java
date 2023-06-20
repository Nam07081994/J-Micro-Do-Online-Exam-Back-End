package com.example.article.jarticleservicedoonlineexam.command;

import com.example.article.jarticleservicedoonlineexam.common.annotations.MultipleFileExtension;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateArticleThumbnailCommand {
	@Min(value = 1)
	private Long id;

	@MultipleFileExtension private MultipartFile image;
}
