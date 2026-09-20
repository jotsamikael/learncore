package com.dodibo.learncore.elearningcore.lesson.dto;

public record LessonPathItemResponse(
        String uuid,
        String title,
        int position,
        int estimatedReadMinutes,
        boolean isPremium,
        int exerciseCount,
        LessonPathStatus status
) {
}
