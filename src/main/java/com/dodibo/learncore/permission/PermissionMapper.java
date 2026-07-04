package com.dodibo.learncore.permission;

import com.dodibo.learncore.permission.dto.PermissionResponse;
import org.springframework.stereotype.Component;

/*
* This class represents the mapper for the permissions in the system.
* The mapper is responsible for mapping the permissions to the responses.
* The mapper is responsible for mapping the requests to the permissions.
* The mapper is responsible for mapping the responses to the permissions.
* The mapper is responsible for mapping the permissions to the roles.
* The mapper is responsible for mapping the permissions to the users.
* The mapper is responsible for mapping the permissions to the tenants.
* The mapper is responsible for mapping the permissions to the resources.
* The mapper is responsible for mapping the permissions to the resource types.
* The mapper is responsible for mapping the permissions to the resource actions.
* */
@Component
public class PermissionMapper {

    public PermissionResponse toResponse(Permission permission) {
        if (permission == null) {
            return null;
        }
        return new PermissionResponse(
                permission.getUuid(),
                permission.getCode(),
                permission.getDescription(),
                permission.getLevel()
        );
    }
}
