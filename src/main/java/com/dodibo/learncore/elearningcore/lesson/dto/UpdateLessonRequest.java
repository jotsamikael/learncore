package com.dodibo.learncore.elearningcore.lesson.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateLessonRequest(
        @NotBlank String categoryUuid,
        @NotBlank String title,
        @NotBlank String content,
        boolean isPremium
) {
}
