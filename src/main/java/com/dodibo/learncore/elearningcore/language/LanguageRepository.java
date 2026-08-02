package com.dodibo.learncore.elearningcore.language;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface LanguageRepository extends JpaRepository<Language,Long>, JpaSpecificationExecutor<Language> {
    Optional<Language> findByCodeAndTenantId(String code, Long tenantId);
}
