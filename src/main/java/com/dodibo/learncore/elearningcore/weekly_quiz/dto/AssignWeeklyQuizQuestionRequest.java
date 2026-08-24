package com.dodibo.learncore.elearningcore.weekly_quiz.dto;

import jakarta.validation.constraints.NotBlank;

public record AssignWeeklyQuizQuestionRequest(
        @NotBlank String weeklyQuizUuid,
        @NotBlank String questionUuid,
        Integer displayOrder
) {
}
