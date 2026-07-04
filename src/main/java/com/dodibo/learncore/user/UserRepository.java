package com.dodibo.learncore.user;

import com.dodibo.learncore.staff.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    Optional<User> findByEmailAndTenant_Id(String email, Long tenantId);

    @Query("SELECT u FROM User u WHERE u.email = :email AND u.tenant IS NULL")
    Optional<User> findByEmailAndTenantIsNull(@Param("email") String email);

    @Query("SELECT s FROM Staff s WHERE s.tenant.id = :tenantId")
    List<Staff> findStaffByTenantId(@Param("tenantId") Long tenantId);

    @Query("SELECT s FROM Staff s WHERE s.tenant IS NULL")
    List<Staff> findPlatformStaff();
}
