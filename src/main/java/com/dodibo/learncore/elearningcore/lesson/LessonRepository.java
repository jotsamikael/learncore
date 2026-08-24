package com.dodibo.learncore.elearningcore.lesson;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long>, JpaSpecificationExecutor<Lesson> {

    java.util.Optional<Lesson> findByUuidAndTenant_Id(String uuid, Long tenantId);

    java.util.Optional<Lesson> findByUuidAndTenant_IdAndIsDeletedFalse(String uuid, Long tenantId);
}
