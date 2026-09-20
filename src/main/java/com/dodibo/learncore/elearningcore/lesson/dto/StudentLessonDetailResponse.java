package com.dodibo.learncore.elearningcore.lesson.dto;

import java.util.List;

public record StudentLessonDetailResponse(
        String uuid,
        String title,
        String content,
        int position,
        int estimatedReadMinutes,
        boolean isPremium,
        LessonPathStatus status,
        List<String> exerciseQuestionUuids
) {
}
