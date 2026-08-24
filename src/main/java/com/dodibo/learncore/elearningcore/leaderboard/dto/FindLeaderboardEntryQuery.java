package com.dodibo.learncore.elearningcore.leaderboard.dto;

import com.dodibo.learncore.common.dto.PaginationQuery;
import com.dodibo.learncore.elearningcore.leaderboard.enums.LeaderboardType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FindLeaderboardEntryQuery extends PaginationQuery {

    private LeaderboardType leaderboardType;
    private String categoryUuid;
    private String weeklyQuizUuid;
    private String userUuid;
}
