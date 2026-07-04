package com.dodibo.learncore.permission.dto;

import com.dodibo.learncore.role.RoleLevel;

public record PermissionResponse(
        String uuid,
        String code,
        String description,
        RoleLevel level
) {
}
