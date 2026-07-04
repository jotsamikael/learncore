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

    /** @deprecated use {@link #accessToken} */
    private String token;

    private String userUuid;
    private String tenantUuid;
    private List<String> roles;
}
