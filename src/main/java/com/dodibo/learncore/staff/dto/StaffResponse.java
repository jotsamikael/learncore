package com.dodibo.learncore.staff.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class StaffResponse {

    private String uuid;
    private String firstname;
    private String lastname;
    private String email;
    private String positionName;
    private boolean accountLocked;
    private String tenantUuid;
    private LocalDateTime lastModifiedDate;
    private List<String> roles;
    private boolean platformStaff;
    private boolean enabled;
}
