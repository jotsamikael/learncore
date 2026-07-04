package com.dodibo.learncore.tenant;

import com.dodibo.learncore.tenant.dto.TenantResponse;
import com.dodibo.learncore.tenant.dto.UpdateTenantRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/admin/tenant")
@RequiredArgsConstructor
@Tag(name = "Tenant Admin Settings")
public class AdminTenantController {

    private final TenantService tenantService;

    @GetMapping
    @PreAuthorize("@authz.can('admin.tenant.read')")
    public TenantResponse getOwnTenant() {
        return tenantService.getOwnTenant();
    }

    @PatchMapping(consumes  = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("@authz.can('admin.tenant.update')")
    public TenantResponse updateOwnTenant(@Valid @ModelAttribute UpdateTenantRequest request,
                                          @RequestPart(value = "logo", required = false) MultipartFile logo) {
        return tenantService.updateOwnTenant(request, logo);
    }
}
