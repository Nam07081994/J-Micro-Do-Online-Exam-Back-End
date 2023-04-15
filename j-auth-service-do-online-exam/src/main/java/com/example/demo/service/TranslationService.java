package com.example.demo.service;

import org.springframework.stereotype.Service;

import static com.example.demo.config.i18n.Translator.toLocale;

@Service
public class TranslationService {
    public String getTranslation(String code){
        return toLocale(code);
    }
}
