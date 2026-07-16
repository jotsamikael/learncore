package com.dodibo.learncore.user.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProfileRequest {

    @Size(max = 100)
    private String firstname;

    @Size(max = 100)
    private String lastname;

    @Size(max = 30)
    private String email;

    @Size(max = 50)
    private String username;

    @Size(max = 20)
    @Pattern(regexp = "^\\+?[0-9\\s\\-\\(\\)]{7,30}$", message = "must be a valid phone number")
    private String phone;
}
