package com.dodibo.learncore.staff;

import com.dodibo.learncore.staff.dto.*;
import org.springframework.data.domain.Page;

import java.util.List;

public interface StaffService {

    StaffResponse createPlatformStaff(CreatePlatformStaffRequest request);

    StaffResponse createTenantStaff(CreateTenantStaffRequest request);

    Page<StaffResponse> listPlatformStaff(FindStaffQuery query);

    Page<StaffResponse> listTenantStaff(String tenantUuid, FindStaffQuery query);

    TenantStaffDetailsResponse getTenantStaffDetails(String tenantStaffUuid);

    StaffResponse updateTenantStaff(UpdateTenantStaffRequest request, String staffUuid);

    StaffResponse changeStaffStatus(String staffUuid);

    PlatformStaffDetailsResponse getPlatformStaffDetails(String uuid);

    StaffResponse updatePlatformStaff(UpdatePlatformStaffRequest request, String staffUuid);

    StaffResponse changePlatformStaffStatus(String staffUuid);
}
