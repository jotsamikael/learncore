package com.dodibo.learncore.elearningcore.question_option.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateQuestionOptionRequest(
        @NotBlank String questionUuid,
        @NotBlank String optionText,
        boolean correct,
        String imageUrl
) {
}
