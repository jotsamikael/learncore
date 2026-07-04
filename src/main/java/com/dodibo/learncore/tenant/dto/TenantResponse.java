package com.dodibo.learncore.tenant.dto;

import lombok.Builder;
import lombok.Getter;

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
    private boolean active;
}