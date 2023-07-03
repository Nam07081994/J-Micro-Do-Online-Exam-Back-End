package com.example.demo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class ExamByCategoryDto {
	private String categoryName;
	private Long id;
	private Integer duration;
	private Long downloadNumber;
	private String examName;

	public ExamByCategoryDto(
			String categoryName, Long id, Integer duration, Long downloadNumber, String examName) {
		this.id = id;
		this.duration = duration;
		this.downloadNumber = downloadNumber;
		this.categoryName = categoryName;
		this.examName = examName;
	}
}
