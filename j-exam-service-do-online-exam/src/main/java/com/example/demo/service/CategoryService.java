package com.example.demo.service;

import static com.example.demo.constant.Constant.*;
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
import com.example.demo.exceptions.InvalidDateFormatException;
import com.example.demo.repository.CategoryRepository;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class CategoryService {

	@Value("${app.url.upload-img-endpoint}")
	private String UPLOAD_IMAGE_URI;

	@Value("${app.url.update-img-endpoint}")
	private String UPDATE_IMAGE_URI;

	@Autowired private RestTemplate restTemplate;

	@Autowired private CategoryRepository categoryRepository;

	@Autowired private TranslationService translationService;

	public ResponseEntity<?> getAllCategories(QuerySearchCommand command, String name)
			throws ExecuteSQLException, InvalidDateFormatException {
		Map<String, QueryCondition> searchParams = new HashMap<>();

		if (!StringUtils.isEmpty(name)) {
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

	public ResponseEntity<?> createCategory(CreateCategoryCommand command) throws IOException {
		var categoryCheck = categoryRepository.findByCategoryName(command.getCategoryName());
		if (categoryCheck.isPresent()) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST, translationService.getTranslation(CATEGORY_NAME_EXISTED));
		}

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		body.add(DOMAIN_KEY, CATEGORY_DOMAIN_NAME);
		body.add(FILE_TYPE_KEY, IMAGE_FOLDER_TYPE);
		ByteArrayResource contentsAsResource =
				new ByteArrayResource(command.getImage().getBytes()) {
					@Override
					public String getFilename() {
						return command.getImage().getOriginalFilename();
					}
				};
		body.add(FILE_KEY, contentsAsResource);
		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
		ResponseEntity<String> response =
				restTemplate.exchange(UPLOAD_IMAGE_URI, HttpMethod.POST, requestEntity, String.class);

		var category =
				Category.builder()
						.categoryName(command.getCategoryName())
						.thumbnail(response.getBody())
						.build();
		categoryRepository.save(category);

		return GenerateResponseHelper.generateMessageResponse(
				HttpStatus.OK, translationService.getTranslation(SAVE_CATEGORY_INFORMATION_SUCCESS));
	}

	public ResponseEntity<?> updateCategoryInfo(Long id, String name) {
		var categoryCheck = categoryRepository.findById(id);
		if (categoryCheck.isPresent()) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST,
					translationService.getTranslation(NOT_FOUND_CATEGORY_INFORMATION));
		}
		var category = categoryCheck.get();
		if (!category.getCategoryName().equals(name)) {
			Optional<Category> cateOpt = categoryRepository.findByCategoryName(name);
			if (cateOpt.isPresent()) {
				return GenerateResponseHelper.generateMessageResponse(
						HttpStatus.BAD_REQUEST, translationService.getTranslation(CATEGORY_NAME_EXISTED));
			}
		}

		category.setCategoryName(name);
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

	public ResponseEntity<?> updateCategoryThumbnail(UpdateCategoryCommand command) {
		var categoryCheck = categoryRepository.findById(command.getCategoryId());
		if (categoryCheck.isEmpty()) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST,
					translationService.getTranslation(NOT_FOUND_CATEGORY_INFORMATION));
		}
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.MULTIPART_FORM_DATA);
			MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
			body.add(DOMAIN_KEY, CATEGORY_DOMAIN_NAME);
			body.add(OLD_IMAGE_PATH_KEY, categoryCheck.get().getThumbnail());
			ByteArrayResource contentsAsResource =
					new ByteArrayResource(command.getImage().getBytes()) {
						@Override
						public String getFilename() {
							return command.getImage().getOriginalFilename();
						}
					};
			body.add(FILE_KEY, contentsAsResource);
			HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
			ResponseEntity<String> resp =
					restTemplate.exchange(UPDATE_IMAGE_URI, HttpMethod.POST, requestEntity, String.class);
			categoryCheck.get().setThumbnail(resp.getBody());
			categoryRepository.save(categoryCheck.get());

			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.OK, translationService.getTranslation(UPDATE_CATEGORY_THUMBNAIL_SUCCESS));
		} catch (Exception ex) {
			return null;
		}
	}
}
