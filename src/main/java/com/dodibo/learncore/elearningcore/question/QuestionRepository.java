package com.dodibo.learncore.elearningcore.question;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long>, JpaSpecificationExecutor<Question> {

    Optional<Question> findByUuidAndTenantId(String uuid, Long tenantId);

    Optional<Question> findByUuidAndTenantIdAndIsDeletedFalse(String uuid, Long tenantId);
}
