package com.dodibo.learncore.elearningcore.weekly_quiz;

import com.dodibo.learncore.elearningcore.weekly_quiz.dto.FindWeeklyQuizQuery;
import org.springframework.data.jpa.domain.Specification;

public final class WeeklyQuizSpecification {

    private WeeklyQuizSpecification() {
    }

    public static Specification<WeeklyQuiz> fromQuery(FindWeeklyQuizQuery query, Long tenantId) {
        return Specification.where(belongsToTenant(tenantId))
                .and(notDeleted())
                .and(hasWeekStart(query.getWeekStart()));
    }

    private static Specification<WeeklyQuiz> belongsToTenant(Long tenantId) {
        return (root, cq, cb) -> cb.equal(root.get("tenant").get("id"), tenantId);
    }

    private static Specification<WeeklyQuiz> notDeleted() {
        return (root, cq, cb) -> cb.isFalse(root.get("isDeleted"));
    }

    private static Specification<WeeklyQuiz> hasWeekStart(java.time.LocalDate weekStart) {
        return (root, cq, cb) -> weekStart == null
                ? cb.conjunction()
                : cb.equal(root.get("weekStart"), weekStart);
    }
}
