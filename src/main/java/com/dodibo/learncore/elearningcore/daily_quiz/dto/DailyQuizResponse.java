package com.dodibo.learncore.elearningcore.daily_quiz.dto;

import java.time.LocalDate;

public record DailyQuizResponse(
        String uuid,
        LocalDate quizDate
) {
}
