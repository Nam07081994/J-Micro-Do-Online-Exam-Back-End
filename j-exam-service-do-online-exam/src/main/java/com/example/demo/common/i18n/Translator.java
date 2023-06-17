package com.example.demo.common.i18n;

import java.util.Locale;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;

public class Translator {
	private static ResourceBundleMessageSource messageSource;

	public Translator(@Qualifier("messages") ResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public static String toLocale(String code) {
		Locale locale = LocaleContextHolder.getLocale();

		return messageSource.getMessage(code, null, locale);
	}
}
