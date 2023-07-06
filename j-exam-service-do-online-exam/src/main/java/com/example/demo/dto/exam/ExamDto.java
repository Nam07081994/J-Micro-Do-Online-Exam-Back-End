package com.example.demo.dto.exam;

import com.example.demo.dto.question.QuestionExamDto;
import com.example.demo.entity.Exam;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ExamDto {
	private Long id;
	private String examName;
	private Integer duration;
	private String categoryName;
	private List<QuestionExamDto> questionsExam;

	public ExamDto(Exam exam, List<QuestionExamDto> qes) {
		this.id = exam.getId();
		this.questionsExam = qes;
		this.examName = exam.getExamName();
		this.duration = exam.getDuration();
		this.categoryName = exam.getCategoryName();
	}
}
