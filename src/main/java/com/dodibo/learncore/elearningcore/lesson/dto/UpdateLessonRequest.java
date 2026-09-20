package com.dodibo.learncore.elearningcore.lesson.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateLessonRequest(
        @NotBlank String categoryUuid,
        @NotBlank String title,
        @NotBlank String content,
        @NotNull @Min(1) @Max(180) Integer estimatedReadMinutes,
        @NotNull @Min(1) Integer position,
        boolean isPremium
) {
}
