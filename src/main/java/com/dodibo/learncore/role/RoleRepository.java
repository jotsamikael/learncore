package com.dodibo.learncore.role;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long>, JpaSpecificationExecutor<Role> {

    boolean existsByNameAndTenantIsNull(String name);

    boolean existsByNameAndTenant_Id(String name, Long tenantId);

    boolean existsByNameAndTenantIsNullAndUuidNot(String name, String uuid);

    boolean existsByNameAndTenant_IdAndUuidNot(String name, Long tenantId, String uuid);

    Optional<Role> findByNameAndTenantIsNull(String name);

    Optional<Role> findByNameAndTenant_Id(String name, Long tenantId);

    Optional<Role> findByUuid(String uuid);
}
