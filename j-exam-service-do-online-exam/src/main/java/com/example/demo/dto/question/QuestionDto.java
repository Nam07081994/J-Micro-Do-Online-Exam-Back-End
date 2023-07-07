package com.example.demo.dto.question;

import com.example.demo.entity.Question;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDto {
	private String questionType;
	private Integer questionPoint;
	private String question;
	private List<String> answers;
	private List<Long> correctAnswers;

	public QuestionDto(Question ques) {
		this.answers = ques.getAnswers();
		this.question = ques.getQuestion();
		this.questionPoint = ques.getQuestionPoint();
		this.correctAnswers = ques.getCorrectAnswers();
		this.questionType = ques.getQuestionType().name();
	}
}
