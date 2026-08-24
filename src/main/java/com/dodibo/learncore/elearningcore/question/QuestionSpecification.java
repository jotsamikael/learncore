package com.dodibo.learncore.elearningcore.question;

import com.dodibo.learncore.elearningcore.question.dto.FindQuestionQuery;
import org.springframework.data.jpa.domain.Specification;

public final class QuestionSpecification {

    private QuestionSpecification() {
    }

    public static Specification<Question> fromQuery(FindQuestionQuery query, Long tenantId) {
        return Specification.where(belongsToTenant(tenantId))
                .and(notDeleted())
                .and(hasQuestionText(query.getQuestionText()))
                .and(hasDifficultyLevel(query.getDifficultyLevel()))
                .and(hasQuestionType(query.getQuestionType()))
                .and(hasCategoryUuid(query.getCategoryUuid()));
    }

    private static Specification<Question> belongsToTenant(Long tenantId) {
        return (root, cq, cb) -> cb.equal(root.get("tenantId"), tenantId);
    }

    private static Specification<Question> notDeleted() {
        return (root, cq, cb) -> cb.isFalse(root.get("isDeleted"));
    }

    private static Specification<Question> hasQuestionText(String questionText) {
        return (root, cq, cb) -> questionText == null || questionText.isBlank()
                ? cb.conjunction()
                : cb.like(cb.lower(root.get("questionText")), "%" + questionText.toLowerCase() + "%");
    }

    private static Specification<Question> hasDifficultyLevel(
            com.dodibo.learncore.elearningcore.question.enums.DifficultyLevel difficultyLevel) {
        return (root, cq, cb) -> difficultyLevel == null
                ? cb.conjunction()
                : cb.equal(root.get("difficultyLevel"), difficultyLevel);
    }

    private static Specification<Question> hasQuestionType(
            com.dodibo.learncore.elearningcore.question.enums.QuestionType questionType) {
        return (root, cq, cb) -> questionType == null
                ? cb.conjunction()
                : cb.equal(root.get("questionType"), questionType);
    }

    private static Specification<Question> hasCategoryUuid(String categoryUuid) {
        return (root, cq, cb) -> categoryUuid == null || categoryUuid.isBlank()
                ? cb.conjunction()
                : cb.equal(root.get("category").get("uuid"), categoryUuid);
    }
}
