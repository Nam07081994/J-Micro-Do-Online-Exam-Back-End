package com.example.demo.command;

import com.example.demo.common.annotations.CheckListSize;
import java.time.LocalDateTime;
import java.util.List;
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

	@CheckListSize List<String> emails;

	private LocalDateTime startAt;

	private LocalDateTime endAt;
}
