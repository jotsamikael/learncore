package com.dodibo.learncore.elearningcore.weekly_quiz.dto;

public record WeeklyQuizQuestionResponse(
        Long id,
        String weeklyQuizUuid,
        String questionUuid,
        String questionText,
        Integer displayOrder
) {
}
