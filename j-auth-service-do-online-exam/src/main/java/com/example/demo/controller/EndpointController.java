package com.example.demo.controller;

import com.example.demo.command.CommonSearchCommand;
import com.example.demo.exceptions.ExecuteSQLException;
import com.example.demo.exceptions.InvalidDateFormatException;
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
	public ResponseEntity<?> getEndPoints(
			@RequestParam(name = "name", required = false) String name,
			@RequestParam(name = "from_date", required = false) String from_date,
			@RequestParam(name = "to_date", required = false) String to_date,
			@RequestParam(name = "page_size", defaultValue = "10") int page_size,
			@RequestParam(name = "page_index", defaultValue = "-1") int page_index,
			@RequestParam(name = "order_by", defaultValue = "-1") int order_by)
			throws ExecuteSQLException, InvalidDateFormatException {
		return endPointService.endPointService(
				CommonSearchCommand.from(from_date, to_date, page_index, page_size, order_by), name);
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
