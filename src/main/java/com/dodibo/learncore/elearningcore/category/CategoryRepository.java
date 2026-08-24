package com.dodibo.learncore.elearningcore.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>, JpaSpecificationExecutor<Category> {

    Optional<Category> findBySlugAndTenantId(String slug, Long tenantId);

    Optional<Category> findByUuidAndTenantId(String uuid, Long tenantId);

    Optional<Category> findBySlugAndTenantIdAndUuidNot(String slug, Long tenantId, String uuid);

    Optional<Category> findByUuidAndTenantIdAndIsDeletedFalse(String uuid, Long tenantId);

    List<Category> findByTenantIdAndParent_IdAndIsDeletedFalseOrderByNameAsc(Long tenantId, Long parentId);
}
