package com.example.demo.controller;

import java.util.HashMap;
import java.util.Map;

import com.example.demo.common.response.HTTPResponseObject;
import com.example.demo.common.response.HTTPResponseObjectBuilder;
import com.example.demo.service.TranslationService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.example.demo.constant.TranslationCodeConstant.INVALID_INPUT_INFORMATION;

@RestControllerAdvice
@AllArgsConstructor
public class ApplicationExceptionHandler {

    private final TranslationService translationService;

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<HTTPResponseObject> handleInvalidArgumentException(MethodArgumentNotValidException ex) {
        Map<String, String> errorMap = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(err -> errorMap.put(err.getField(), err.getDefaultMessage()));

        return new ResponseEntity<>(new HTTPResponseObjectBuilder()
                .withMessage(translationService.getTranslation(INVALID_INPUT_INFORMATION))
                .addData("validate", errorMap)
                .build(), HttpStatus.BAD_REQUEST);
    }
}
