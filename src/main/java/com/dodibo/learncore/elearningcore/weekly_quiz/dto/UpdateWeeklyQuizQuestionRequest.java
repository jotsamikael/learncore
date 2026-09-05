package com.dodibo.learncore.elearningcore.weekly_quiz.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UpdateWeeklyQuizQuestionRequest(
        @NotNull @Min(1) Integer displayOrder
) {
}
