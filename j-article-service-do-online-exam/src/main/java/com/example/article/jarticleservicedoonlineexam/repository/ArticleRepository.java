package com.example.article.jarticleservicedoonlineexam.repository;

import com.example.article.jarticleservicedoonlineexam.entity.Article;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository
		extends JpaRepository<Article, Long>, AbstractRepository<Article> {
	Optional<Article> findByTitle(String title);
}
