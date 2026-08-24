package com.dodibo.learncore.elearningcore.lesson.dto;

import jakarta.validation.constraints.NotBlank;

public record AssignLessonQuestionRequest(
        @NotBlank String lessonUuid,
        @NotBlank String questionUuid,
        Integer displayOrder
) {
}
