package com.example.demo.controller;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/circuit-breaker")
public class CircuitBreakerController {
	@GetMapping("/fallback")
	public String getMessageCircuitBreakerGetMothod() {
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
