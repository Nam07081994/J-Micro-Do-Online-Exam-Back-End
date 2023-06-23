package com.jnotificationservicedoonlineexam.exceptions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jnotificationservicedoonlineexam.common.response.GenerateResponseHelper;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApplicationExceptionHandler {
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<?> handleInvalidArgumentException(MethodArgumentNotValidException ex) {
		Map<String, String> errorMap = new HashMap<>();
		ex.getBindingResult()
				.getFieldErrors()
				.forEach(err -> errorMap.put(err.getField(), err.getDefaultMessage()));

		return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(ExecuteSQLException.class)
	public ResponseEntity<?> handleExecuteSQLException() {
		return GenerateResponseHelper.generateMessageResponse(HttpStatus.BAD_REQUEST, "");
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(JsonProcessingException.class)
	public ResponseEntity<?> handleJsonProcessingException() {
		return GenerateResponseHelper.generateMessageResponse(HttpStatus.BAD_REQUEST, "");
	}
}
