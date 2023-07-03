package com.example.demo.exceptions;

import static com.example.demo.constant.Constant.VALIDATE_KEY;
import static com.example.demo.constant.TranslationCodeConstants.EXECUTE_SQL_ERROR;
import static com.example.demo.constant.TranslationCodeConstants.NOT_FOUND_FILE_ERROR;

import com.example.demo.common.response.GenerateResponseHelper;
import com.example.demo.service.TranslationService;
import java.io.FileNotFoundException;
import java.io.IOException;
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
	@ExceptionHandler(InvalidDateFormatException.class)
	public ResponseEntity<?> handleInvalidDateFormatException(InvalidDateFormatException ex) {
		return GenerateResponseHelper.generateMessageResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(IOException.class)
	public ResponseEntity<?> handleIOException(IOException ex) {
		return GenerateResponseHelper.generateMessageResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<?> handleInvalidArgumentException(MethodArgumentNotValidException ex) {
		Map<String, String> errorMap = new HashMap<>();
		ex.getBindingResult()
				.getFieldErrors()
				.forEach(err -> errorMap.put(err.getField(), err.getDefaultMessage()));

		return GenerateResponseHelper.generateDataResponse(
				HttpStatus.BAD_REQUEST, Map.of(VALIDATE_KEY, errorMap));
	}
}
