package com.example.demo.dto.category;

import com.example.demo.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDto {
	private String name;
	private Long id;
	private String createdAt;

	public CategoryDto(Category category) {
		this.name = category.getCategoryName();
		this.id = category.getId();
		this.createdAt = category.getCreatedAt().toString();
	}
}
