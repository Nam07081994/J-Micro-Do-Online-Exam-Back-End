package com.example.demo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class ExamByCategoryDto {
	private Long id;
	private String image;
	private Long categoryID;
	private String examName;
	private Integer duration;
	private String description;
	private String categoryName;
	private Long downloadNumber;

	public ExamByCategoryDto(
			String categoryName,
			Long id,
			Integer duration,
			Long downloadNumber,
			String examName,
			String image,
			String description,
			Long categoryID) {
		this.id = id;
		this.image = image;
		this.duration = duration;
		this.examName = examName;
		this.categoryID = categoryID;
		this.description = description;
		this.categoryName = categoryName;
		this.downloadNumber = downloadNumber;
	}
}
