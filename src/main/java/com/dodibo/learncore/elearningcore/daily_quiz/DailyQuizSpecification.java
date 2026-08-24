package com.dodibo.learncore.elearningcore.daily_quiz;

import com.dodibo.learncore.elearningcore.daily_quiz.dto.FindDailyQuizQuery;
import org.springframework.data.jpa.domain.Specification;

public final class DailyQuizSpecification {

    private DailyQuizSpecification() {
    }

    public static Specification<DailyQuiz> fromQuery(FindDailyQuizQuery query, Long tenantId) {
        return Specification.where(belongsToTenant(tenantId))
                .and(notDeleted())
                .and(hasQuizDate(query.getQuizDate()));
    }

    private static Specification<DailyQuiz> belongsToTenant(Long tenantId) {
        return (root, cq, cb) -> cb.equal(root.get("tenant").get("id"), tenantId);
    }

    private static Specification<DailyQuiz> notDeleted() {
        return (root, cq, cb) -> cb.isFalse(root.get("isDeleted"));
    }

    private static Specification<DailyQuiz> hasQuizDate(java.time.LocalDate quizDate) {
        return (root, cq, cb) -> quizDate == null
                ? cb.conjunction()
                : cb.equal(root.get("quizDate"), quizDate);
    }
}
