package com.example.demo.common.i18n;

import java.util.Locale;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

@Component
public class LocaleConfig {
	@Value("${app.i18n.baseName}")
	private String baseName;

	@Value("${app.i18n.defaultLocale}")
	private String defaultLocale;

	@Bean(name = "messages")
	public ResourceBundleMessageSource messageSource() {
		ResourceBundleMessageSource rs = new ResourceBundleMessageSource();
		rs.setBasename(baseName);
		rs.setDefaultEncoding("UTF-8");
		rs.setUseCodeAsDefaultMessage(true);

		return rs;
	}

	@Bean
	public LocaleResolver localeResolver() {
		AcceptHeaderLocaleResolver acceptHeaderLocaleResolver = new AcceptHeaderLocaleResolver();
		acceptHeaderLocaleResolver.setDefaultLocale(new Locale(defaultLocale));

		return acceptHeaderLocaleResolver;
	}
}
