package com.example.demo.controller;

import static com.example.demo.constant.TranslationCodeConstant.*;

import com.example.demo.common.response.GenerateResponseHelper;
import com.example.demo.constant.StringConstant;
import com.example.demo.exceptions.ExecuteSQLException;
import com.example.demo.service.TranslationService;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@AllArgsConstructor
public class ApplicationExceptionHandler {
	// TODO: add log
	private final TranslationService translationService;

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(FileNotFoundException.class)
	public ResponseEntity<?> handleFileNotFoundException() {
		return GenerateResponseHelper.generateMessageResponse(
				HttpStatus.BAD_REQUEST, translationService.getTranslation(NOT_FOUND_FILE_ERROR));
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(ExecuteSQLException.class)
	public ResponseEntity<?> handleExecuteSQLException() {
		return GenerateResponseHelper.generateMessageResponse(
				HttpStatus.BAD_REQUEST, translationService.getTranslation(EXECUTE_SQL_ERROR));
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<?> handleInvalidArgumentException(MethodArgumentNotValidException ex) {
		Map<String, String> errorMap = new HashMap<>();
		ex.getBindingResult()
				.getFieldErrors()
				.forEach(err -> errorMap.put(err.getField(), err.getDefaultMessage()));

		return GenerateResponseHelper.generateDataResponse(
				HttpStatus.BAD_REQUEST,
				Map.of(
						StringConstant.MESSAGE_KEY,
						translationService.getTranslation(INVALID_INPUT_INFORMATION),
						StringConstant.VALIDATE_KEY,
						errorMap));
	}
}
