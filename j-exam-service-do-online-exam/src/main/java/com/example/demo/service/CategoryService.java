package com.example.demo.service;

import java.util.Map;

import com.example.demo.command.CreateCategoryCommand;
import com.example.demo.command.DeleteCategoryCommand;
import com.example.demo.command.UpdateCategoryCommand;
import com.example.demo.common.response.CommonResponse;
import com.example.demo.entity.Category;
import com.example.demo.repository.CategoryRepository;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    public ResponseEntity<?> getAllCategories(Pageable pageable){
        var page = categoryRepository.findAllByOrderByIdDesc(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.builder()
                .body(Map.of("categories", page))
                .build()
                .getBody());
    }

    public ResponseEntity<?> getCategoryDetail(Long id){
        var category = categoryRepository.findById(id);
        if(!category.isPresent()){
            //TODO: Add i18n for message
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CommonResponse.builder()
                    .body(Map.of("message", "Category is not exist!!"))
                    .build()
                    .getBody());
        }
        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.builder()
                .body(Map.of("category", category.get()))
                .build()
                .getBody());
    }

    public ResponseEntity<?> createCategory(CreateCategoryCommand command){
        var categoryCheck = categoryRepository.findByCategoryName(command.getCategoryName());
        if(categoryCheck.isPresent()){
            //TODO: Add i18n for message
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CommonResponse.builder()
                    .body(Map.of("message", "Category is exist!!"))
                    .build()
                    .getBody());
        }

        var category = Category.builder()
                .categoryName(command.getCategoryName())
                .build();
        categoryRepository.save(category);
        //TODO: Add i18n for message
        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.builder()
                .body(Map.of("message", "Create Category Success"))
                .build()
                .getBody());
    }

    public ResponseEntity<?> updateCategory(UpdateCategoryCommand command){
        var categoryCheck = categoryRepository.findById(command.getCategoryId());
        if(!categoryCheck.isPresent()){
            //TODO: Add i18n for message
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CommonResponse.builder()
                    .body(Map.of("message", "Category is not exist!!"))
                    .build()
                    .getBody());
        }
        val category = categoryCheck.get();
        category.setCategoryName(command.getCategoryName());
        categoryRepository.save(category);
        //TODO: Add i18n for message
        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.builder()
                .body(Map.of("message", "Update Category Success"))
                .build()
                .getBody());
    }

    public ResponseEntity<?> deleteCategory(DeleteCategoryCommand command){
        var categoryCheck = categoryRepository.findById(command.getCategoryId());
        if(!categoryCheck.isPresent()){
            //TODO: Add i18n for message
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CommonResponse.builder()
                    .body(Map.of("message", "Category is not exist!!"))
                    .build()
                    .getBody());
        }
        val category = categoryCheck.get();
        categoryRepository.delete(category);
        //TODO: Add i18n for message
        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.builder()
                .body(Map.of("message", "Delete Category Success"))
                .build()
                .getBody());
    }
}
