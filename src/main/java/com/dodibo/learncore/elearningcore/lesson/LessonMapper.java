package com.dodibo.learncore.elearningcore.lesson;

import com.dodibo.learncore.elearningcore.category.Category;
import com.dodibo.learncore.elearningcore.lesson.dto.CreateLessonRequest;
import com.dodibo.learncore.elearningcore.lesson.dto.LessonResponse;
import com.dodibo.learncore.elearningcore.lesson.dto.UpdateLessonRequest;
import com.dodibo.learncore.tenant.Tenant;
import org.springframework.stereotype.Component;

@Component
public class LessonMapper {

    public LessonResponse toResponse(Lesson lesson) {
        if (lesson == null) {
            return null;
        }
        Category category = lesson.getCategory();
        return new LessonResponse(
                lesson.getUuid(),
                lesson.getTitle(),
                lesson.getContent(),
                Boolean.TRUE.equals(lesson.getIsPremium()),
                category != null ? category.getUuid() : null,
                category != null ? category.getName() : null
        );
    }

    public Lesson toEntity(CreateLessonRequest request, Tenant tenant, Category category) {
        if (request == null) {
            return null;
        }
        return Lesson.builder()
                .tenant(tenant)
                .category(category)
                .title(request.title())
                .content(request.content())
                .isPremium(request.isPremium())
                .build();
    }

    public void applyUpdate(Lesson lesson, UpdateLessonRequest request, Category category) {
        lesson.setCategory(category);
        lesson.setTitle(request.title());
        lesson.setContent(request.content());
        lesson.setIsPremium(request.isPremium());
    }
}
