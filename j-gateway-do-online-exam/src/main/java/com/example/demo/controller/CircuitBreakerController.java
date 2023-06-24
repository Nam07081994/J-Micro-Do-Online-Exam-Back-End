package com.example.demo.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/circuit-breaker")
public class CircuitBreakerController {
	@GetMapping("/fallback")
	public String getMessageCircuitBreakerGetMethod() {
		return "SERVER DOWN!!";
	}

	@PostMapping("/fallback")
	public String getMessageCircuitBreakerPostMethod() {
		return "SERVER DOWN!!";
	}

	@PutMapping("/fallback")
	public String getMessageCircuitBreakerPutMethod() {
		return "SERVER DOWN!!";
	}

	@DeleteMapping("/fallback")
	public String getMessageCircuitBreakerDeleteMethod() {
		return "SERVER DOWN!!";
	}
}
