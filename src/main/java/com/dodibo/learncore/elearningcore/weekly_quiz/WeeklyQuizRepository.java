package com.dodibo.learncore.elearningcore.weekly_quiz;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface WeeklyQuizRepository extends JpaRepository<WeeklyQuiz, Long>, JpaSpecificationExecutor<WeeklyQuiz> {

    Optional<WeeklyQuiz> findByTenant_IdAndWeekStart(Long tenantId, LocalDate weekStart);

    Optional<WeeklyQuiz> findByUuidAndTenant_Id(String uuid, Long tenantId);

    Optional<WeeklyQuiz> findByTenant_IdAndWeekStartAndUuidNot(Long tenantId, LocalDate weekStart, String uuid);

    Optional<WeeklyQuiz> findByUuidAndTenant_IdAndIsDeletedFalse(String uuid, Long tenantId);
}
