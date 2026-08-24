package com.dodibo.learncore.elearningcore.lesson.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateLessonRequest(
        @NotBlank
        String categoryUuid,

        @NotBlank
        String title,

        @NotBlank
        String content,

        boolean isPremium
) {
}
