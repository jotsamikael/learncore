package com.dodibo.learncore.elearningcore.question.dto;

import com.dodibo.learncore.elearningcore.question.enums.GradingStrategy;

import java.math.BigDecimal;
import java.util.List;

public record WrittenAnswerConfigResponse(
        String uuid,
        String referenceAnswer,
        BigDecimal maxScore,
        GradingStrategy gradingStrategy,
        Double minimumScoreThreshold,
        List<GradingCriterionResponse> gradingCriteria
) {
}
