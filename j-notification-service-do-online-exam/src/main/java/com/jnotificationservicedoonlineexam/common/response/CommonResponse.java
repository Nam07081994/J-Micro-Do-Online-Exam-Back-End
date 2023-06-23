package com.jnotificationservicedoonlineexam.common.response;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommonResponse {
	private Map<String, Object> body;

	public static Map<String, Object> message(String message) {
		return Map.of("message", message);
	}
}
