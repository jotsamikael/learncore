package com.dodibo.learncore.elearningcore.weekly_quiz;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WeeklyQuizQuestionRepository extends JpaRepository<WeeklyQuizQuestion, Long>,
        JpaSpecificationExecutor<WeeklyQuizQuestion> {

    Optional<WeeklyQuizQuestion> findByWeeklyQuiz_IdAndQuestion_Id(Long weeklyQuizId, Long questionId);

    Optional<WeeklyQuizQuestion> findByIdAndWeeklyQuiz_Tenant_Id(Long id, Long tenantId);
}
