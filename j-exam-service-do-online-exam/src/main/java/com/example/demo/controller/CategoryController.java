package com.example.demo.controller;

import com.example.demo.command.QuerySearchCommand;
import com.example.demo.command.category.CreateCategoryCommand;
import com.example.demo.command.category.UpdateCategoryCommand;
import com.example.demo.exceptions.ExecuteSQLException;
import com.example.demo.service.CategoryService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
			throws ExecuteSQLException {

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

	@PostMapping("/create")
	public ResponseEntity<?> createCategory(@RequestBody CreateCategoryCommand command) {
		return categoryService.createCategory(command);
	}

	@PutMapping("/update")
	public ResponseEntity<?> udpateCategory(@RequestBody UpdateCategoryCommand command) {
		return categoryService.updateCategory(command);
	}

	@DeleteMapping("/delete")
	public ResponseEntity<?> deleteCategory(@RequestParam("id") Long id) {
		return categoryService.deleteCategory(id);
	}
}
