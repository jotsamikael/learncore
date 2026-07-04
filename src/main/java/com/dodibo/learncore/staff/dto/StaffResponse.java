package com.dodibo.learncore.staff.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class StaffResponse {

    private String uuid;
    private String firstname;
    private String lastname;
    private String email;
    private String positionName;
    private String tenantUuid;
    private List<String> roles;
    private boolean platformStaff;
    private boolean enabled;
}
