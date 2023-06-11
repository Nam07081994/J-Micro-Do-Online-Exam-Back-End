package com.example.demo.common.response;

import com.example.demo.constant.StringConstant;
import java.util.Map;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

public class GenerateResponseHelper {
	public static ResponseEntity<?> generateMessageResponse(HttpStatusCode statusCode, String msg) {
		return ResponseEntity.status(statusCode)
				.body(
						CommonResponse.builder()
								.body(Map.of(StringConstant.MESSAGE_KEY, msg))
								.build()
								.getBody());
	}

	public static ResponseEntity<?> generateDataResponse(
			HttpStatusCode statusCode, Map<String, Object> body) {
		return ResponseEntity.status(statusCode)
				.body(CommonResponse.builder().body(body).build().getBody());
	}
}
