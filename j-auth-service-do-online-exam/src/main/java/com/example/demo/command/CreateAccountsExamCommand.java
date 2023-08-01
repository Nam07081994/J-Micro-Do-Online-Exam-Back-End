package com.example.demo.command;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.List;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateAccountsExamCommand {
	private Long contestID;

	private String examName;

	private List<User> userInfo;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm")
	private LocalDateTime startAt;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm")
	private LocalDateTime endAt;

	@Data
	public static class User {
		private String username;
		private String email;
	}
}
