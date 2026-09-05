package com.dodibo.learncore.elearningcore.question.dto;

import com.dodibo.learncore.elearningcore.question.enums.DifficultyLevel;
import com.dodibo.learncore.elearningcore.question.enums.QuestionType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateQuestionDto(
        @NotNull DifficultyLevel difficultyLevel,
        @NotNull QuestionType questionType,
        @NotBlank String questionText,
        String explanation,
        @NotBlank String categoryUuid,
        @Valid WrittenAnswerConfigRequest writtenAnswerConfig
) {
}
