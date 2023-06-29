package com.example.demo.dto.contest;

import com.example.demo.entity.Contest;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ContestOwnerDto {
	private Long id;
	private String name;
	private String examName;
	private String ownerName;
	private String startAt;
	private String endAt;

	public ContestOwnerDto(Contest contest, String ownerName, String examName) {
		this.id = contest.getId();
		this.name = contest.getName();
		this.startAt = contest.getStartAt().toString();
		this.endAt = contest.getEndAt().toString();
		this.ownerName = ownerName;
		this.examName = examName;
	}
}
