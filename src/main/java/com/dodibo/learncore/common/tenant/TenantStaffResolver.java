package com.dodibo.learncore.common.tenant;

import com.dodibo.learncore.exception.OperationNotPermittedException;
import com.dodibo.learncore.exception.ResourceNotFoundException;
import com.dodibo.learncore.security.SecurityUtils;
import com.dodibo.learncore.staff.Staff;
import com.dodibo.learncore.tenant.Tenant;
import com.dodibo.learncore.tenant.TenantRepository;
import com.dodibo.learncore.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TenantStaffResolver {

    private final TenantRepository tenantRepository;

    public Tenant requireCallerTenant() {
        User user = SecurityUtils.getCurrentUser();
        if (!(user instanceof Staff staff) || !staff.isTenantStaff()) {
            throw new OperationNotPermittedException("Only tenant staff can access this resource");
        }
        return tenantRepository.findById(staff.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));
    }

    public Long requireCallerTenantId() {
        return requireCallerTenant().getId();
    }
}
