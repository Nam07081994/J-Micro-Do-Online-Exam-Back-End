package com.example.file.jfileservicedoonlineexam.common.annotations;

import com.example.file.jfileservicedoonlineexam.service.FileService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.io.FileNotFoundException;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

public class MultipleFileExtensionConstraintValidator
		implements ConstraintValidator<MultipleFileExtension, MultipartFile> {
	@Autowired private FileService fileService;

	private String[] allowedTypes;

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
			fileType = fileService.getExtensionFile(multipartFile.getOriginalFilename());
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}
		if (!Arrays.asList(allowedTypes).contains(fileType)) {
			return false;
		}

		return true;
	}
}
