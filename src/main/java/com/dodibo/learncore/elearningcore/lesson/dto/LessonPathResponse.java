package com.dodibo.learncore.elearningcore.lesson.dto;

import java.util.List;

public record LessonPathResponse(
        String categoryUuid,
        String categoryName,
        List<LessonPathItemResponse> lessons
) {
}
