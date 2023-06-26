package com.example.demo.controller;

import com.example.demo.command.QuerySearchCommand;
import com.example.demo.command.category.CreateCategoryCommand;
import com.example.demo.command.category.UpdateCategoryCommand;
import com.example.demo.exceptions.ExecuteSQLException;
import com.example.demo.exceptions.InvalidDateFormatException;
import com.example.demo.service.CategoryService;
import jakarta.validation.Valid;
import java.io.IOException;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/exams/categories")
public class CategoryController {

	private CategoryService categoryService;

	@GetMapping("/get")
	public ResponseEntity<?> getAllCategories(
			@RequestParam(name = "name", required = false) String name,
			@RequestParam(name = "from_date", required = false) String from_date,
			@RequestParam(name = "to_date", required = false) String to_date,
			@RequestParam(name = "page_size", defaultValue = "10") int page_size,
			@RequestParam(name = "page_index", defaultValue = "-1") int page_index,
			@RequestParam(name = "order_by", defaultValue = "-1") int order_by)
			throws ExecuteSQLException, InvalidDateFormatException {

		return categoryService.getAllCategories(
				QuerySearchCommand.from(from_date, to_date, page_index, page_size, order_by), name);
	}

	@GetMapping("/options")
	public ResponseEntity<?> getCategoriesOption() {
		return categoryService.getAllCategoriesOption();
	}

	@GetMapping("/detail")
	public ResponseEntity<?> getCategoryDetail(@RequestParam("id") Long id) {
		return categoryService.getCategoryDetail(id);
	}

	@PostMapping(
			value = "/create",
			consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
	public ResponseEntity<?> createCategory(@ModelAttribute @Valid CreateCategoryCommand command)
			throws IOException {
		return categoryService.createCategory(command);
	}

	@PutMapping(
			value = "/update/thumbnail",
			consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
	public ResponseEntity<?> updateCategoryThumbnail(
			@ModelAttribute @Valid UpdateCategoryCommand command) {
		return categoryService.updateCategoryThumbnail(command);
	}

	@PutMapping("/update/info")
	public ResponseEntity<?> updateCategoryInfo(
			@RequestParam("id") Long id, @RequestParam("name") String name) {
		return categoryService.updateCategoryInfo(id, name);
	}

	@DeleteMapping("/delete")
	public ResponseEntity<?> deleteCategory(@RequestParam("id") Long id) {
		return categoryService.deleteCategory(id);
	}
}
