package com.dodibo.learncore.elearningcore.leaderboard.dto;

import com.dodibo.learncore.elearningcore.leaderboard.enums.LeaderboardType;

import java.math.BigDecimal;

public record LeaderboardEntryResponse(
        String uuid,
        LeaderboardType leaderboardType,
        String userUuid,
        String categoryUuid,
        String weeklyQuizUuid,
        BigDecimal score,
        Integer rankPosition
) {
}
