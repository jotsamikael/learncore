package com.dodibo.learncore.elearningcore.ai;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface UserQuestionStatisticsRepository extends JpaRepository<UserQuestionStatistics, Long>,
        JpaSpecificationExecutor<UserQuestionStatistics> {
}
