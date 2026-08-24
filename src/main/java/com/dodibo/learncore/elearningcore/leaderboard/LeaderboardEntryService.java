package com.dodibo.learncore.elearningcore.leaderboard;

import com.dodibo.learncore.elearningcore.leaderboard.dto.CreateLeaderboardEntryRequest;
import com.dodibo.learncore.elearningcore.leaderboard.dto.FindLeaderboardEntryQuery;
import com.dodibo.learncore.elearningcore.leaderboard.dto.LeaderboardEntryResponse;
import org.springframework.data.domain.Page;

public interface LeaderboardEntryService {

    LeaderboardEntryResponse createLeaderboardEntry(CreateLeaderboardEntryRequest request);

    Page<LeaderboardEntryResponse> getLeaderboardEntries(FindLeaderboardEntryQuery query);
}
