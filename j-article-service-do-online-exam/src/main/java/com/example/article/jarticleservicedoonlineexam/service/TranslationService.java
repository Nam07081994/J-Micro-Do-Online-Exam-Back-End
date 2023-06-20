package com.example.article.jarticleservicedoonlineexam.service;

import static com.example.article.jarticleservicedoonlineexam.configs.i18n.Translator.toLocale;

import org.springframework.stereotype.Service;

@Service
public class TranslationService {
	public String getTranslation(String code) {
		return toLocale(code);
	}
}
