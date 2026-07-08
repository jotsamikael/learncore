package com.dodibo.learncore.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
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
}
