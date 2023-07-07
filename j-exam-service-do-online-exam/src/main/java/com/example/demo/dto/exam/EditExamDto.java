package com.example.demo.dto.exam;

import com.example.demo.dto.question.QuestionDto;
import com.example.demo.entity.Exam;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EditExamDto {
	private Long id;
	private String title;
	private Long categoryID;
	private Integer duration;
	private String description;
	private List<QuestionDto> questions;

	public EditExamDto(Exam exam, List<QuestionDto> questions) {
		this.id = exam.getId();
		this.questions = questions;
		this.title = exam.getExamName();
		this.duration = exam.getDuration();
		this.categoryID = exam.getCategoryId();
		this.description = exam.getDescription();
	}
}
