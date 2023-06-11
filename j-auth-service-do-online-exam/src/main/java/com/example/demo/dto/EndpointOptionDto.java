package com.example.demo.dto;

import com.example.demo.entity.EndPoint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EndpointOptionDto {
	private Long id;
	private String name;

	public EndpointOptionDto(EndPoint endPoint) {
		this.id = endPoint.getId();
		this.name = endPoint.getEndPoint();
	}
}
