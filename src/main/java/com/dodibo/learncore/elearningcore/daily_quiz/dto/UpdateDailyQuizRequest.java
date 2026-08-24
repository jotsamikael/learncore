package com.dodibo.learncore.elearningcore.daily_quiz.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record UpdateDailyQuizRequest(
        @NotNull LocalDate quizDate
) {
}
