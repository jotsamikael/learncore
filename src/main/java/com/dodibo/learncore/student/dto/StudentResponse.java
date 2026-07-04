package com.dodibo.learncore.student.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class StudentResponse {

    private String uuid;
    private String username;
    private String email;
    private String firstname;
    private String lastname;
    private Integer xp;
    private Integer level;
    private String tenantUuid;
}
