package com.dodibo.learncore.role;

import java.util.List;
import java.util.Optional;

import com.dodibo.learncore.role.dto.CreateRoleRequest;
import com.dodibo.learncore.role.dto.FindRolesQuery;
import com.dodibo.learncore.role.dto.RoleResponse;
import com.dodibo.learncore.role.dto.UpdateRoleRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

/*
 * This interface is used to define the role service.
 * It is used to find a role by name.
 * It is used to find all roles.
 * It is used to create a new role.
 * It is used to update a role.
 * It is used to delete a role.
 */
@Service
public interface RoleService {

    Optional<Role> findByName(String name);

    RoleResponse createRole(CreateRoleRequest createRoleRequest);

    Page<RoleResponse> getRoles(FindRolesQuery query);

     RoleResponse updateRole(UpdateRoleRequest request, String uuid);

    void deleteRole(String uuid);
}
