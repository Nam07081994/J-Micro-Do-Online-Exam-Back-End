package com.example.article.jarticleservicedoonlineexam.service;

import static com.example.article.jarticleservicedoonlineexam.constants.Constants.*;
import static com.example.article.jarticleservicedoonlineexam.constants.SQLConstants.*;
import static com.example.article.jarticleservicedoonlineexam.constants.TranslationCodeConstants.*;

import com.example.article.jarticleservicedoonlineexam.command.CreateArticleCommand;
import com.example.article.jarticleservicedoonlineexam.command.QuerySearchCommand;
import com.example.article.jarticleservicedoonlineexam.command.UpdateArticleThumbnailCommand;
import com.example.article.jarticleservicedoonlineexam.command.UpdateInfoArticleCommand;
import com.example.article.jarticleservicedoonlineexam.common.query.QueryCondition;
import com.example.article.jarticleservicedoonlineexam.common.query.QueryDateCondition;
import com.example.article.jarticleservicedoonlineexam.common.response.GenerateResponseHelper;
import com.example.article.jarticleservicedoonlineexam.dto.ArticleDto;
import com.example.article.jarticleservicedoonlineexam.entity.Article;
import com.example.article.jarticleservicedoonlineexam.exceptions.ExecuteSQLException;
import com.example.article.jarticleservicedoonlineexam.repository.ArticleRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class ArticleService {

	@Value("${app.url.upload-file-endpoint}")
	private String SAVE_FILE_URI;

	@Value("${app.url.update-file-endpoint}")
	private String UPDATE_FILE_URI;

	@Autowired private RestTemplate restTemplate;

	@Autowired private ArticleRepository articleRepository;

	@Autowired private TranslationService translationService;

	public ResponseEntity<?> getArticles(QuerySearchCommand command, String title, String author)
			throws ExecuteSQLException {
		Map<String, QueryCondition> searchParams = new HashMap<>();

		if (!title.isEmpty()) {
			searchParams.put(
					ARTICLE_TITLE_KEY,
					QueryCondition.builder().value(title).operation(LIKE_OPERATOR).build());
		}

		if (!author.isEmpty()) {
			searchParams.put(
					ARTICLE_AUTHOR_KEY,
					QueryCondition.builder().value(author).operation(LIKE_OPERATOR).build());
		}

		if (QueryDateCondition.generate(command, searchParams))
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST, translationService.getTranslation(FROM_DATE_TO_DATE_INVALID));

		var result =
				articleRepository.search(
						searchParams,
						Map.of(),
						command.getOrder_by(),
						command.getPage_size(),
						command.getPage_index(),
						Article.class);

		List<Article> articles = (List<Article>) result.get(DATA_KEY);

		result.put(DATA_KEY, articles.stream().map(ArticleDto::new).collect(Collectors.toList()));

		return null;
	}

	public ResponseEntity<?> deleteArticle(Long id) {
		Optional<Article> articleOpt = articleRepository.findById(id);
		if (articleOpt.isEmpty()) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST, translationService.getTranslation(NOT_FOUND_ARTICLE));
		}

		articleRepository.delete(articleOpt.get());

		return GenerateResponseHelper.generateMessageResponse(
				HttpStatus.OK, translationService.getTranslation(DELETE_ARTICLE_SUCCESS));
	}

	public ResponseEntity<?> makeArticle(CreateArticleCommand article) {
		Optional<Article> articleOpt = articleRepository.findByTitle(article.getTitle());
		if (articleOpt.isPresent()) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST, translationService.getTranslation(INVALID_ARTICLE_TITLE_EXIST));
		}

		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.MULTIPART_FORM_DATA);
			MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
			body.add(DOMAIN_KEY, ARTICLE_DOMAIN_NAME);
			body.add(FILE_TYPE_KEY, IMAGE_FOLDER_TYPE);
			ByteArrayResource contentsAsResource =
					new ByteArrayResource(article.getImage().getBytes()) {
						@Override
						public String getFilename() {
							return article.getImage().getOriginalFilename();
						}
					};
			body.add(FILE_KEY, contentsAsResource);
			HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
			ResponseEntity<String> resp =
					restTemplate.exchange(SAVE_FILE_URI, HttpMethod.POST, requestEntity, String.class);

			Article newArticle =
					Article.builder()
							.author(article.getAuthor())
							.title(article.getTitle())
							.thumbnail(resp.getBody())
							.content(article.getContent())
							.build();

			articleRepository.save(newArticle);
			// TODO: send event new article is created -> notification service

			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.OK, translationService.getTranslation(CREATE_ARTICLE_SUCCESS));
		} catch (Exception ex) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST, translationService.getTranslation(HAVING_ERROR_CREATE_ARTICLE));
		}
	}

	public ResponseEntity<?> updateThumbnail(UpdateArticleThumbnailCommand req) {
		Optional<Article> articleOpt = articleRepository.findById(req.getId());
		if (articleOpt.isEmpty()) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST, translationService.getTranslation(NOT_FOUND_ARTICLE));
		}

		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.MULTIPART_FORM_DATA);
			MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
			body.add(DOMAIN_KEY, ARTICLE_DOMAIN_NAME);
			body.add(OLD_IMAGE_PATH_KEY, articleOpt.get().getThumbnail());
			ByteArrayResource contentsAsResource =
					new ByteArrayResource(req.getImage().getBytes()) {
						@Override
						public String getFilename() {
							return req.getImage().getOriginalFilename();
						}
					};
			body.add(FILE_KEY, contentsAsResource);
			HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
			ResponseEntity<String> resp =
					restTemplate.exchange(UPDATE_FILE_URI, HttpMethod.POST, requestEntity, String.class);
			articleOpt.get().setThumbnail(resp.getBody());
			articleRepository.save(articleOpt.get());

			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.OK, translationService.getTranslation(UPDATE_ARTICLE_THUMBNAIL_SUCCESS));
		} catch (Exception ex) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST,
					translationService.getTranslation(HAVING_ERROR_UPDATE_ARTICLE_THUMBNAIL));
		}
	}

	public ResponseEntity<?> updateArticleInfo(UpdateInfoArticleCommand article, Long id) {
		Optional<Article> articleOpt = articleRepository.findById(id);
		if (articleOpt.isEmpty()) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST, translationService.getTranslation(NOT_FOUND_ARTICLE));
		}

		if (!articleOpt.get().getTitle().equals(article.getTitle())
				&& articleRepository.findByTitle(article.getTitle()).isPresent()) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST, translationService.getTranslation(INVALID_ARTICLE_TITLE_EXIST));
		}

		articleOpt.get().setAuthor(article.getAuthor());

		articleOpt.get().setTitle(article.getTitle());

		articleOpt.get().setContent(article.getContent());

		articleRepository.save(articleOpt.get());

		return GenerateResponseHelper.generateMessageResponse(
				HttpStatus.OK, translationService.getTranslation(UPDATE_ARTICLE_INFO_SUCCESS));
	}

	public ResponseEntity<?> getArticleByTitle(String title) {
		Optional<Article> articleOpt = articleRepository.findByTitle(title);
		if (articleOpt.isEmpty()) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST, translationService.getTranslation(NOT_FOUND_ARTICLE));
		}

		return GenerateResponseHelper.generateDataResponse(
				HttpStatus.OK, Map.of(DATA_KEY, new ArticleDto(articleOpt.get())));
	}

	public ResponseEntity<?> getArticleByID(Long id) {
		Optional<Article> articleOpt = articleRepository.findById(id);
		if (articleOpt.isEmpty()) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST, translationService.getTranslation(NOT_FOUND_ARTICLE));
		}

		return GenerateResponseHelper.generateDataResponse(
				HttpStatus.OK, Map.of(DATA_KEY, new ArticleDto(articleOpt.get())));
	}
}
