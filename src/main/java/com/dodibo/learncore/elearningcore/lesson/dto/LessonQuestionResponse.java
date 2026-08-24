package com.dodibo.learncore.elearningcore.lesson.dto;

public record LessonQuestionResponse(
        Long id,
        String lessonUuid,
        String questionUuid,
        String questionText,
        Integer displayOrder
) {
}
