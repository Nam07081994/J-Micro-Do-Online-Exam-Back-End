package com.example.demo.command.category;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCategoryCommand {
	@NotEmpty(message = "Category name is mandatory")
	private String categoryName;
}
