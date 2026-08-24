package com.dodibo.learncore.common.tenant;

import com.dodibo.learncore.exception.OperationNotPermittedException;
import com.dodibo.learncore.exception.ResourceNotFoundException;
import com.dodibo.learncore.security.SecurityUtils;
import com.dodibo.learncore.tenant.Tenant;
import com.dodibo.learncore.tenant.TenantRepository;
import com.dodibo.learncore.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TenantMemberResolver {

    private final TenantRepository tenantRepository;

    public User requireCurrentUser() {
        return SecurityUtils.getCurrentUser();
    }

    public Tenant requireCallerTenant() {
        User user = requireCurrentUser();
        Long tenantId = user.getTenantId();
        if (tenantId == null) {
            throw new OperationNotPermittedException("No tenant associated with this account");
        }
        return tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));
    }
}
