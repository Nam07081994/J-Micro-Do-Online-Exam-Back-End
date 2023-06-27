package com.example.demo.dto.exam;

import com.example.demo.entity.Exam;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ExamCardDto {
	private Long id;
	private String examName;
	private Boolean isPrivate;
	private String description;
	private String categoryName;
	private String createAt;
	private String thumbnail;

	public ExamCardDto(Exam exam, String categoryName) {
		this.id = exam.getId();
		this.categoryName = categoryName;
		this.examName = exam.getExamName();
		this.thumbnail = exam.getThumbnail();
		this.isPrivate = exam.getIsPrivate();
		this.description = exam.getDescription();
		this.createAt = exam.getCreatedAt().toString();
	}
}
