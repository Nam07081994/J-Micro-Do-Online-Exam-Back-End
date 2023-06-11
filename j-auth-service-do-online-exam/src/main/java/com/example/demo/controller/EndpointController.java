package com.example.demo.controller;

import com.example.demo.service.EndPointService;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/auth/endpoints")
public class EndpointController {
	private EndPointService endPointService;

	@GetMapping
	public ResponseEntity<?> getEndPoints() {
		return endPointService.endPointService();
	}

	@GetMapping("/options")
	public ResponseEntity<?> getEndpointsOption() {
		return endPointService.getEndpointsOption();
	}

	@PostMapping("/create")
	public ResponseEntity<?> makeEndPoint(
			@RequestParam("endPointPath") @NotEmpty String endPointPath) {
		return endPointService.saveEndPoint(endPointPath);
	}

	@PutMapping("/edit")
	public ResponseEntity<?> editEndPoint(
			@RequestParam("endPointPath") String endPointPath, @RequestParam("id") Long id) {
		return endPointService.editEndPoint(endPointPath, id);
	}

	@DeleteMapping("/delete")
	public ResponseEntity<?> deleteEndPoint(@RequestParam("id") Long id) {
		return endPointService.deleteEndPoint(id);
	}
}
