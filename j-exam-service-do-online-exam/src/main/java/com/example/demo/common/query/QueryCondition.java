package com.example.demo.common.query;

import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QueryCondition {
	private String operation;
	private Object value;
	private Object value2;
}
