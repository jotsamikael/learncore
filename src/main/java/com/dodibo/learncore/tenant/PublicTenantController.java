package com.dodibo.learncore.tenant;

import com.dodibo.learncore.tenant.dto.TenantResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tenants/public")
@RequiredArgsConstructor
@Tag(name = "Public Tenants")
public class PublicTenantController {

    private final TenantService tenantService;

    @GetMapping("/{slug}")
    public TenantResponse getTenantBySlug(@PathVariable String slug) {
        return tenantService.getPublicTenantBySlug(slug);
    }
}
