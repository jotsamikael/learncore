package com.dodibo.learncore.elearningcore.leaderboard;

import com.dodibo.learncore.elearningcore.leaderboard.dto.FindLeaderboardEntryQuery;
import org.springframework.data.jpa.domain.Specification;

public final class LeaderboardEntrySpecification {

    private LeaderboardEntrySpecification() {
    }

    public static Specification<LeaderboardEntry> fromQuery(FindLeaderboardEntryQuery query, Long tenantId) {
        return Specification.where(belongsToTenant(tenantId))
                .and(hasLeaderboardType(query.getLeaderboardType()))
                .and(hasCategoryUuid(query.getCategoryUuid()))
                .and(hasWeeklyQuizUuid(query.getWeeklyQuizUuid()))
                .and(hasUserUuid(query.getUserUuid()));
    }

    private static Specification<LeaderboardEntry> belongsToTenant(Long tenantId) {
        return (root, cq, cb) -> cb.equal(root.get("tenant").get("id"), tenantId);
    }

    private static Specification<LeaderboardEntry> hasLeaderboardType(
            com.dodibo.learncore.elearningcore.leaderboard.enums.LeaderboardType type) {
        return (root, cq, cb) -> type == null
                ? cb.conjunction()
                : cb.equal(root.get("leaderboardType"), type);
    }

    private static Specification<LeaderboardEntry> hasCategoryUuid(String categoryUuid) {
        return (root, cq, cb) -> categoryUuid == null || categoryUuid.isBlank()
                ? cb.conjunction()
                : cb.equal(root.get("category").get("uuid"), categoryUuid);
    }

    private static Specification<LeaderboardEntry> hasWeeklyQuizUuid(String weeklyQuizUuid) {
        return (root, cq, cb) -> weeklyQuizUuid == null || weeklyQuizUuid.isBlank()
                ? cb.conjunction()
                : cb.equal(root.get("weeklyQuiz").get("uuid"), weeklyQuizUuid);
    }

    private static Specification<LeaderboardEntry> hasUserUuid(String userUuid) {
        return (root, cq, cb) -> userUuid == null || userUuid.isBlank()
                ? cb.conjunction()
                : cb.equal(root.get("user").get("uuid"), userUuid);
    }
}
