package com.dodibo.learncore.staff;

import com.dodibo.learncore.staff.dto.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/platform/staff")
@RequiredArgsConstructor
@Tag(name = "Platform Staff")
public class PlatformStaffController {

    private final StaffService staffService;

    @GetMapping
    @PreAuthorize("@authz.can('platform.staff.read')")
    public Page<StaffResponse> listPlatformStaff(@ModelAttribute FindStaffQuery query) {
        return staffService.listPlatformStaff(query);
    }

    @GetMapping("/{uuid}")
    @PreAuthorize("@authz.can('platform.staff.read')")
    public PlatformStaffDetailsResponse getPlatformStaffDetails(@PathVariable String uuid) {
        return staffService.getPlatformStaffDetails(uuid);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("@authz.can('platform.staff.create')")
    public StaffResponse createPlatformStaff(@RequestBody @Valid CreatePlatformStaffRequest request) {
        return staffService.createPlatformStaff(request);
    }

    @PostMapping("/tenant-admin")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("@authz.can('platform.tenant-admin.create')")
    public StaffResponse createTenantAdmin(@RequestBody @Valid CreateTenantStaffRequest request) {
        request.setRoles(List.of("TENANT_ADMIN"));
        return staffService.createTenantStaff(request);
    }




    @PatchMapping("/{uuid}")
    @PreAuthorize("@authz.can('platform.staff.update')")
    public StaffResponse updatePlatformStaff(
            @PathVariable String uuid,
            @RequestBody @Valid UpdatePlatformStaffRequest request
    ) {
        return staffService.updatePlatformStaff(request, uuid);
    }

    @PatchMapping("/{uuid}/status")
    @PreAuthorize("@authz.can('platform.staff.update')")
    public StaffResponse changeStaffStatus(@PathVariable String uuid) {
        return staffService.changePlatformStaffStatus(uuid);
    }


}
