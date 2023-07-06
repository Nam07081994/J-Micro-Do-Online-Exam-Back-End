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
	private String image;
	private Long categoryID;
	private String createAt;
	private String examName;
	private String examType;
	private Integer duration;
	private String description;
	private String categoryName;
	private Long downloadNumber;


	public ExamCardDto(Exam exam) {
		this.id = exam.getId();
		this.examType = exam.getExamType();
		this.image = exam.getThumbnail();
		this.duration = exam.getDuration();
		this.examName = exam.getExamName();
		this.categoryID = exam.getCategoryId();
		this.description = exam.getDescription();
		this.categoryName = exam.getCategoryName();
		this.createAt = exam.getCreatedAt().toString();
		this.downloadNumber = exam.getDownloadNumber();
	}
}
