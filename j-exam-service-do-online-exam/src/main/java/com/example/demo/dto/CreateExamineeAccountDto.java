package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateExamineeAccountDto {
	private Long contestID;

	private String examName;

	private List<User> userInfo;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm")
	private LocalDateTime startAt;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm")
	private LocalDateTime endAt;

	@Data
	@Builder
	public static class User {
		private String username;
		private String email;
	}
}
