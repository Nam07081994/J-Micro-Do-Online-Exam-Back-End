package com.example.demo.command.exam;

import com.example.demo.dto.AnswerDto;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SubmitExamCommand {
	@NotNull(message = "Required")
	private Long id;

	private List<AnswerDto> answers;

	private Long startTimeExam;

	private Long endTimeExam;
}
