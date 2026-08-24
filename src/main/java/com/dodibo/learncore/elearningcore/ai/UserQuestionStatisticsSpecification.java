package com.dodibo.learncore.elearningcore.ai;

import com.dodibo.learncore.elearningcore.ai.dto.FindQuestionStatisticsQuery;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public final class UserQuestionStatisticsSpecification {

    private UserQuestionStatisticsSpecification() {
    }

    public static Specification<UserQuestionStatistics> fromQuery(
            FindQuestionStatisticsQuery query,
            Long tenantId,
            Long userId
    ) {
        return Specification.where(belongsToTenant(tenantId))
                .and(belongsToUser(userId))
                .and(hasQuestionUuid(query.getQuestionUuid()))
                .and(isDueForReview(query.getDueForReview()));
    }

    private static Specification<UserQuestionStatistics> belongsToTenant(Long tenantId) {
        return (root, cq, cb) -> cb.equal(root.get("tenant").get("id"), tenantId);
    }

    private static Specification<UserQuestionStatistics> belongsToUser(Long userId) {
        return (root, cq, cb) -> cb.equal(root.get("user").get("id"), userId);
    }

    private static Specification<UserQuestionStatistics> hasQuestionUuid(String questionUuid) {
        return (root, cq, cb) -> questionUuid == null || questionUuid.isBlank()
                ? cb.conjunction()
                : cb.equal(root.get("question").get("uuid"), questionUuid);
    }

    private static Specification<UserQuestionStatistics> isDueForReview(Boolean dueForReview) {
        return (root, cq, cb) -> {
            if (dueForReview == null || !dueForReview) {
                return cb.conjunction();
            }
            return cb.or(
                    cb.isNull(root.get("nextReviewAt")),
                    cb.lessThanOrEqualTo(root.get("nextReviewAt"), LocalDateTime.now())
            );
        };
    }
}
