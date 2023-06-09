package com.example.demo.filter;

import java.util.List;
import java.util.function.Predicate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

@Component
public class RouterValidator {
	@Value("${app.openApiEndPoints}")
	private List<String> openApiEndPoints;

	public Predicate<ServerHttpRequest> isSecured =
			request ->
					openApiEndPoints.stream().noneMatch(uri -> request.getURI().getPath().contains(uri));
}
