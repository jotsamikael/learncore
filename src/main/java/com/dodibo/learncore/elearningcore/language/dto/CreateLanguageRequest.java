package com.dodibo.learncore.elearningcore.language.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record CreateLanguageRequest(
        @NotNull
        @NotEmpty
        String name,

        @NotNull
        @NotEmpty
        String code
) {
}
