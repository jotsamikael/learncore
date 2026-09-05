package com.dodibo.learncore.elearningcore.lesson.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record AssignLessonQuestionRequest(
        @NotBlank String lessonUuid,
        @NotBlank String questionUuid,
        @Min(1) Integer displayOrder
) {
}
