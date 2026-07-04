package com.dodibo.learncore.staff.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateTenantStaffRequest {

    @NotBlank
    private String firstname;

    @NotBlank
    private String lastname;

    @NotBlank
    @Email
    private String email;

    @NotEmpty
    private List<String> roles;

    private String positionName;

    /** Required when caller is platform staff; ignored for TENANT_ADMIN (uses own tenant). */
    private String tenantUuid;
}
