package com.dodibo.learncore.role;

import com.dodibo.learncore.role.dto.CreateRoleRequest;
import com.dodibo.learncore.role.dto.RoleResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RoleMapper {

    public RoleResponse toResponse(Role role) {
        if (role == null) {
            return null;
        }
        List<String> permissionCodes = role.getPermissions() == null
                ? List.of()
                : role.getPermissions().stream().map(p -> p.getCode()).toList();

        return new RoleResponse(
                role.getUuid(),
                role.getName(),
                role.getDescription(),
                role.getLevel(),
                role.getTenant() != null ? role.getTenant().getUuid() : null,
                permissionCodes,
                role.getCreatedDate(),
                role.getLastModifiedDate()
        );
    }

    public Role toEntity(CreateRoleRequest request, RoleLevel level) {
        return Role.builder()
                .name(request.name())
                .description(request.description())
                .level(level)
                .build();
    }
}
