package com.dodibo.learncore.elearningcore.lesson;

import com.dodibo.learncore.elearningcore.lesson.dto.FindLessonQuestionQuery;
import org.springframework.data.jpa.domain.Specification;

public final class LessonQuestionSpecification {

    private LessonQuestionSpecification() {
    }

    public static Specification<LessonQuestion> fromQuery(FindLessonQuestionQuery query, Long tenantId) {
        return Specification.where(belongsToTenant(tenantId))
                .and(hasLessonUuid(query.getLessonUuid()))
                .and(hasQuestionUuid(query.getQuestionUuid()));
    }

    private static Specification<LessonQuestion> belongsToTenant(Long tenantId) {
        return (root, cq, cb) -> cb.equal(root.get("lesson").get("tenant").get("id"), tenantId);
    }

    private static Specification<LessonQuestion> hasLessonUuid(String lessonUuid) {
        return (root, cq, cb) -> lessonUuid == null || lessonUuid.isBlank()
                ? cb.conjunction()
                : cb.equal(root.get("lesson").get("uuid"), lessonUuid);
    }

    private static Specification<LessonQuestion> hasQuestionUuid(String questionUuid) {
        return (root, cq, cb) -> questionUuid == null || questionUuid.isBlank()
                ? cb.conjunction()
                : cb.equal(root.get("question").get("uuid"), questionUuid);
    }
}
