package com.dodibo.learncore.tenant.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateTenantRequest {

    @NotBlank
    @Size(max = 80)
    @Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$", message = "Slug must be lowercase alphanumeric with optional hyphens")
    private String slug;

    @NotBlank
    @Size(max = 150)
    private String name;

    @Size(max = 50)
    private String examFocus;

    @Email
    @Size(max = 150)
    private String email;


    @Size(max = 20)
    @Pattern(regexp = "^\\+?[0-9\\s\\-\\(\\)]{7,30}$", message = "must be a valid phone number")
    private String phone;

    @Size(max = 100)
    private String country;
}
