package com.dodibo.learncore.elearningcore.daily_quiz.dto;

public record DailyQuizQuestionResponse(
        Long id,
        String dailyQuizUuid,
        String questionUuid,
        String questionText,
        Integer displayOrder
) {
}
