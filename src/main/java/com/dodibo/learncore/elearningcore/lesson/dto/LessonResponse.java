package com.dodibo.learncore.elearningcore.lesson.dto;

public record LessonResponse(
        String uuid,
        String title,
        String content,
        boolean isPremium,
        String categoryUuid,
        String categoryName
) {
}
