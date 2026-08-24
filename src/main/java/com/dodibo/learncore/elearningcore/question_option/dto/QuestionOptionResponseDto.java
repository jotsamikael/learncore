package com.dodibo.learncore.elearningcore.question_option.dto;

public record QuestionOptionResponseDto(
        String uuid,
        String optionText,
        boolean correct,
        String imageUrl,
        String questionUuid
) {
}
