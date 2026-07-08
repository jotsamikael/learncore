package com.dodibo.learncore.tenant;


import com.dodibo.learncore.tenant.dto.TenantResponse;
import org.springframework.stereotype.Service;

@Service
public class TenantMapper {
    public TenantResponse toResponse(Tenant tenant) {
        return TenantResponse.builder()
                .id(tenant.getId())
                .uuid(tenant.getUuid())
                .slug(tenant.getSlug())
                .name(tenant.getName())
                .examFocus(tenant.getExamFocus())
                .logoUrl(tenant.getLogoUrl())
                .email(tenant.getEmail())
                .lastModifiedDate(tenant.getLastModifiedDate())
                .phone(tenant.getPhone())
                .country(tenant.getCountry())
                .active(tenant.isActive())
                .build();
    }
}
