package com.dodibo.learncore.elearningcore.question_option;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuestionOptionRepository extends JpaRepository<QuestionOption, Long>, JpaSpecificationExecutor<QuestionOption> {

    @Query("SELECT o FROM QuestionOption o JOIN o.question q WHERE o.uuid = :uuid AND q.tenantId = :tenantId AND o.isDeleted = false")
    Optional<QuestionOption> findByUuidAndTenantId(@Param("uuid") String uuid, @Param("tenantId") Long tenantId);
}
