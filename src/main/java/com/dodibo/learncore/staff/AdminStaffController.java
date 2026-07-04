package com.dodibo.learncore.staff;

import com.dodibo.learncore.staff.dto.*;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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
    public List<StaffResponse> listTenantStaff(@RequestParam(required = false) String tenantUuid) {
        return staffService.listTenantStaff(tenantUuid);
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
