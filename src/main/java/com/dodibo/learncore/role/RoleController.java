package com.dodibo.learncore.role;

import org.springdoc.core.annotations.ParameterObject;
import com.dodibo.learncore.role.dto.CreateRoleRequest;
import com.dodibo.learncore.role.dto.FindRolesQuery;
import com.dodibo.learncore.role.dto.RoleResponse;
import com.dodibo.learncore.role.dto.UpdateRoleRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("roles")
@RequiredArgsConstructor
@Tag(name = "Roles")
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    @PreAuthorize("@authz.can('platform.role.read') or @authz.can('admin.role.read')")
    public ResponseEntity<Page<RoleResponse>> getRoles(@ParameterObject FindRolesQuery query) {
        return ResponseEntity.ok(roleService.getRoles(query));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("@authz.can('platform.role.create') or @authz.can('admin.role.create')")
    public ResponseEntity<RoleResponse> createRole(@RequestBody @Valid CreateRoleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(roleService.createRole(request));
    }

    @PatchMapping("/{uuid}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@authz.can('platform.role.create') or @authz.can('admin.role.create')")
    public ResponseEntity<RoleResponse> updateRole(@RequestBody @Valid UpdateRoleRequest request, @PathVariable String uuid) {
        return ResponseEntity.status(HttpStatus.OK).body(roleService.updateRole(request, uuid));
    }

    @DeleteMapping("/{uuid}")
    @PreAuthorize("@authz.can('platform.role.delete') or @authz.can('admin.role.delete')")
    public ResponseEntity<Void> deleteRole(@PathVariable String uuid) {
        roleService.deleteRole(uuid);
        return ResponseEntity.ok().build();
    }
}
