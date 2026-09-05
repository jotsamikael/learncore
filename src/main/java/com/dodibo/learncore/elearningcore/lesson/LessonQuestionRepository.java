package com.dodibo.learncore.elearningcore.lesson;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LessonQuestionRepository extends JpaRepository<LessonQuestion, Long>, JpaSpecificationExecutor<LessonQuestion> {

    Optional<LessonQuestion> findByLesson_IdAndQuestion_Id(Long lessonId, Long questionId);

    Optional<LessonQuestion> findByIdAndLesson_Tenant_Id(Long id, Long tenantId);

    long countByLesson_Id(Long lessonId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            UPDATE LessonQuestion l
            SET l.displayOrder = l.displayOrder + 1
            WHERE l.lesson.id = :lessonId
              AND l.displayOrder >= :fromOrder
            """)
    void incrementOrdersFrom(@Param("lessonId") Long lessonId, @Param("fromOrder") int fromOrder);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            UPDATE LessonQuestion l
            SET l.displayOrder = l.displayOrder + 1
            WHERE l.lesson.id = :lessonId
              AND l.displayOrder >= :newOrder
              AND l.displayOrder < :oldOrder
              AND l.id <> :excludeId
            """)
    void shiftOrdersUp(
            @Param("lessonId") Long lessonId,
            @Param("newOrder") int newOrder,
            @Param("oldOrder") int oldOrder,
            @Param("excludeId") Long excludeId
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            UPDATE LessonQuestion l
            SET l.displayOrder = l.displayOrder - 1
            WHERE l.lesson.id = :lessonId
              AND l.displayOrder > :oldOrder
              AND l.displayOrder <= :newOrder
              AND l.id <> :excludeId
            """)
    void shiftOrdersDown(
            @Param("lessonId") Long lessonId,
            @Param("oldOrder") int oldOrder,
            @Param("newOrder") int newOrder,
            @Param("excludeId") Long excludeId
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            UPDATE LessonQuestion l
            SET l.displayOrder = l.displayOrder - 1
            WHERE l.lesson.id = :lessonId
              AND l.displayOrder > :deletedOrder
            """)
    void decrementOrdersAbove(@Param("lessonId") Long lessonId, @Param("deletedOrder") int deletedOrder);
}
