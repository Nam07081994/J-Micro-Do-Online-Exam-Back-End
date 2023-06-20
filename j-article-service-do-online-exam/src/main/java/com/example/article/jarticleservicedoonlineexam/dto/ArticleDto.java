package com.example.article.jarticleservicedoonlineexam.dto;

import com.example.article.jarticleservicedoonlineexam.entity.Article;
import lombok.*;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ArticleDto {
	private Long id;

	private String title;

	private String author;

	private String content;

	private String thumbnail;

	public ArticleDto(Article article) {
		this.id = article.getId();
		this.thumbnail = article.getThumbnail();
		this.title = article.getTitle();
		this.author = article.getAuthor();
		this.content = article.getContent();
	}
}
