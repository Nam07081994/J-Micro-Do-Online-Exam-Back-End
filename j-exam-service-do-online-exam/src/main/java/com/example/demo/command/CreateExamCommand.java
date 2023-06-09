package com.example.demo.command;

import com.example.demo.dto.QuestionDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateExamCommand {
	private Long categoryId;
	private String title;
	private String description;
	private Integer duration;
	private List<QuestionDto> questions;
}
