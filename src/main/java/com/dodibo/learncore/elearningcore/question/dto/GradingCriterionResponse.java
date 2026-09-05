package com.dodibo.learncore.elearningcore.question.dto;

import com.dodibo.learncore.elearningcore.question.enums.GradingStrategy;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

public record GradingCriterionResponse(
        String uuid,
        String title,
        String description,
        BigDecimal score,
        Set<String> keywords
) {
}
