package com.dodibo.learncore.elearningcore.lesson;

import com.dodibo.learncore.elearningcore.lesson.dto.FindLessonQuery;
import org.springframework.data.jpa.domain.Specification;

public final class LessonSpecification {

    private LessonSpecification() {
    }

    public static Specification<Lesson> fromQuery(FindLessonQuery query, Long tenantId) {
        return Specification.where(belongsToTenant(tenantId))
                .and(notDeleted())
                .and(hasTitle(query.getTitle()))
                .and(hasCategoryUuid(query.getCategoryUuid()))
                .and(hasPremium(query.getIsPremium()));
    }

    private static Specification<Lesson> belongsToTenant(Long tenantId) {
        return (root, cq, cb) -> cb.equal(root.get("tenant").get("id"), tenantId);
    }

    private static Specification<Lesson> notDeleted() {
        return (root, cq, cb) -> cb.isFalse(root.get("isDeleted"));
    }

    private static Specification<Lesson> hasTitle(String title) {
        return (root, cq, cb) -> title == null || title.isBlank()
                ? cb.conjunction()
                : cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
    }

    private static Specification<Lesson> hasCategoryUuid(String categoryUuid) {
        return (root, cq, cb) -> categoryUuid == null || categoryUuid.isBlank()
                ? cb.conjunction()
                : cb.equal(root.get("category").get("uuid"), categoryUuid);
    }

    private static Specification<Lesson> hasPremium(Boolean isPremium) {
        return (root, cq, cb) -> isPremium == null
                ? cb.conjunction()
                : cb.equal(root.get("isPremium"), isPremium);
    }
}
