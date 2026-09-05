package com.dodibo.learncore.elearningcore.question.dto;

import com.dodibo.learncore.elearningcore.question.enums.DifficultyLevel;
import com.dodibo.learncore.elearningcore.question.enums.QuestionType;

public record GetQuestionResponse(
        String uuid,
        DifficultyLevel difficultyLevel,
        QuestionType questionType,
        String questionText,
        String explanation,
        String imageUrl,
        String categoryUuid,
        String categoryName,
        WrittenAnswerConfigResponse writtenAnswerConfig
) {
}
