package com.dodibo.learncore.staff.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class TenantStaffDetailsResponse {

    private String uuid;
    private String firstname;
    private String lastname;
    private String email;
    private String positionName;
    private String tenantUuid;
    private List<String> roles;
    private String avatarUrl;
    private boolean enabled;
    private boolean accountLocked;
    private LocalDateTime createdDate;
}
