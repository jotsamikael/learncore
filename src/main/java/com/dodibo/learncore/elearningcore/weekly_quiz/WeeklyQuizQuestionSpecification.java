package com.dodibo.learncore.elearningcore.weekly_quiz;

import com.dodibo.learncore.elearningcore.weekly_quiz.dto.FindWeeklyQuizQuestionQuery;
import org.springframework.data.jpa.domain.Specification;

public final class WeeklyQuizQuestionSpecification {

    private WeeklyQuizQuestionSpecification() {
    }

    public static Specification<WeeklyQuizQuestion> fromQuery(FindWeeklyQuizQuestionQuery query, Long tenantId) {
        return Specification.where(belongsToTenant(tenantId))
                .and(hasWeeklyQuizUuid(query.getWeeklyQuizUuid()))
                .and(hasQuestionUuid(query.getQuestionUuid()));
    }

    private static Specification<WeeklyQuizQuestion> belongsToTenant(Long tenantId) {
        return (root, cq, cb) -> cb.equal(root.get("weeklyQuiz").get("tenant").get("id"), tenantId);
    }

    private static Specification<WeeklyQuizQuestion> hasWeeklyQuizUuid(String weeklyQuizUuid) {
        return (root, cq, cb) -> weeklyQuizUuid == null || weeklyQuizUuid.isBlank()
                ? cb.conjunction()
                : cb.equal(root.get("weeklyQuiz").get("uuid"), weeklyQuizUuid);
    }

    private static Specification<WeeklyQuizQuestion> hasQuestionUuid(String questionUuid) {
        return (root, cq, cb) -> questionUuid == null || questionUuid.isBlank()
                ? cb.conjunction()
                : cb.equal(root.get("question").get("uuid"), questionUuid);
    }
}
