package com.dodibo.learncore.auth.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class AuthenticationResponse {

    private String accessToken;
    private String refreshToken;
    private String email;
    private String firstname;
    private String lastname;

    private String userUuid;
    private String tenantUuid;
    private List<String> roles;
}
