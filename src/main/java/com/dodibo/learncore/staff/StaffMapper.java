package com.dodibo.learncore.staff;

import com.dodibo.learncore.permission.Permission;
import com.dodibo.learncore.role.Role;
import com.dodibo.learncore.staff.dto.PlatformStaffDetailsResponse;
import com.dodibo.learncore.staff.dto.StaffResponse;
import com.dodibo.learncore.staff.dto.TenantStaffDetailsResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Component
public class StaffMapper {

    public StaffResponse toResponse(Staff staff, String tenantUuid) {
        if (staff == null) {
            return null;
        }

        return StaffResponse.builder()
                .uuid(staff.getUuid())
                .firstname(staff.getFirstname())
                .lastname(staff.getLastname())
                .email(staff.getEmail())
                .positionName(staff.getPositionName())
                .tenantUuid(tenantUuid)
                .roles(toRoleNames(staff))
                .platformStaff(staff.isPlatformStaff())
                .enabled(staff.isEnabled())
                .build();
    }

    public TenantStaffDetailsResponse toDetailsResponse(Staff staff, String tenantUuid) {
        if (staff == null) {
            return null;
        }

        return TenantStaffDetailsResponse.builder()
                .uuid(staff.getUuid())
                .firstname(staff.getFirstname())
                .lastname(staff.getLastname())
                .email(staff.getEmail())
                .positionName(staff.getPositionName())
                .tenantUuid(tenantUuid)
                .roles(toRoleNames(staff))
                .avatarUrl(staff.getAvatarUrl())
                .enabled(staff.isEnabled())
                .accountLocked(staff.isAccountLocked())
                .createdDate(staff.getCreatedDate())
                .build();
    }

    public PlatformStaffDetailsResponse toPlatformDetailsResponse(Staff staff) {
        if (staff == null) {
            return null;
        }

        return PlatformStaffDetailsResponse.builder()
                .uuid(staff.getUuid())
                .firstname(staff.getFirstname())
                .lastname(staff.getLastname())
                .email(staff.getEmail())
                .positionName(staff.getPositionName())
                .roles(toRoleNames(staff))
                .avatarUrl(staff.getAvatarUrl())
                .enabled(staff.isEnabled())
                .accountLocked(staff.isAccountLocked())
                .createdDate(staff.getCreatedDate())
                .permissions(toPermissionCodes(staff))
                .build();
    }

    private List<String> toPermissionCodes(Staff staff) {
        if (staff.getRoles() == null || staff.getRoles().isEmpty()) {
            return List.of();
        }

        Set<String> codes = new LinkedHashSet<>();
        for (Role role : staff.getRoles()) {
            if (role.getPermissions() == null) {
                continue;
            }
            for (Permission permission : role.getPermissions()) {
                codes.add(permission.getCode());
            }
        }
        return new ArrayList<>(codes);
    }

    private List<String> toRoleNames(Staff staff) {
        if (staff.getRoles() == null) {
            return List.of();
        }
        return staff.getRoles().stream().map(Role::getName).toList();
    }
}
