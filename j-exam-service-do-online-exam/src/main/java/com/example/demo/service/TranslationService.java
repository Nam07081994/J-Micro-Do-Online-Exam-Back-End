package com.example.demo.service;

import static com.example.demo.common.i18n.Translator.toLocale;

import org.springframework.stereotype.Service;

@Service
public class TranslationService {
	public String getTranslation(String code) {
		return toLocale(code);
	}
}
