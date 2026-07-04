package com.dodibo.learncore.role.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateRoleRequest(
        @NotNull(message = "100")
        @NotBlank(message = "100")
        String name,

        @NotNull(message = "100")
        @NotBlank(message = "100")
        String description,

        @NotNull(message = "100")
        @NotEmpty(message = "100")
        List<@NotBlank String> permissionCodes
) {
}
