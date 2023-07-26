package com.example.demo.dto.contest;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ContestUserDto {
	private Long examID;
	private String endAt;
	private String startAt;
	private String examName;
	private String contestName;
}
