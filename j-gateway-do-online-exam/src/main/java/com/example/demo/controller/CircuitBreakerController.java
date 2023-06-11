package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/circuit-breaker")
public class CircuitBreakerController {
	@GetMapping("/fallback")
	public String getMessageCircuitBreaker() {
		return "Some thing wrong";
	}
}
