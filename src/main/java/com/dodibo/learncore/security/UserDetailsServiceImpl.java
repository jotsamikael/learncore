package com.dodibo.learncore.security;

import com.dodibo.learncore.common.EmailUtils;
import com.dodibo.learncore.common.tenant.AuthScope;
import com.dodibo.learncore.common.tenant.TenantContext;
import com.dodibo.learncore.user.User;
import com.dodibo.learncore.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/*
 * This class is used to implement the UserDetailsService interface.
 * It is used to load the user by username.
 * It is used to load the user by username
 * */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository repository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        AuthScope scope = TenantContext.getAuthScope();
        String email = EmailUtils.normalize(identifier);

        if (scope == AuthScope.JWT) {
            Long userId = Long.parseLong(identifier);
            return repository.findById(userId)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        }

        if (scope == AuthScope.PLATFORM_STAFF) {
            User user = repository.findByEmailIgnoreCase(email)
                    .filter(User::isPlatformUser)
                    .orElseThrow(() -> new UsernameNotFoundException("Platform staff not found"));
            if (!(user instanceof com.dodibo.learncore.staff.Staff)) {
                throw new UsernameNotFoundException("Not a staff account");
            }
            return user;
        }

        Long tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new UsernameNotFoundException("Tenant context is required");
        }

        return repository.findByEmailIgnoreCase(email)
                .filter(user -> user.getTenantId() != null && user.getTenantId().equals(tenantId))
                .orElseThrow(() -> new UsernameNotFoundException("User not found for tenant"));
    }
}
