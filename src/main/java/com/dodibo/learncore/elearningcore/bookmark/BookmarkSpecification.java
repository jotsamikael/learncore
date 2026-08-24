package com.dodibo.learncore.elearningcore.bookmark;

import com.dodibo.learncore.elearningcore.bookmark.dto.FindBookmarkQuery;
import org.springframework.data.jpa.domain.Specification;

public final class BookmarkSpecification {

    private BookmarkSpecification() {
    }

    public static Specification<Bookmark> fromQuery(FindBookmarkQuery query, Long tenantId, Long userId) {
        return Specification.where(belongsToTenant(tenantId))
                .and(belongsToUser(userId))
                .and(hasQuestionUuid(query.getQuestionUuid()));
    }

    private static Specification<Bookmark> belongsToTenant(Long tenantId) {
        return (root, cq, cb) -> cb.equal(root.get("tenant").get("id"), tenantId);
    }

    private static Specification<Bookmark> belongsToUser(Long userId) {
        return (root, cq, cb) -> cb.equal(root.get("user").get("id"), userId);
    }

    private static Specification<Bookmark> hasQuestionUuid(String questionUuid) {
        return (root, cq, cb) -> questionUuid == null || questionUuid.isBlank()
                ? cb.conjunction()
                : cb.equal(root.get("question").get("uuid"), questionUuid);
    }
}
