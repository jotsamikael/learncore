package com.dodibo.learncore.elearningcore.weekly_quiz.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record UpdateWeeklyQuizRequest(
        @NotNull LocalDate weekStart
) {
}
