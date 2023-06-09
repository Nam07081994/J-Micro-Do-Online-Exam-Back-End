package com.example.demo.controller;

import com.example.demo.command.CreateCategoryCommand;
import com.example.demo.command.DeleteCategoryCommand;
import com.example.demo.command.UpdateCategoryCommand;
import com.example.demo.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
@RequestMapping("/api/v1/exam/category")
public class CategoryController {
	@Autowired private CategoryService categoryService;

	@GetMapping("/getCategories")
	public ResponseEntity<?> getAllCategories(
			@RequestParam(defaultValue = "0") Integer page,
			@RequestParam(defaultValue = "10") Integer size) {
		Pageable paging = PageRequest.of(page, size);
		return categoryService.getAllCategories(paging);
	}

	@GetMapping("/detail")
	public ResponseEntity<?> getCategoryDetail(@RequestBody Long categoryId) {
		return categoryService.getCategoryDetail(categoryId);
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
	public ResponseEntity<?> deleteCategory(@RequestBody DeleteCategoryCommand command) {
		return categoryService.deleteCategory(command);
	}
}
