package com.example.demo.service;

import static com.example.demo.constant.Constant.DATA_KEY;
import static com.example.demo.constant.SQLConstants.CATEGORY_NAME_KEY;
import static com.example.demo.constant.SQLConstants.LIKE_OPERATOR;
import static com.example.demo.constant.TranslationCodeConstants.*;

import com.example.demo.command.QuerySearchCommand;
import com.example.demo.command.category.CreateCategoryCommand;
import com.example.demo.command.category.UpdateCategoryCommand;
import com.example.demo.common.query.QueryCondition;
import com.example.demo.common.query.QueryDateCondition;
import com.example.demo.common.response.GenerateResponseHelper;
import com.example.demo.dto.category.CategoryDto;
import com.example.demo.dto.category.CategoryOptionDto;
import com.example.demo.entity.Category;
import com.example.demo.exceptions.ExecuteSQLException;
import com.example.demo.repository.CategoryRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CategoryService {
	private CategoryRepository categoryRepository;

	private TranslationService translationService;

	public ResponseEntity<?> getAllCategories(QuerySearchCommand command, String name)
			throws ExecuteSQLException {
		Map<String, QueryCondition> searchParams = new HashMap<>();

		if (!name.isEmpty()) {
			searchParams.put(
					CATEGORY_NAME_KEY, QueryCondition.builder().operation(LIKE_OPERATOR).value(name).build());
		}

		if (QueryDateCondition.generate(command, searchParams))
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST, translationService.getTranslation(FROM_DATE_TO_DATE_INVALID));

		var result =
				categoryRepository.search(
						searchParams,
						Map.of(),
						command.getOrder_by(),
						command.getPage_size(),
						command.getPage_index(),
						Category.class);

		List<Category> categories = (List<Category>) result.get(DATA_KEY);

		result.put(DATA_KEY, categories.stream().map(CategoryDto::new).collect(Collectors.toList()));

		return GenerateResponseHelper.generateDataResponse(HttpStatus.OK, result);
	}

	public ResponseEntity<?> getCategoryDetail(Long id) {
		var category = categoryRepository.findById(id);

		if (category.isEmpty()) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST,
					translationService.getTranslation(NOT_FOUND_CATEGORY_INFORMATION));
		}

		return GenerateResponseHelper.generateDataResponse(
				HttpStatus.OK, Map.of(DATA_KEY, new CategoryDto(category.get())));
	}

	public ResponseEntity<?> getAllCategoriesOption() {
		List<CategoryOptionDto> categories =
				categoryRepository.findAll().stream().map(CategoryOptionDto::new).toList();

		return GenerateResponseHelper.generateDataResponse(HttpStatus.OK, Map.of(DATA_KEY, categories));
	}

	public ResponseEntity<?> createCategory(CreateCategoryCommand command) {
		var categoryCheck = categoryRepository.findByCategoryName(command.getCategoryName());
		if (categoryCheck.isPresent()) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST, translationService.getTranslation(CATEGORY_NAME_EXISTED));
		}

		var category = Category.builder().categoryName(command.getCategoryName()).build();
		categoryRepository.save(category);

		return GenerateResponseHelper.generateMessageResponse(
				HttpStatus.OK, translationService.getTranslation(SAVE_CATEGORY_INFORMATION_SUCCESS));
	}

	public ResponseEntity<?> updateCategory(UpdateCategoryCommand command) {
		var categoryCheck = categoryRepository.findById(command.getCategoryId());
		if (categoryCheck.isPresent()) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST,
					translationService.getTranslation(NOT_FOUND_CATEGORY_INFORMATION));
		}
		var category = categoryCheck.get();
		if (!category.getCategoryName().equals(command.getCategoryName())) {
			Optional<Category> cateOpt = categoryRepository.findByCategoryName(command.getCategoryName());
			if (cateOpt.isPresent()) {
				return GenerateResponseHelper.generateMessageResponse(
						HttpStatus.BAD_REQUEST, translationService.getTranslation(CATEGORY_NAME_EXISTED));
			}
		}

		category.setCategoryName(command.getCategoryName());
		categoryRepository.save(category);

		return GenerateResponseHelper.generateMessageResponse(
				HttpStatus.OK, translationService.getTranslation(UPDATE_CATEGORY_INFORMATION_SUCCESS));
	}

	public ResponseEntity<?> deleteCategory(Long id) {
		var categoryCheck = categoryRepository.findById(id);
		if (categoryCheck.isEmpty()) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST,
					translationService.getTranslation(NOT_FOUND_CATEGORY_INFORMATION));
		}

		var category = categoryCheck.get();
		categoryRepository.delete(category);

		return GenerateResponseHelper.generateMessageResponse(
				HttpStatus.OK, translationService.getTranslation(DELETE_CATEGORY_INFORMATION_SUCCESS));
	}
}
