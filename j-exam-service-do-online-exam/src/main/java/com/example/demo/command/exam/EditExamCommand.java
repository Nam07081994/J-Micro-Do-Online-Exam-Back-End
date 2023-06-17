package com.example.demo.command.exam;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EditExamCommand {
	private Long id;
	private String title;
	private Integer duration;
	private String description;
}
