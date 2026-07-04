package com.dodibo.learncore.permission;

import com.dodibo.learncore.permission.dto.PermissionResponse;
import com.dodibo.learncore.role.RoleLevel;
import com.dodibo.learncore.user.User;

import java.util.List;

/*
* This interface represents the service for the permissions in the system.
* The service is responsible for the business logic of the permissions.
* The service is responsible for the retrieval of the permissions.
* The service is responsible for the deletion of the permissions.
* The service is responsible for the update of the permissions.
* The service is responsible for the creation of the permissions.
* */
public interface PermissionService {

    List<PermissionResponse> listAssignablePermissions();

    RoleLevel resolveAssignableLevel(User user);
}
