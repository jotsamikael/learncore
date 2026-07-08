package com.dodibo.learncore.config;

import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring MVC adds {@link ApiPaths#BASE} via {@link WebMvcConfig}, so springdoc documents
 * full paths like /api/v1/roles. Server URLs in {@link OpenApiConfig} already include that
 * prefix — strip it from operation paths so clients do not double-prefix requests.
 */
@Configuration
public class OpenApiPathCustomizerConfig {

    @Bean
    public OpenApiCustomizer stripApiPathPrefixCustomizer() {
        return openApi -> {
            Paths paths = openApi.getPaths();
            if (paths == null || paths.isEmpty()) {
                return;
            }

            String[] keys = paths.keySet().toArray(String[]::new);
            for (String key : keys) {
                if (!key.startsWith(ApiPaths.BASE)) {
                    continue;
                }
                PathItem pathItem = paths.remove(key);
                String relative = key.substring(ApiPaths.BASE.length());
                if (relative.isEmpty()) {
                    relative = "/";
                }
                paths.addPathItem(relative, pathItem);
            }
        };
    }
}
