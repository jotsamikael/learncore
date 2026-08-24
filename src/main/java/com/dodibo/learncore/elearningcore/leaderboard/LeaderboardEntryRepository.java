package com.dodibo.learncore.elearningcore.leaderboard;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface LeaderboardEntryRepository extends JpaRepository<LeaderboardEntry, Long>, JpaSpecificationExecutor<LeaderboardEntry> {
}
