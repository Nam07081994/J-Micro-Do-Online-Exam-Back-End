package com.jnotificationservicedoonlineexam.service;

import static com.jnotificationservicedoonlineexam.config.i18n.Translator.toLocale;

import org.springframework.stereotype.Service;

@Service
public class TranslationService {
	public String getTranslation(String code) {
		return toLocale(code);
	}
}
