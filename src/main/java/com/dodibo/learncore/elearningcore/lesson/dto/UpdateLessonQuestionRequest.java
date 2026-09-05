package com.dodibo.learncore.elearningcore.lesson.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UpdateLessonQuestionRequest(
        @NotNull @Min(1) Integer displayOrder
) {
}
