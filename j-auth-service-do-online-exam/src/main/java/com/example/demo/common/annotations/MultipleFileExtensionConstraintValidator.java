package com.example.demo.common.annotations;

import com.example.demo.constant.StringConstant;
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
		if (Objects.equals(fileName, StringConstant.EMPTY_STRING)) {
			throw new FileNotFoundException(fileName + "Not found file");
		}

		return fileName.substring(fileName.lastIndexOf(StringConstant.DOT_STRING_CHARACTER) + 1);
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
