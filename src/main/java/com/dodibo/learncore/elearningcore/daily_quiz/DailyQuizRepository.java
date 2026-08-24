package com.dodibo.learncore.elearningcore.daily_quiz;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface DailyQuizRepository extends JpaRepository<DailyQuiz, Long>, JpaSpecificationExecutor<DailyQuiz> {

    Optional<DailyQuiz> findByTenant_IdAndQuizDate(Long tenantId, LocalDate quizDate);

    Optional<DailyQuiz> findByUuidAndTenant_Id(String uuid, Long tenantId);

    Optional<DailyQuiz> findByTenant_IdAndQuizDateAndUuidNot(Long tenantId, LocalDate quizDate, String uuid);

    Optional<DailyQuiz> findByUuidAndTenant_IdAndIsDeletedFalse(String uuid, Long tenantId);
}
