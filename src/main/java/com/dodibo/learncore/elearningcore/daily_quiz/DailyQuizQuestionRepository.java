package com.dodibo.learncore.elearningcore.daily_quiz;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DailyQuizQuestionRepository extends JpaRepository<DailyQuizQuestion, Long>,
        JpaSpecificationExecutor<DailyQuizQuestion> {

    Optional<DailyQuizQuestion> findByDailyQuiz_IdAndQuestion_Id(Long dailyQuizId, Long questionId);

    Optional<DailyQuizQuestion> findByIdAndDailyQuiz_Tenant_Id(Long id, Long tenantId);

    long countByDailyQuiz_Id(Long dailyQuizId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            UPDATE DailyQuizQuestion d
            SET d.displayOrder = d.displayOrder + 1
            WHERE d.dailyQuiz.id = :dailyQuizId
              AND d.displayOrder >= :fromOrder
            """)
    void incrementOrdersFrom(@Param("dailyQuizId") Long dailyQuizId, @Param("fromOrder") int fromOrder);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            UPDATE DailyQuizQuestion d
            SET d.displayOrder = d.displayOrder + 1
            WHERE d.dailyQuiz.id = :dailyQuizId
              AND d.displayOrder >= :newOrder
              AND d.displayOrder < :oldOrder
              AND d.id <> :excludeId
            """)
    void shiftOrdersUp(
            @Param("dailyQuizId") Long dailyQuizId,
            @Param("newOrder") int newOrder,
            @Param("oldOrder") int oldOrder,
            @Param("excludeId") Long excludeId
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            UPDATE DailyQuizQuestion d
            SET d.displayOrder = d.displayOrder - 1
            WHERE d.dailyQuiz.id = :dailyQuizId
              AND d.displayOrder > :oldOrder
              AND d.displayOrder <= :newOrder
              AND d.id <> :excludeId
            """)
    void shiftOrdersDown(
            @Param("dailyQuizId") Long dailyQuizId,
            @Param("oldOrder") int oldOrder,
            @Param("newOrder") int newOrder,
            @Param("excludeId") Long excludeId
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            UPDATE DailyQuizQuestion d
            SET d.displayOrder = d.displayOrder - 1
            WHERE d.dailyQuiz.id = :dailyQuizId
              AND d.displayOrder > :deletedOrder
            """)
    void decrementOrdersAbove(@Param("dailyQuizId") Long dailyQuizId, @Param("deletedOrder") int deletedOrder);
}
