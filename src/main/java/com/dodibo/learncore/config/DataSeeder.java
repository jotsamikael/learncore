package com.dodibo.learncore.config;

import com.dodibo.learncore.common.EmailUtils;
import com.dodibo.learncore.permission.Permission;
import com.dodibo.learncore.permission.PermissionCodes;
import com.dodibo.learncore.permission.PermissionRepository;
import com.dodibo.learncore.role.Role;
import com.dodibo.learncore.role.RoleLevel;
import com.dodibo.learncore.role.RoleNames;
import com.dodibo.learncore.role.RoleRepository;
import com.dodibo.learncore.staff.Staff;
import com.dodibo.learncore.staff.StaffRepository;
import com.dodibo.learncore.tenant.Tenant;
import com.dodibo.learncore.tenant.TenantRepository;
import com.dodibo.learncore.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements ApplicationRunner {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final TenantRepository tenantRepository;
    private final StaffRepository staffRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${application.seed.super-admin.email:admin@learncore.com}")
    private String superAdminEmail;

    @Value("${application.seed.super-admin.firstname:Super}")
    private String superAdminFirstname;

    @Value("${application.seed.super-admin.lastname:Admin}")
    private String superAdminLastname;

    @Value("${application.seed.super-admin.password:12345678}")
    private String superAdminPassword;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        seedPermissions();
        seedRoles();
        seedRolePermissions();
        Tenant objectif600 = seedObjectif600Tenant();
        seedSuperAdmin();
        log.info("Data seeding complete. Tenant '{}' uuid={}", objectif600.getSlug(), objectif600.getUuid());
    }

    private void seedPermissions() {
        Map<String, PermissionSeed> permissions = Map.ofEntries(
                Map.entry(PermissionCodes.PLATFORM_STAFF_READ,
                        new PermissionSeed("List platform staff", RoleLevel.PLATFORM)),
                Map.entry(PermissionCodes.PLATFORM_STAFF_CREATE,
                        new PermissionSeed("Create platform staff", RoleLevel.PLATFORM)),
                Map.entry(PermissionCodes.PLATFORM_STAFF_UPDATE,
                        new PermissionSeed("Update platform staff", RoleLevel.PLATFORM)),
                Map.entry(PermissionCodes.PLATFORM_TENANT_ADMIN_CREATE,
                        new PermissionSeed("Create tenant admin from platform", RoleLevel.PLATFORM)),
                Map.entry(PermissionCodes.PLATFORM_STAFF_ASSIGN_SUPER_ADMIN,
                        new PermissionSeed("Assign SUPER_ADMIN role to platform staff", RoleLevel.PLATFORM)),
                Map.entry(PermissionCodes.PLATFORM_ROLE_CREATE,
                        new PermissionSeed("Create platform roles", RoleLevel.PLATFORM)),
                Map.entry(PermissionCodes.PLATFORM_ROLE_READ,
                        new PermissionSeed("List platform roles", RoleLevel.PLATFORM)),
                Map.entry(PermissionCodes.PLATFORM_ROLE_DELETE,
                        new PermissionSeed("Delete platform roles", RoleLevel.PLATFORM)),
                Map.entry(PermissionCodes.ADMIN_STAFF_READ,
                        new PermissionSeed("List tenant staff", RoleLevel.TENANT)),
                Map.entry(PermissionCodes.ADMIN_STAFF_CREATE,
                        new PermissionSeed("Create tenant staff", RoleLevel.TENANT)),
                Map.entry(PermissionCodes.ADMIN_STAFF_UPDATE,
                        new PermissionSeed("Update tenant staff", RoleLevel.TENANT)),
                Map.entry(PermissionCodes.ADMIN_TENANT_READ,
                        new PermissionSeed("View tenant settings", RoleLevel.TENANT)),
                Map.entry(PermissionCodes.ADMIN_TENANT_UPDATE,
                        new PermissionSeed("Update tenant settings", RoleLevel.TENANT)),
                Map.entry(PermissionCodes.ADMIN_ROLE_CREATE,
                        new PermissionSeed("Create tenant roles", RoleLevel.TENANT)),
                Map.entry(PermissionCodes.ADMIN_ROLE_READ,
                        new PermissionSeed("List tenant roles", RoleLevel.TENANT)),
                Map.entry(PermissionCodes.ADMIN_ROLE_DELETE,
                        new PermissionSeed("Delete tenant roles", RoleLevel.TENANT)),
                Map.entry(PermissionCodes.TENANT_CREATE,
                        new PermissionSeed("Create tenants", RoleLevel.PLATFORM)),
                Map.entry(PermissionCodes.TENANT_READ,
                        new PermissionSeed("Read tenants", RoleLevel.PLATFORM)),
                Map.entry(PermissionCodes.TENANT_UPDATE,
                        new PermissionSeed("Update tenants", RoleLevel.PLATFORM)),
                Map.entry(PermissionCodes.TENANT_DELETE,
                        new PermissionSeed("Delete tenants", RoleLevel.PLATFORM)),
                Map.entry(PermissionCodes.TENANT_STUDENT_READ,
                        new PermissionSeed("List tenant students", RoleLevel.TENANT)),
                Map.entry(PermissionCodes.TENANT_STUDENT_UPDATE,
                        new PermissionSeed("Manage tenant students", RoleLevel.TENANT))
        );

        permissions.forEach((code, seed) -> permissionRepository.findByCode(code).map(existing -> {
            if (existing.getLevel() != seed.level()) {
                existing.setLevel(seed.level());
                existing.setDescription(seed.description());
                return permissionRepository.save(existing);
            }
            return existing;
        }).orElseGet(() -> {
            Permission permission = Permission.builder()
                    .code(code)
                    .description(seed.description())
                    .level(seed.level())
                    .createdDate(LocalDateTime.now())
                    .build();
            log.info("Seeding permission: {}", code);
            return permissionRepository.save(permission);
        }));
    }

    private void seedRoles() {
        Map<String, RoleLevel> roles = Map.of(
                RoleNames.SUPER_ADMIN, RoleLevel.PLATFORM,
                RoleNames.PLATFORM_MANAGER, RoleLevel.PLATFORM,
                RoleNames.PLATFORM_ANALYST, RoleLevel.PLATFORM,
                RoleNames.TENANT_ADMIN, RoleLevel.TENANT,
                RoleNames.COURSE_EDITOR, RoleLevel.TENANT,
                RoleNames.QUESTION_EDITOR, RoleLevel.TENANT,
                RoleNames.STUDENT, RoleLevel.TENANT
        );

        roles.forEach((name, level) -> roleRepository.findByNameAndTenantIsNull(name).orElseGet(() -> {
            Role role = Role.builder()
                    .name(name)
                    .description(name.replace('_', ' ').toLowerCase())
                    .level(level)
                    .permissions(new ArrayList<>())
                    .createdDate(LocalDateTime.now())
                    .build();
            log.info("Seeding role: {}", name);
            return roleRepository.save(role);
        }));
    }

    private void seedRolePermissions() {
        assignPermissions(RoleNames.SUPER_ADMIN, PermissionCodes.ALL);
        assignPermissions(RoleNames.PLATFORM_MANAGER, Set.of(
                PermissionCodes.PLATFORM_STAFF_READ,
                PermissionCodes.PLATFORM_STAFF_CREATE,
                PermissionCodes.PLATFORM_STAFF_UPDATE,
                PermissionCodes.PLATFORM_TENANT_ADMIN_CREATE,
                PermissionCodes.PLATFORM_ROLE_CREATE,
                PermissionCodes.PLATFORM_ROLE_READ,
                PermissionCodes.TENANT_CREATE,
                PermissionCodes.TENANT_READ,
                PermissionCodes.TENANT_UPDATE
        ));
        assignPermissions(RoleNames.TENANT_ADMIN, Set.of(
                PermissionCodes.ADMIN_STAFF_READ,
                PermissionCodes.ADMIN_STAFF_CREATE,
                PermissionCodes.ADMIN_STAFF_UPDATE,
                PermissionCodes.ADMIN_TENANT_READ,
                PermissionCodes.ADMIN_TENANT_UPDATE,
                PermissionCodes.ADMIN_ROLE_CREATE,
                PermissionCodes.ADMIN_ROLE_READ,
                PermissionCodes.ADMIN_ROLE_DELETE,
                PermissionCodes.TENANT_STUDENT_READ,
                PermissionCodes.TENANT_STUDENT_UPDATE
        ));
    }

    private record PermissionSeed(String description, RoleLevel level) {
    }

    private void assignPermissions(String roleName, Collection<String> permissionCodes) {
        Role role = roleRepository.findByNameAndTenantIsNull(roleName)
                .orElseThrow(() -> new IllegalStateException("Role not found: " + roleName));
        List<Permission> permissions = permissionRepository.findByCodeIn(permissionCodes);
        role.setPermissions(permissions);
        roleRepository.save(role);
        log.info("Assigned {} permissions to role {}", permissions.size(), roleName);
    }

    private Tenant seedObjectif600Tenant() {
        return tenantRepository.findBySlug("objectif600").orElseGet(() -> {
            Tenant tenant = Tenant.builder()
                    .slug("objectif600")
                    .name("Objectif 600")
                    .examFocus("TAGE_MAGE")
                    .active(true)
                    .createdAt(LocalDateTime.now())
                    .build();
            log.info("Seeding tenant: objectif600");
            return tenantRepository.save(tenant);
        });
    }

    private void seedSuperAdmin() {
        if (userRepository.existsByEmailIgnoreCase(EmailUtils.normalize(superAdminEmail))) {
            return;
        }

        Role superAdminRole = roleRepository.findByNameAndTenantIsNull(RoleNames.SUPER_ADMIN)
                .orElseThrow();

        Staff superAdmin = Staff.builder()
                .firstname(superAdminFirstname)
                .lastname(superAdminLastname)
                .email(EmailUtils.normalize(superAdminEmail))
                .password(passwordEncoder.encode(superAdminPassword))
                .positionName("Platform Owner")
                .roles(List.of(superAdminRole))
                .tenant(null)
                .enabled(true)
                .accountLocked(false)
                .createdDate(LocalDateTime.now())
                .build();

        staffRepository.save(superAdmin);
        log.info("Seeded SUPER_ADMIN: {} (password from application.seed.super-admin.password)", superAdminEmail);
    }
}
