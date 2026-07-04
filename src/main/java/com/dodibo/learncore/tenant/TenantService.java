package com.dodibo.learncore.tenant;

import com.dodibo.learncore.tenant.dto.CreateTenantRequest;
import com.dodibo.learncore.tenant.dto.PlatformUpdateTenantRequest;
import com.dodibo.learncore.tenant.dto.TenantResponse;
import com.dodibo.learncore.tenant.dto.UpdateTenantRequest;
import org.springframework.web.multipart.MultipartFile;

public interface TenantService {

    Tenant getBySlug(String slug);

    Tenant getById(Long id);

    Tenant getByUuid(String uuid);

    TenantResponse createTenant(CreateTenantRequest request, MultipartFile logo);

    TenantResponse updateTenantByPlatform(String tenantUuid, PlatformUpdateTenantRequest request, MultipartFile logo);

    TenantResponse getOwnTenant();

    TenantResponse updateOwnTenant(UpdateTenantRequest request, MultipartFile logo);

    TenantResponse getPublicTenantBySlug(String slug);

    TenantResponse changeStatus(String uuid);
}
