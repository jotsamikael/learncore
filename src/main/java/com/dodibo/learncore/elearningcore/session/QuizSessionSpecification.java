package com.dodibo.learncore.elearningcore.session;

import com.dodibo.learncore.elearningcore.session.dto.FindQuizSessionQuery;
import org.springframework.data.jpa.domain.Specification;

public final class QuizSessionSpecification {

    private QuizSessionSpecification() {
    }

    public static Specification<QuizSession> fromQuery(FindQuizSessionQuery query, Long tenantId, Long userId) {
        return Specification.where(belongsToTenant(tenantId))
                .and(belongsToUser(userId))
                .and(hasSessionType(query.getSessionType()))
                .and(hasCompleted(query.getCompleted()));
    }

    private static Specification<QuizSession> belongsToTenant(Long tenantId) {
        return (root, cq, cb) -> cb.equal(root.get("tenant").get("id"), tenantId);
    }

    private static Specification<QuizSession> belongsToUser(Long userId) {
        return (root, cq, cb) -> cb.equal(root.get("user").get("id"), userId);
    }

    private static Specification<QuizSession> hasSessionType(
            com.dodibo.learncore.elearningcore.session.enums.SessionType sessionType) {
        return (root, cq, cb) -> sessionType == null
                ? cb.conjunction()
                : cb.equal(root.get("sessionType"), sessionType);
    }

    private static Specification<QuizSession> hasCompleted(Boolean completed) {
        return (root, cq, cb) -> {
            if (completed == null) {
                return cb.conjunction();
            }
            return completed
                    ? cb.isNotNull(root.get("completedAt"))
                    : cb.isNull(root.get("completedAt"));
        };
    }
}
