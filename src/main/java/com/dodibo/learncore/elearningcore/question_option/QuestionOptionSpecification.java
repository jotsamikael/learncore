package com.dodibo.learncore.elearningcore.question_option;

import com.dodibo.learncore.elearningcore.question_option.dto.FindQuestionOptionQuery;
import org.springframework.data.jpa.domain.Specification;

public final class QuestionOptionSpecification {

    private QuestionOptionSpecification() {
    }

    public static Specification<QuestionOption> fromQuery(FindQuestionOptionQuery query, Long tenantId) {
        return Specification.where(belongsToTenant(tenantId))
                .and(notDeleted())
                .and(hasQuestionUuid(query.getQuestionUuid()));
    }

    private static Specification<QuestionOption> belongsToTenant(Long tenantId) {
        return (root, cq, cb) -> cb.equal(root.get("question").get("tenantId"), tenantId);
    }

    private static Specification<QuestionOption> notDeleted() {
        return (root, cq, cb) -> cb.isFalse(root.get("isDeleted"));
    }

    private static Specification<QuestionOption> hasQuestionUuid(String questionUuid) {
        return (root, cq, cb) -> questionUuid == null || questionUuid.isBlank()
                ? cb.conjunction()
                : cb.equal(root.get("question").get("uuid"), questionUuid);
    }
}
