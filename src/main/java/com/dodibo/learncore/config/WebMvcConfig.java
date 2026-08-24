package com.dodibo.learncore.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.addPathPrefix(ApiPaths.BASE, handlerType ->
                handlerType.isAnnotationPresent(RestController.class)
                        && handlerType.getPackageName().startsWith("com.dodibo.learncore"));
    }

    /**
     * Swagger UI / clients often submit an empty string for optional file fields.
     * Treat that as "no file" instead of failing MultipartFile conversion.
     */
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new Converter<String, MultipartFile>() {
            @Override
            public MultipartFile convert(@NonNull String source) {
                return null;
            }
        });
    }
}
