package com.example.demo.command.category;

import com.example.demo.common.anotations.MultipleFileExtension;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCategoryCommand {
	@NotEmpty(message = "Category name is mandatory")
	private String categoryName;

	@MultipleFileExtension private MultipartFile image;
}
