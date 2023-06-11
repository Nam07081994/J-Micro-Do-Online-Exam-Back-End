package com.example.demo.dto;

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
}
