package com.dodibo.learncore.tenant;

import org.springdoc.core.annotations.ParameterObject;
import com.dodibo.learncore.tenant.dto.CreateTenantRequest;
import com.dodibo.learncore.tenant.dto.FindTenantsQuery;
import com.dodibo.learncore.tenant.dto.PlatformUpdateTenantRequest;
import com.dodibo.learncore.tenant.dto.TenantResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/platform/tenants")
@RequiredArgsConstructor
@Tag(name = "Platform Tenants")
public class PlatformTenantController {

    private final TenantMapper tenantMapper;
    private final TenantService tenantService;

    @GetMapping
    @PreAuthorize("@authz.can('tenant.read')")
    public ResponseEntity<Page<TenantResponse>> listTenants(@ParameterObject FindTenantsQuery query) {
        return ResponseEntity.ok(tenantService.getTenants(query));
    }

    @GetMapping("/{uuid}")
    @PreAuthorize("@authz.can('tenant.read')")
    public TenantResponse getTenant(@PathVariable String uuid) {
        return tenantMapper.toResponse(tenantService.getByUuid(uuid));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("@authz.can('tenant.create')")
    public TenantResponse createTenant(
            @Valid @ModelAttribute CreateTenantRequest request,
            @RequestPart(value = "logo", required = false) MultipartFile logo
    ) {
        return tenantService.createTenant(request, logo);
    }

    @PatchMapping(value = "/{uuid}",consumes  = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("@authz.can('tenant.update')")
    public TenantResponse updateTenant(
            @PathVariable String uuid,
            @Valid @ModelAttribute PlatformUpdateTenantRequest request,
            @RequestPart(value = "logo", required = false) MultipartFile logo
    ) {
        return tenantService.updateTenantByPlatform(uuid, request, logo);
    }


    @PatchMapping(value = "/{uuid}/status")
    @PreAuthorize("@authz.can('tenant.update')")
    public TenantResponse changeStatus(
            @PathVariable String uuid
    ) {
        return tenantService.changeStatus(uuid);
    }
}
