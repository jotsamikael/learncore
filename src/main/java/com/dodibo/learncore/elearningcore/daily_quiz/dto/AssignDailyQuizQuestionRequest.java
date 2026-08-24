package com.dodibo.learncore.elearningcore.daily_quiz.dto;

import jakarta.validation.constraints.NotBlank;

public record AssignDailyQuizQuestionRequest(
        @NotBlank String dailyQuizUuid,
        @NotBlank String questionUuid,
        Integer displayOrder
) {
}
