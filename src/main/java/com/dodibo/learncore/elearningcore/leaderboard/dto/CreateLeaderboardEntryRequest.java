package com.dodibo.learncore.elearningcore.leaderboard.dto;

import com.dodibo.learncore.elearningcore.leaderboard.enums.LeaderboardType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateLeaderboardEntryRequest(
        @NotNull
        LeaderboardType leaderboardType,

        @NotBlank
        String userUuid,

        String categoryUuid,

        String weeklyQuizUuid,

        BigDecimal score,

        Integer rankPosition
) {
}
