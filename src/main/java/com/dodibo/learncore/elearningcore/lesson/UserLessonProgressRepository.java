package com.dodibo.learncore.elearningcore.lesson;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserLessonProgressRepository extends JpaRepository<UserLessonProgress, Long> {

    Optional<UserLessonProgress> findByUser_IdAndLesson_Id(Long userId, Long lessonId);

    List<UserLessonProgress> findByUser_IdAndLesson_IdIn(Long userId, Collection<Long> lessonIds);

    boolean existsByUser_IdAndLesson_Id(Long userId, Long lessonId);

    @Query("""
            SELECT p.lesson.id
            FROM UserLessonProgress p
            WHERE p.user.id = :userId
              AND p.lesson.category.id = :categoryId
            """)
    Set<Long> findCompletedLessonIdsByUserAndCategory(
            @Param("userId") Long userId,
            @Param("categoryId") Long categoryId
    );
}
