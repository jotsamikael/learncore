package com.dodibo.learncore.student.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class StudentDetailResponse {

    private String uuid;
    private String firstname;
    private String lastname;
    private String email;
    private String username;
    private String tenantUuid;
    private String profilePictureUrl;
    private Integer xp;
    private Integer level;
    private Integer streakDays;
    private String referCode;
    private String subscriptionType;
    private boolean enabled;
    private LocalDateTime createdDate;
}
