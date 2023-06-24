package com.example.article.jarticleservicedoonlineexam.controller;

import com.example.article.jarticleservicedoonlineexam.command.CreateArticleCommand;
import com.example.article.jarticleservicedoonlineexam.command.QuerySearchCommand;
import com.example.article.jarticleservicedoonlineexam.command.UpdateArticleThumbnailCommand;
import com.example.article.jarticleservicedoonlineexam.command.UpdateInfoArticleCommand;
import com.example.article.jarticleservicedoonlineexam.exceptions.ExecuteSQLException;
import com.example.article.jarticleservicedoonlineexam.exceptions.InvalidDateFormatException;
import com.example.article.jarticleservicedoonlineexam.service.ArticleService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/articles")
public class ArticlesController {

	private final ArticleService articleService;

	@GetMapping
	public ResponseEntity<?> getArticles(
			@RequestParam(name = "title", required = false) String title,
			@RequestParam(name = "author", required = false) String author,
			@RequestParam(name = "from_date", required = false) String from_date,
			@RequestParam(name = "to_date", required = false) String to_date,
			@RequestParam(name = "page_size", defaultValue = "10") int page_size,
			@RequestParam(name = "page_index", defaultValue = "-1") int page_index,
			@RequestParam(name = "order_by", defaultValue = "-1") int order_by)
			throws ExecuteSQLException, InvalidDateFormatException {

		return articleService.getArticles(
				QuerySearchCommand.from(from_date, to_date, page_index, page_size, order_by),
				title,
				author);
	}

	@PostMapping(
			value = "/create",
			consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
	public ResponseEntity<?> makeArticle(@ModelAttribute @Valid CreateArticleCommand article) {
		return articleService.makeArticle(article);
	}

	@PutMapping("/update")
	public ResponseEntity<?> updateArticle(
			@RequestBody UpdateInfoArticleCommand article, @RequestParam Long id) {
		return articleService.updateArticleInfo(article, id);
	}

	@PutMapping(
			value = "/update-img",
			consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
	public ResponseEntity<?> updateArticleImg(
			@ModelAttribute @Valid UpdateArticleThumbnailCommand req) {
		return articleService.updateThumbnail(req);
	}

	@GetMapping(value = "/name")
	public ResponseEntity<?> getArticleByName(@RequestParam String title) {
		return articleService.getArticleByTitle(title);
	}

	@GetMapping(value = "/id")
	public ResponseEntity<?> getArticleById(@RequestParam Long id) {
		return articleService.getArticleByID(id);
	}

	@DeleteMapping("/delete")
	public ResponseEntity<?> deleteArticle(@RequestParam Long id) {
		return articleService.deleteArticle(id);
	}
}
