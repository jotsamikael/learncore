package com.dodibo.learncore.permission;

import com.dodibo.learncore.permission.dto.PermissionResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/*
* This class represents the controller for the permissions in the system.
* The controller is responsible for handling the requests for the permissions.
* The controller is responsible for returning the permissions to the client.
* The controller is responsible for assigning permissions to roles.
* The controller is responsible for assigning permissions to users.
* The controller is responsible for assigning permissions to tenants.
* The controller is responsible for assigning permissions to resources.
* The controller is responsible for assigning permissions to resource types.
* The controller is responsible for assigning permissions to resource actions.
* */
@RestController
@RequestMapping("permissions")
@RequiredArgsConstructor
@Tag(name = "Permissions")
public class PermissionController {

    private final PermissionService permissionService;

    @GetMapping
    @PreAuthorize("@authz.can('platform.role.create') or @authz.can('admin.role.create')")
    public ResponseEntity<List<PermissionResponse>> listAssignablePermissions() {
        return ResponseEntity.ok(permissionService.listAssignablePermissions());
    }
}
