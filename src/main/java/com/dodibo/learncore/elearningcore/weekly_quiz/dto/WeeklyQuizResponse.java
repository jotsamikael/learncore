package com.dodibo.learncore.elearningcore.weekly_quiz.dto;

import java.time.LocalDate;

public record WeeklyQuizResponse(
        String uuid,
        LocalDate weekStart
) {
}
