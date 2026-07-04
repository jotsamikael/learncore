package com.dodibo.learncore.staff;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {

    @Query("SELECT s FROM Staff s WHERE s.uuid = :uuid")
    Optional<Staff> findByUuid(@Param("uuid") String uuid);

    Optional<Staff> findByEmailIgnoreCase(String email);
}
