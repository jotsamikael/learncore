package com.dodibo.learncore.elearningcore.daily_quiz;

import com.dodibo.learncore.elearningcore.daily_quiz.dto.FindDailyQuizQuestionQuery;
import org.springframework.data.jpa.domain.Specification;

public final class DailyQuizQuestionSpecification {

    private DailyQuizQuestionSpecification() {
    }

    public static Specification<DailyQuizQuestion> fromQuery(FindDailyQuizQuestionQuery query, Long tenantId) {
        return Specification.where(belongsToTenant(tenantId))
                .and(hasDailyQuizUuid(query.getDailyQuizUuid()))
                .and(hasQuestionUuid(query.getQuestionUuid()));
    }

    private static Specification<DailyQuizQuestion> belongsToTenant(Long tenantId) {
        return (root, cq, cb) -> cb.equal(root.get("dailyQuiz").get("tenant").get("id"), tenantId);
    }

    private static Specification<DailyQuizQuestion> hasDailyQuizUuid(String dailyQuizUuid) {
        return (root, cq, cb) -> dailyQuizUuid == null || dailyQuizUuid.isBlank()
                ? cb.conjunction()
                : cb.equal(root.get("dailyQuiz").get("uuid"), dailyQuizUuid);
    }

    private static Specification<DailyQuizQuestion> hasQuestionUuid(String questionUuid) {
        return (root, cq, cb) -> questionUuid == null || questionUuid.isBlank()
                ? cb.conjunction()
                : cb.equal(root.get("question").get("uuid"), questionUuid);
    }
}
