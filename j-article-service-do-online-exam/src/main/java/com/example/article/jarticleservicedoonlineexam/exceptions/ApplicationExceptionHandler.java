package com.example.article.jarticleservicedoonlineexam.exceptions;

import static com.example.article.jarticleservicedoonlineexam.constants.TranslationCodeConstants.EXECUTE_SQL_ERROR;

import com.example.article.jarticleservicedoonlineexam.common.response.GenerateResponseHelper;
import com.example.article.jarticleservicedoonlineexam.service.TranslationService;
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

	private TranslationService translationService;

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(IOException.class)
	public String handleIOException(IOException ex) {
		return ex.getMessage();
	}

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
		return GenerateResponseHelper.generateMessageResponse(
				HttpStatus.BAD_REQUEST, translationService.getTranslation(EXECUTE_SQL_ERROR));
	}
}
