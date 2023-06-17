package com.example.demo.repository;

import com.example.demo.entity.Category;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository
		extends JpaRepository<Category, Long>, AbstractRepository<Category> {
	Optional<Category> findByCategoryName(String categoryName);
}
