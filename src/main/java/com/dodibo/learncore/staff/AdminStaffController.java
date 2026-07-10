package com.dodibo.learncore.staff;

import com.dodibo.learncore.staff.dto.*;
import com.dodibo.learncore.tenant.dto.FindTenantsQuery;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/staff")
@RequiredArgsConstructor
@Tag(name = "Tenant Staff")
public class AdminStaffController {

    private final StaffService staffService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@authz.can('admin.staff.read')")
    public Page<StaffResponse> listTenantStaff(@RequestParam(required = false) String tenantUuid, @ModelAttribute FindStaffQuery query) {
        return staffService.listTenantStaff(tenantUuid,query);
    }

    @GetMapping("/{uuid}")
    @PreAuthorize("@authz.can('admin.staff.read')")
    public TenantStaffDetailsResponse getTenantStaffDetails(@PathVariable String uuid) {
        return staffService.getTenantStaffDetails(uuid);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("@authz.can('admin.staff.create')")
    public StaffResponse createTenantStaff(@RequestBody @Valid CreateTenantStaffRequest request) {
        return staffService.createTenantStaff(request);
    }

    @PatchMapping("/{uuid}")
    @PreAuthorize("@authz.can('admin.staff.update')")
    public StaffResponse updateTenantStaff(
            @PathVariable String uuid,
            @RequestBody @Valid UpdateTenantStaffRequest request
    ) {
        return staffService.updateTenantStaff(request, uuid);
    }

    @PatchMapping("/{uuid}/status")
    @PreAuthorize("@authz.can('admin.staff.update')")
    public StaffResponse changeStaffStatus(@PathVariable String uuid) {
        return staffService.changeStaffStatus(uuid);
    }
}
