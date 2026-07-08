package com.dodibo.learncore.tenant.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateTenantRequest {

    @NotBlank
    @Size(max = 150)
    private String name;

    @Size(max = 50)
    private String examFocus;

    private String logoUrl;

    @Email
    @Size(max = 150)
    private String email;

    @Size(max = 30)
    private String phone;

    @Size(max = 32)
    private String country;

    @Size(max = 500)
    private String description;
}
