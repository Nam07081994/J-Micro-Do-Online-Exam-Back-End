package com.example.demo.common.anotations;

import static com.example.demo.constant.Constant.DOT_STRING_CHARACTER;
import static com.example.demo.constant.Constant.EMPTY_STRING;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Objects;
import org.springframework.web.multipart.MultipartFile;

public class MultipleFileExtensionConstraintValidator
		implements ConstraintValidator<MultipleFileExtension, MultipartFile> {

	private String[] allowedTypes;

	private String getExtensionFile(String fileName) throws FileNotFoundException {
		if (Objects.equals(fileName, EMPTY_STRING)) {
			throw new FileNotFoundException("Not found file " + fileName);
		}

		return fileName.substring(fileName.lastIndexOf(DOT_STRING_CHARACTER) + 1);
	}

	@Override
	public void initialize(MultipleFileExtension constraintAnnotation) {
		this.allowedTypes = constraintAnnotation.allowedTypes();
	}

	@Override
	public boolean isValid(
			MultipartFile multipartFile, ConstraintValidatorContext constraintValidatorContext) {
		if (multipartFile == null) {
			return false;
		}

		String fileType = null;
		try {
			fileType = getExtensionFile(multipartFile.getOriginalFilename());
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}

		if (!Arrays.asList(allowedTypes).contains(fileType)) {
			return false;
		}

		return true;
	}
}
