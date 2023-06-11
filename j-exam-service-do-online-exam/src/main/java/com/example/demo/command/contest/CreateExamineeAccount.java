package com.example.demo.command.contest;

import lombok.Data;

@Data
public class CreateExamineeAccount {

	private String stt;

	private String hoten;

	private String email;

	private String password;

	private Boolean isCreated;
}
