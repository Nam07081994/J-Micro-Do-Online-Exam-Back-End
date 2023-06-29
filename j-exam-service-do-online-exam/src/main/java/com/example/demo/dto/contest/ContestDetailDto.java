package com.example.demo.dto.contest;

import com.example.demo.entity.Contest;
import java.util.List;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ContestDetailDto {
	private String name;
	private String examName;
	private String ownerName;
	private String description;
	private String startAt;
	private String endAt;
	private List<AccountExamDto> participants;

	public ContestDetailDto(
			Contest contest, List<AccountExamDto> users, String examName, String ownerName) {
		this.name = contest.getName();
		this.description = contest.getDescription();
		this.endAt = contest.getEndAt().toString();
		this.startAt = contest.getStartAt().toString();
		this.ownerName = ownerName;
		this.examName = examName;
		this.participants = users;
	}
}
