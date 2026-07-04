package com.dodibo.learncore.role.dto;

import com.dodibo.learncore.role.RoleLevel;

import java.time.LocalDateTime;
import java.util.List;

public record RoleResponse(
        String uuid,
        String name,
        String description,
        RoleLevel level,
        String tenantUuid,
        List<String> permissionCodes,
        LocalDateTime createdDate,
        LocalDateTime lastModifiedDate
) {
}
