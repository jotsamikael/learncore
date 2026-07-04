package com.dodibo.learncore.staff;

import com.dodibo.learncore.common.EmailUtils;
import com.dodibo.learncore.exception.OperationNotPermittedException;
import com.dodibo.learncore.exception.ResourceNotFoundException;
import com.dodibo.learncore.exception.RoleAssignmentException;
import com.dodibo.learncore.exception.TenantAccessDeniedException;
import com.dodibo.learncore.permission.PermissionCodes;
import com.dodibo.learncore.role.Role;
import com.dodibo.learncore.role.RoleLevel;
import com.dodibo.learncore.role.RoleNames;
import com.dodibo.learncore.role.RoleRepository;
import com.dodibo.learncore.security.AuthorizationService;
import com.dodibo.learncore.security.SecurityUtils;
import com.dodibo.learncore.staff.dto.*;
import com.dodibo.learncore.tenant.Tenant;
import com.dodibo.learncore.tenant.TenantRepository;
import com.dodibo.learncore.user.User;
import com.dodibo.learncore.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class StaffServiceImpl implements StaffService {

    public static final String DEFAULT_STAFF_PASSWORD = "12345678";

    private final StaffRepository staffRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final TenantRepository tenantRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthorizationService authorizationService;
    private final StaffMapper staffMapper;

    @Value("${application.staff.default-password:" + DEFAULT_STAFF_PASSWORD + "}")
    private String defaultStaffPassword;

    @Override
    @Transactional
    public StaffResponse createPlatformStaff(CreatePlatformStaffRequest request) {
        User creator = SecurityUtils.getCurrentUser();
        assertCanManagePlatformStaff(creator);
        String email = EmailUtils.normalize(request.getEmail());

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new OperationNotPermittedException("A user with this email already exists");
        }

        List<Role> roles = resolveRoles(request.getRoles(), true, null);
        validatePlatformRoles(roles);

        Staff staff = Staff.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(email)
                .password(passwordEncoder.encode(defaultStaffPassword))
                .positionName(request.getPositionName())
                .roles(roles)
                .tenant(null)
                .enabled(true)
                .accountLocked(false)
                .createdDate(LocalDateTime.now())
                .build();

        return staffMapper.toResponse(staffRepository.save(staff), null);
    }

    @Override
    @Transactional
    public StaffResponse createTenantStaff(CreateTenantStaffRequest request) {
        User creator = SecurityUtils.getCurrentUser();
        Tenant tenant = resolveTargetTenant(creator, request.getTenantUuid());
        String email = EmailUtils.normalize(request.getEmail());

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new OperationNotPermittedException("A user with this email already exists");
        }

        List<Role> roles = resolveRoles(request.getRoles(), false, tenant.getId());
        validateTenantStaffRoles(creator, roles);

        Staff staff = Staff.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(email)
                .password(passwordEncoder.encode(defaultStaffPassword))
                .positionName(request.getPositionName())
                .roles(roles)
                .tenant(tenant)
                .enabled(true)
                .accountLocked(false)
                .createdDate(LocalDateTime.now())
                .build();

        return staffMapper.toResponse(staffRepository.save(staff), tenant.getUuid());
    }

    @Override
    @Transactional(readOnly = true)
    public List<StaffResponse> listPlatformStaff() {
        return userRepository.findPlatformStaff().stream()
                .map(staff -> staffMapper.toResponse(staff, null))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<StaffResponse> listTenantStaff(String tenantUuid) {
        User creator = SecurityUtils.getCurrentUser();
        Tenant tenant = resolveTargetTenant(creator, tenantUuid);
        return userRepository.findStaffByTenantId(tenant.getId()).stream()
                .map(staff -> staffMapper.toResponse(staff, tenant.getUuid()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TenantStaffDetailsResponse getTenantStaffDetails(String tenantStaffUuid) {
        Staff staff = resolveAccessibleTenantStaff(tenantStaffUuid);
        return staffMapper.toDetailsResponse(staff, resolveTenantUuid(staff));
    }

    @Override
    @Transactional
    public StaffResponse updateTenantStaff(UpdateTenantStaffRequest request, String staffUuid) {
        if (!authorizationService.can(PermissionCodes.ADMIN_STAFF_UPDATE)) {
            throw new OperationNotPermittedException("You are not allowed to update tenant staff");
        }

        if (!hasStaffProfileUpdate(request)) {
            throw new OperationNotPermittedException("No staff fields to update");
        }

        Staff staff = resolveAccessibleTenantStaff(staffUuid);
        User updater = SecurityUtils.getCurrentUser();

        if (StringUtils.hasText(request.getFirstname())) {
            staff.setFirstname(request.getFirstname().trim());
        }
        if (StringUtils.hasText(request.getLastname())) {
            staff.setLastname(request.getLastname().trim());
        }
        if (request.getPositionName() != null) {
            staff.setPositionName(request.getPositionName().trim());
        }
        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            List<Role> roles = resolveRoles(request.getRoles(), false, staff.getTenantId());
            validateTenantStaffRoles(updater, roles);
            staff.setRoles(roles);
        }

        return staffMapper.toResponse(staffRepository.save(staff), resolveTenantUuid(staff));
    }

    @Override
    @Transactional
    public StaffResponse changeStaffStatus(String staffUuid) {
        if (!authorizationService.can(PermissionCodes.ADMIN_STAFF_UPDATE)) {
            throw new OperationNotPermittedException("You are not allowed to update tenant staff status");
        }

        Staff staff = resolveAccessibleTenantStaff(staffUuid);
        staff.setAccountLocked(!staff.isAccountLocked());

        return staffMapper.toResponse(staffRepository.save(staff), resolveTenantUuid(staff));
    }

    @Override
    @Transactional(readOnly = true)
    public PlatformStaffDetailsResponse getPlatformStaffDetails(String uuid) {
        Staff staff = resolveAccessiblePlatformStaff(uuid);
        return staffMapper.toPlatformDetailsResponse(staff);
    }

    @Override
    @Transactional
    public StaffResponse updatePlatformStaff(UpdatePlatformStaffRequest request, String staffUuid) {
        if (!authorizationService.can(PermissionCodes.PLATFORM_STAFF_UPDATE)) {
            throw new OperationNotPermittedException("You are not allowed to update platform staff");
        }

        if (!hasPlatformStaffProfileUpdate(request)) {
            throw new OperationNotPermittedException("No staff fields to update");
        }

        Staff staff = resolveAccessiblePlatformStaff(staffUuid);

        if (StringUtils.hasText(request.getFirstname())) {
            staff.setFirstname(request.getFirstname().trim());
        }
        if (StringUtils.hasText(request.getLastname())) {
            staff.setLastname(request.getLastname().trim());
        }
        if (request.getPositionName() != null) {
            staff.setPositionName(request.getPositionName().trim());
        }
        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            List<Role> roles = resolveRoles(request.getRoles(), true, null);
            validatePlatformRoles(roles);
            staff.setRoles(roles);
        }

        return staffMapper.toResponse(staffRepository.save(staff), null);
    }

    @Override
    @Transactional
    public StaffResponse changePlatformStaffStatus(String staffUuid) {
        if (!authorizationService.can(PermissionCodes.PLATFORM_STAFF_UPDATE)) {
            throw new OperationNotPermittedException("You are not allowed to update platform staff status");
        }

        Staff staff = resolveAccessiblePlatformStaff(staffUuid);
        staff.setAccountLocked(!staff.isAccountLocked());

        return staffMapper.toResponse(staffRepository.save(staff), null);
    }

    private Staff resolveAccessiblePlatformStaff(String staffUuid) {
        Staff staff = staffRepository.findByUuid(staffUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found: " + staffUuid));

        if (!staff.isPlatformStaff()) {
            throw new OperationNotPermittedException("Only platform staff can be managed through this endpoint");
        }

        return staff;
    }

    private boolean hasPlatformStaffProfileUpdate(UpdatePlatformStaffRequest request) {
        return StringUtils.hasText(request.getFirstname())
                || StringUtils.hasText(request.getLastname())
                || request.getPositionName() != null
                || (request.getRoles() != null && !request.getRoles().isEmpty());
    }

    private Staff resolveAccessibleTenantStaff(String staffUuid) {
        Staff staff = staffRepository.findByUuid(staffUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found: " + staffUuid));

        if (!staff.isTenantStaff()) {
            throw new OperationNotPermittedException("Only tenant staff can be managed through this endpoint");
        }

        if (!canAccessStaff(staff)) {
            throw new TenantAccessDeniedException("Cannot access staff outside your tenant");
        }

        return staff;
    }

    private boolean canAccessStaff(Staff staff) {
        User user = SecurityUtils.getCurrentUser();

        if (user instanceof Staff currentStaff && currentStaff.isTenantStaff()) {
            return staff.getTenantId() != null && staff.getTenantId().equals(currentStaff.getTenantId());
        }

        return user.isPlatformUser();
    }

    private String resolveTenantUuid(Staff staff) {
        if (staff.getTenantId() == null) {
            return null;
        }
        return tenantRepository.findById(staff.getTenantId())
                .map(Tenant::getUuid)
                .orElse(null);
    }

    private boolean hasStaffProfileUpdate(UpdateTenantStaffRequest request) {
        return StringUtils.hasText(request.getFirstname())
                || StringUtils.hasText(request.getLastname())
                || request.getPositionName() != null
                || (request.getRoles() != null && !request.getRoles().isEmpty());
    }

    private Tenant resolveTargetTenant(User creator, String requestedTenantUuid) {
        if (creator instanceof Staff staff && staff.isTenantStaff()) {
            Tenant tenant = tenantRepository.findById(staff.getTenantId())
                    .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));
            if (requestedTenantUuid != null && !requestedTenantUuid.equals(tenant.getUuid())) {
                throw new TenantAccessDeniedException("Cannot manage staff outside your tenant");
            }
            return tenant;
        }

        if (requestedTenantUuid == null || requestedTenantUuid.isBlank()) {
            throw new OperationNotPermittedException("tenantUuid is required for platform staff");
        }

        return tenantRepository.findByUuid(requestedTenantUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found: " + requestedTenantUuid));
    }

    private void assertCanManagePlatformStaff(User creator) {
        if (!authorizationService.can(PermissionCodes.PLATFORM_STAFF_CREATE)) {
            throw new OperationNotPermittedException("You are not allowed to create platform staff");
        }
    }

    private void validateTenantStaffRoles(User creator, List<Role> roles) {
        for (Role role : roles) {
            if (role.getLevel() == RoleLevel.PLATFORM) {
                throw new RoleAssignmentException("Cannot assign platform role '" + role.getName() + "' to tenant staff");
            }
            if (RoleNames.STUDENT.equals(role.getName())) {
                throw new RoleAssignmentException("Cannot assign STUDENT role to tenant staff");
            }
        }

        boolean creatorIsTenantAdmin = hasAnyRole(creator, RoleNames.TENANT_ADMIN);
        boolean creatorIsPlatform = hasAnyRole(creator, RoleNames.SUPER_ADMIN, RoleNames.PLATFORM_MANAGER);

        if (!creatorIsTenantAdmin && !creatorIsPlatform) {
            throw new OperationNotPermittedException("You are not allowed to manage tenant staff");
        }

        if (creatorIsTenantAdmin) {
            for (Role role : roles) {
                if (RoleNames.TENANT_ADMIN.equals(role.getName())) {
                    throw new OperationNotPermittedException("TENANT_ADMIN cannot assign TENANT_ADMIN role");
                }
            }
        }
    }

    private void validatePlatformRoles(List<Role> roles) {
        for (Role role : roles) {
            if (!isAssignablePlatformRole(role)) {
                throw new RoleAssignmentException("Cannot assign tenant role '" + role.getName() + "' to platform staff");
            }
            if (RoleNames.SUPER_ADMIN.equals(role.getName())
                    && !authorizationService.can(PermissionCodes.PLATFORM_STAFF_ASSIGN_SUPER_ADMIN)) {
                throw new OperationNotPermittedException("You are not allowed to assign SUPER_ADMIN role");
            }
        }
    }

    private boolean isAssignablePlatformRole(Role role) {
        return role.getLevel() == RoleLevel.PLATFORM && role.getTenant() == null;
    }

    private List<Role> resolveRoles(List<String> roleNames, boolean platform, Long tenantId) {
        List<Role> roles = new ArrayList<>();
        for (String roleName : roleNames) {
            if (platform) {
                Role role = roleRepository.findByNameAndTenantIsNull(roleName)
                        .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleName));
                if (!isAssignablePlatformRole(role)) {
                    throw new RoleAssignmentException("Invalid platform role: " + roleName);
                }
                roles.add(role);
                continue;
            }

            roles.add(resolveTenantStaffRole(roleName, tenantId));
        }
        return roles;
    }

    private Role resolveTenantStaffRole(String roleName, Long tenantId) {
        Optional<Role> tenantRole = roleRepository.findByNameAndTenant_Id(roleName, tenantId);
        if (tenantRole.isPresent()) {
            Role role = tenantRole.get();
            if (role.getLevel() != RoleLevel.TENANT) {
                throw new RoleAssignmentException("Invalid tenant staff role: " + roleName);
            }
            return role;
        }

        Role globalRole = roleRepository.findByNameAndTenantIsNull(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleName));
        if (globalRole.getLevel() != RoleLevel.TENANT || RoleNames.STUDENT.equals(globalRole.getName())) {
            throw new RoleAssignmentException("Invalid tenant staff role: " + roleName);
        }
        return globalRole;
    }

    private boolean hasAnyRole(User user, String... roleNames) {
        if (user.getRoles() == null) {
            return false;
        }
        Set<String> names = Set.of(roleNames);
        return user.getRoles().stream().anyMatch(role -> names.contains(role.getName()));
    }
}
