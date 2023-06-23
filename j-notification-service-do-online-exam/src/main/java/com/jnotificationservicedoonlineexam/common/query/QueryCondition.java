package com.jnotificationservicedoonlineexam.common.query;

import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QueryCondition {
	private String operation;
	private Object value;
}
