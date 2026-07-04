package com.dodibo.learncore.permission;

import com.dodibo.learncore.role.RoleLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/*
* This interface represents the repository for the permissions in the system.
* The repository is responsible for the persistence of the permissions.
* The repository is responsible for the retrieval of the permissions.
* The repository is responsible for the deletion of the permissions.
* The repository is responsible for the update of the permissions.
* The repository is responsible for the creation of the permissions.
* */
@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    Optional<Permission> findByCode(String code);

    List<Permission> findByCodeIn(Collection<String> codes);

    List<Permission> findByLevel(RoleLevel level);
}
