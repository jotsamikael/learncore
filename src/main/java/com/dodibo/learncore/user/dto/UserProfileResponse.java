package com.dodibo.learncore.user.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class UserProfileResponse {

    private String uuid;
    private String email;
    private String firstname;
    private String lastname;
    private String dateOfBirth;
    private String username;
    private String avatarUrl;
    private String tenantUuid;
    private List<String> roles;
    private LocalDateTime createdDate;
}
