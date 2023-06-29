package com.example.demo.dto.question;

import com.example.demo.Enum.QuestionType;
import com.example.demo.entity.Question;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionExamDto {
	private Long id;
	private String question;
	private List<String> answers;
	private Integer questionPoint;
	private QuestionType questionType;

	public QuestionExamDto(Question ques) {
		this.question = ques.getQuestion();
		this.answers = ques.getAnswers();
		this.questionPoint = ques.getQuestionPoint();
		this.questionType = ques.getQuestionType();
		this.id = ques.getId();
	}
}
