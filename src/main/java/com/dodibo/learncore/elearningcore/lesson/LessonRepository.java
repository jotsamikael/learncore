package com.dodibo.learncore.elearningcore.lesson;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long>, JpaSpecificationExecutor<Lesson> {

    Optional<Lesson> findByUuidAndTenant_Id(String uuid, Long tenantId);

    Optional<Lesson> findByUuidAndTenant_IdAndIsDeletedFalse(String uuid, Long tenantId);

    long countByCategory_IdAndIsDeletedFalse(Long categoryId);

    List<Lesson> findByCategory_IdAndIsDeletedFalseOrderByPositionAsc(Long categoryId);

    Optional<Lesson> findByCategory_IdAndPositionAndIsDeletedFalse(Long categoryId, int position);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            UPDATE Lesson l
            SET l.position = l.position + 1
            WHERE l.category.id = :categoryId
              AND l.isDeleted = false
              AND l.position >= :fromPosition
            """)
    void incrementPositionsFrom(
            @Param("categoryId") Long categoryId,
            @Param("fromPosition") int fromPosition
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            UPDATE Lesson l
            SET l.position = l.position + 1
            WHERE l.category.id = :categoryId
              AND l.isDeleted = false
              AND l.position >= :newPosition
              AND l.position < :oldPosition
              AND l.id <> :excludeId
            """)
    void shiftPositionsUp(
            @Param("categoryId") Long categoryId,
            @Param("newPosition") int newPosition,
            @Param("oldPosition") int oldPosition,
            @Param("excludeId") Long excludeId
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            UPDATE Lesson l
            SET l.position = l.position - 1
            WHERE l.category.id = :categoryId
              AND l.isDeleted = false
              AND l.position > :oldPosition
              AND l.position <= :newPosition
              AND l.id <> :excludeId
            """)
    void shiftPositionsDown(
            @Param("categoryId") Long categoryId,
            @Param("oldPosition") int oldPosition,
            @Param("newPosition") int newPosition,
            @Param("excludeId") Long excludeId
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            UPDATE Lesson l
            SET l.position = l.position - 1
            WHERE l.category.id = :categoryId
              AND l.isDeleted = false
              AND l.position > :deletedPosition
            """)
    void decrementPositionsAbove(
            @Param("categoryId") Long categoryId,
            @Param("deletedPosition") int deletedPosition
    );
}
