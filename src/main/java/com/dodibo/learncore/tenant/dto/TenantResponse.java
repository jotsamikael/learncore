package com.dodibo.learncore.tenant.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class TenantResponse {
    private Long id;
    private String uuid;
    private String slug;
    private String name;
    private String examFocus;
    private String logoUrl;
    private String email;
    private String phone;
    private String country;
    private String description;
    LocalDateTime lastModifiedDate;
    private boolean active;
}