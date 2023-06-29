package com.example.demo.command.contest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateExamineeAccount {

	private String stt;

	private String username;

	private String email;
}
