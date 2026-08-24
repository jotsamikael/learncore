package com.dodibo.learncore.elearningcore.session.dto;

import com.dodibo.learncore.elearningcore.session.enums.SessionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record QuizSessionResponse(
        String uuid,
        SessionType sessionType,
        String categoryUuid,
        String dailyQuizUuid,
        String weeklyQuizUuid,
        LocalDateTime startedAt,
        LocalDateTime completedAt,
        Integer totalQuestions,
        Integer correctAnswers,
        BigDecimal scorePercentage,
        Integer xpEarned,
        Integer durationSeconds
) {
}
