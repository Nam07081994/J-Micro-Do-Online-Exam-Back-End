package com.example.demo.command;

import com.example.demo.common.annotations.CheckListSize;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateAccountsExamCommand {
	private Long contestID;

	private Long examID;

	private Map<String, String> userInfo;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm")
	private LocalDateTime startAt;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm")
	private LocalDateTime endAt;
}
