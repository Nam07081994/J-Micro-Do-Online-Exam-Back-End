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
public class ExamOptionDto {
	private Long id;
	private String name;

	public ExamOptionDto(Exam ex) {
		this.id = ex.getId();
		this.name = ex.getExamName();
	}
}
