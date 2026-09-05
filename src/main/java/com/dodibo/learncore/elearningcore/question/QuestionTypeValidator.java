package com.dodibo.learncore.elearningcore.question;

import com.dodibo.learncore.elearningcore.question.dto.GradingCriterionRequest;
import com.dodibo.learncore.elearningcore.question.dto.WrittenAnswerConfigRequest;
import com.dodibo.learncore.elearningcore.question.enums.GradingStrategy;
import com.dodibo.learncore.elearningcore.question.enums.QuestionType;
import com.dodibo.learncore.exception.OperationNotPermittedException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class QuestionTypeValidator {

    public void validateWrittenAnswerConfig(QuestionType questionType, WrittenAnswerConfigRequest config) {
        boolean writtenType = isWrittenType(questionType);

        if (writtenType && config == null) {
            throw new OperationNotPermittedException(
                    "writtenAnswerConfig is required for " + questionType + " questions");
        }
        if (!writtenType && config != null) {
            throw new OperationNotPermittedException(
                    "writtenAnswerConfig is not allowed for " + questionType + " questions");
        }
        if (config != null) {
            validateConfigDetails(questionType, config);
        }
    }

    public boolean isWrittenType(QuestionType questionType) {
        return questionType == QuestionType.STRUCTURAL || questionType == QuestionType.ESSAY;
    }

    public boolean isChoiceType(QuestionType questionType) {
        return questionType == QuestionType.MCQ || questionType == QuestionType.TRUE_OR_FALSE;
    }

    private void validateConfigDetails(QuestionType questionType, WrittenAnswerConfigRequest config) {
        GradingStrategy strategy = config.getGradingStrategy();
        List<GradingCriterionRequest> criteria = config.getGradingCriteria();

        if (strategy == GradingStrategy.RUBRIC) {
            if (criteria == null || criteria.isEmpty()) {
                throw new OperationNotPermittedException(
                        "At least one grading criterion is required when gradingStrategy is RUBRIC");
            }
            validateCriteriaTotalScore(config.getMaxScore(), criteria);
        } else if (strategy == GradingStrategy.KEYWORD_MATCH) {
            if (criteria != null && !criteria.isEmpty()) {
                validateCriteriaTotalScore(config.getMaxScore(), criteria);
            }
        } else if (criteria != null && !criteria.isEmpty()) {
            throw new OperationNotPermittedException(
                    "gradingCriteria are only allowed when gradingStrategy is RUBRIC or KEYWORD_MATCH");
        }

        if (questionType == QuestionType.ESSAY
                && strategy != GradingStrategy.RUBRIC
                && strategy != GradingStrategy.AI) {
            throw new OperationNotPermittedException(
                    "ESSAY questions require gradingStrategy RUBRIC or AI");
        }

        if (questionType == QuestionType.STRUCTURAL
                && strategy != GradingStrategy.EXACT_MATCH
                && strategy != GradingStrategy.KEYWORD_MATCH
                && strategy != GradingStrategy.SEMANTIC_SIMILARITY) {
            throw new OperationNotPermittedException(
                    "STRUCTURAL questions require gradingStrategy EXACT_MATCH, KEYWORD_MATCH, or SEMANTIC_SIMILARITY");
        }
    }

    private void validateCriteriaTotalScore(BigDecimal maxScore, List<GradingCriterionRequest> criteria) {
        BigDecimal total = criteria.stream()
                .map(GradingCriterionRequest::getScore)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (maxScore.subtract(total).abs().compareTo(new BigDecimal("0.01")) > 0) {
            throw new OperationNotPermittedException(
                    "Sum of grading criterion scores must equal maxScore");
        }
    }
}
