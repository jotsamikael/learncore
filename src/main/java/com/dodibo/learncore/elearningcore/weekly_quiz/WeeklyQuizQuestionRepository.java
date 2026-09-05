package com.dodibo.learncore.elearningcore.weekly_quiz;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WeeklyQuizQuestionRepository extends JpaRepository<WeeklyQuizQuestion, Long>,
        JpaSpecificationExecutor<WeeklyQuizQuestion> {

    Optional<WeeklyQuizQuestion> findByWeeklyQuiz_IdAndQuestion_Id(Long weeklyQuizId, Long questionId);

    Optional<WeeklyQuizQuestion> findByIdAndWeeklyQuiz_Tenant_Id(Long id, Long tenantId);

    long countByWeeklyQuiz_Id(Long weeklyQuizId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            UPDATE WeeklyQuizQuestion w
            SET w.displayOrder = w.displayOrder + 1
            WHERE w.weeklyQuiz.id = :weeklyQuizId
              AND w.displayOrder >= :fromOrder
            """)
    void incrementOrdersFrom(@Param("weeklyQuizId") Long weeklyQuizId, @Param("fromOrder") int fromOrder);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            UPDATE WeeklyQuizQuestion w
            SET w.displayOrder = w.displayOrder + 1
            WHERE w.weeklyQuiz.id = :weeklyQuizId
              AND w.displayOrder >= :newOrder
              AND w.displayOrder < :oldOrder
              AND w.id <> :excludeId
            """)
    void shiftOrdersUp(
            @Param("weeklyQuizId") Long weeklyQuizId,
            @Param("newOrder") int newOrder,
            @Param("oldOrder") int oldOrder,
            @Param("excludeId") Long excludeId
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            UPDATE WeeklyQuizQuestion w
            SET w.displayOrder = w.displayOrder - 1
            WHERE w.weeklyQuiz.id = :weeklyQuizId
              AND w.displayOrder > :oldOrder
              AND w.displayOrder <= :newOrder
              AND w.id <> :excludeId
            """)
    void shiftOrdersDown(
            @Param("weeklyQuizId") Long weeklyQuizId,
            @Param("oldOrder") int oldOrder,
            @Param("newOrder") int newOrder,
            @Param("excludeId") Long excludeId
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            UPDATE WeeklyQuizQuestion w
            SET w.displayOrder = w.displayOrder - 1
            WHERE w.weeklyQuiz.id = :weeklyQuizId
              AND w.displayOrder > :deletedOrder
            """)
    void decrementOrdersAbove(@Param("weeklyQuizId") Long weeklyQuizId, @Param("deletedOrder") int deletedOrder);
}
