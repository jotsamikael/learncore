package com.dodibo.learncore.elearningcore.leaderboard;

import com.dodibo.learncore.elearningcore.category.Category;
import com.dodibo.learncore.elearningcore.leaderboard.dto.CreateLeaderboardEntryRequest;
import com.dodibo.learncore.elearningcore.leaderboard.dto.LeaderboardEntryResponse;
import com.dodibo.learncore.elearningcore.weekly_quiz.WeeklyQuiz;
import com.dodibo.learncore.tenant.Tenant;
import com.dodibo.learncore.user.User;
import org.springframework.stereotype.Component;

@Component
public class LeaderboardEntryMapper {

    public LeaderboardEntryResponse toResponse(LeaderboardEntry entry) {
        if (entry == null) {
            return null;
        }
        User user = entry.getUser();
        Category category = entry.getCategory();
        WeeklyQuiz weeklyQuiz = entry.getWeeklyQuiz();
        return new LeaderboardEntryResponse(
                entry.getUuid(),
                entry.getLeaderboardType(),
                user != null ? user.getUuid() : null,
                category != null ? category.getUuid() : null,
                weeklyQuiz != null ? weeklyQuiz.getUuid() : null,
                entry.getScore(),
                entry.getRankPosition()
        );
    }

    public LeaderboardEntry toEntity(
            CreateLeaderboardEntryRequest request,
            Tenant tenant,
            User user,
            Category category,
            WeeklyQuiz weeklyQuiz
    ) {
        if (request == null) {
            return null;
        }
        return LeaderboardEntry.builder()
                .tenant(tenant)
                .user(user)
                .leaderboardType(request.leaderboardType())
                .category(category)
                .weeklyQuiz(weeklyQuiz)
                .score(request.score())
                .rankPosition(request.rankPosition())
                .build();
    }
}
