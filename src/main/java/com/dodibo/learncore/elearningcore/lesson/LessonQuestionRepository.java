package com.dodibo.learncore.elearningcore.lesson;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LessonQuestionRepository extends JpaRepository<LessonQuestion, Long>, JpaSpecificationExecutor<LessonQuestion> {

    Optional<LessonQuestion> findByLesson_IdAndQuestion_Id(Long lessonId, Long questionId);

    Optional<LessonQuestion> findByIdAndLesson_Tenant_Id(Long id, Long tenantId);
}
