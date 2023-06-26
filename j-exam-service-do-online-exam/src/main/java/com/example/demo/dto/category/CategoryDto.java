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
	private Long id;
	private String name;
	private String createdAt;
	private String thumbnail;

	public CategoryDto(Category category) {
		this.id = category.getId();
		this.name = category.getCategoryName();
		this.thumbnail = category.getThumbnail();
		this.createdAt = category.getCreatedAt().toString();
	}
}
