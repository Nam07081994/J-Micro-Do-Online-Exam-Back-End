package com.example.demo.command.category;

import com.example.demo.common.anotations.MultipleFileExtension;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCategoryCommand {
	private Long categoryId;

	@MultipleFileExtension private MultipartFile image;
}
