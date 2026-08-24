package com.dodibo.learncore.elearningcore.ai.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record UserQuestionStatisticsResponse(
        Long id,
        String questionUuid,
        String questionText,
        Integer attempts,
        Integer correctAttempts,
        BigDecimal averageTimeSec,
        BigDecimal masteryScore,
        LocalDateTime nextReviewAt,
        Integer sm2Interval,
        BigDecimal sm2Easiness,
        Integer sm2Repetitions,
        LocalDateTime lastAttemptedAt
) {
}
