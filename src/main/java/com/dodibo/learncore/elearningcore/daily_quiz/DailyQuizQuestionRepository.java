package com.dodibo.learncore.elearningcore.daily_quiz;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DailyQuizQuestionRepository extends JpaRepository<DailyQuizQuestion, Long>,
        JpaSpecificationExecutor<DailyQuizQuestion> {

    Optional<DailyQuizQuestion> findByDailyQuiz_IdAndQuestion_Id(Long dailyQuizId, Long questionId);

    Optional<DailyQuizQuestion> findByIdAndDailyQuiz_Tenant_Id(Long id, Long tenantId);
}
