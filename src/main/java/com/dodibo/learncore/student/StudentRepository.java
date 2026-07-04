package com.dodibo.learncore.student;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long>, JpaSpecificationExecutor<Student> {

    Optional<Student> findByEmailAndTenant_Id(String email, Long tenantId);

    Optional<Student> findByUuid(String uuid);

    boolean existsByUsernameAndTenant_Id(String username, Long tenantId);

    boolean existsByUsernameAndTenant_IdAndIdNot(String username, Long tenantId, Long id);
}
