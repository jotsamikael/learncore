package com.dodibo.learncore.staff;

import com.dodibo.learncore.staff.dto.*;

import java.util.List;

public interface StaffService {

    StaffResponse createPlatformStaff(CreatePlatformStaffRequest request);

    StaffResponse createTenantStaff(CreateTenantStaffRequest request);

    List<StaffResponse> listPlatformStaff();

    List<StaffResponse> listTenantStaff(String tenantUuid);

    TenantStaffDetailsResponse getTenantStaffDetails(String tenantStaffUuid);

    StaffResponse updateTenantStaff(UpdateTenantStaffRequest request, String staffUuid);

    StaffResponse changeStaffStatus(String staffUuid);

    PlatformStaffDetailsResponse getPlatformStaffDetails(String uuid);

    StaffResponse updatePlatformStaff(UpdatePlatformStaffRequest request, String staffUuid);

    StaffResponse changePlatformStaffStatus(String staffUuid);
}
