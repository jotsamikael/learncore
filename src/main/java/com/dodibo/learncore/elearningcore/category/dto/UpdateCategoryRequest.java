package com.dodibo.learncore.elearningcore.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateCategoryRequest(
        @NotNull @NotBlank String name,
        @NotNull @NotBlank String slug,
        @NotNull @NotBlank String description,
        String parentUuid,
        String languageUuid
) {
}
