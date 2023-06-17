package com.example.demo.dto.category;

import com.example.demo.entity.Category;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryOptionDto {
	private Long id;
	private String name;

	public CategoryOptionDto(Category cate) {
		this.id = cate.getId();
		this.name = cate.getCategoryName();
	}
}
