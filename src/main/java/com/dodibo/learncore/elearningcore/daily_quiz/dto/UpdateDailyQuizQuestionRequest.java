package com.dodibo.learncore.elearningcore.daily_quiz.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UpdateDailyQuizQuestionRequest(
        @NotNull @Min(1) Integer displayOrder
) {
}
