package com.dodibo.learncore.role;

import com.dodibo.learncore.exception.OperationNotPermittedException;
import com.dodibo.learncore.exception.ResourceNotFoundException;
import com.dodibo.learncore.permission.Permission;
import com.dodibo.learncore.permission.PermissionRepository;
import com.dodibo.learncore.permission.PermissionService;
import com.dodibo.learncore.role.dto.CreateRoleRequest;
import com.dodibo.learncore.role.dto.FindRolesQuery;
import com.dodibo.learncore.role.dto.RoleResponse;
import com.dodibo.learncore.role.dto.UpdateRoleRequest;
import com.dodibo.learncore.security.SecurityUtils;
import com.dodibo.learncore.staff.Staff;
import com.dodibo.learncore.tenant.Tenant;
import com.dodibo.learncore.tenant.TenantRepository;
import com.dodibo.learncore.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;
    private final PermissionRepository permissionRepository;
    private final PermissionService permissionService;
    private final TenantRepository tenantRepository;

    @Override
    public Optional<Role> findByName(String name) {
        return roleRepository.findByNameAndTenantIsNull(name);
    }

    @Override
    @Transactional
    public RoleResponse createRole(CreateRoleRequest request) {
        RoleLevel roleLevel = permissionService.resolveAssignableLevel(SecurityUtils.getCurrentUser());
        Tenant callerTenant = resolveCallerTenant();

        if (roleLevel == RoleLevel.TENANT) {
            if (callerTenant == null) {
                throw new OperationNotPermittedException("Only tenant admins can create tenant roles");
            }
            assertTenantRoleNameAvailable(request.name(), callerTenant.getId());
        } else {
            assertGlobalRoleNameAvailable(request.name());
        }

        List<Permission> permissions = resolvePermissions(request.permissionCodes(), roleLevel);

        Role role = roleMapper.toEntity(request, roleLevel);
        role.setTenant(roleLevel == RoleLevel.TENANT ? callerTenant : null);
        role.setPermissions(permissions);
        role.setCreatedDate(LocalDateTime.now());

        return roleMapper.toResponse(roleRepository.save(role));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RoleResponse> getRoles(FindRolesQuery query) {
        RoleLevel assignableLevel = permissionService.resolveAssignableLevel(SecurityUtils.getCurrentUser());
        assertRoleLevelQueryAllowed(query.getRoleLevel(), assignableLevel);

        Specification<Role> spec = RoleSpecification.fromQuery(query).and(buildVisibilitySpec(assignableLevel));

        return roleRepository.findAll(spec, query.toPageable()).map(roleMapper::toResponse);
    }

    @Override
    @Transactional
    public RoleResponse updateRole(UpdateRoleRequest request, String uuid) {
        RoleLevel roleLevel = permissionService.resolveAssignableLevel(SecurityUtils.getCurrentUser());
        Tenant callerTenant = resolveCallerTenant();

        Role role = roleRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + uuid));

        assertCanManageRole(role, roleLevel, callerTenant);

        if (!role.getName().equals(request.name())) {
            if (role.getTenant() == null) {
                assertGlobalRoleNameAvailableForUpdate(request.name(), uuid);
            } else {
                assertTenantRoleNameAvailableForUpdate(request.name(), role.getTenant().getId(), uuid);
            }
        }

        List<Permission> permissions = resolvePermissions(request.permissionCodes(), roleLevel);
        role.setName(request.name());
        role.setDescription(request.description());
        role.setPermissions(permissions);

        return roleMapper.toResponse(roleRepository.save(role));
    }

    @Override
    @Transactional
    public void deleteRole(String uuid) {
        RoleLevel roleLevel = permissionService.resolveAssignableLevel(SecurityUtils.getCurrentUser());
        Tenant callerTenant = resolveCallerTenant();

        Role role = roleRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + uuid));

        assertCanManageRole(role, roleLevel, callerTenant);
        role.setDeleted(true);
        roleRepository.save(role);
    }

    private Specification<Role> buildVisibilitySpec(RoleLevel assignableLevel) {
        if (assignableLevel == RoleLevel.PLATFORM) {
            return RoleSpecification.platformRoles();
        }

        Tenant callerTenant = resolveCallerTenant();
        if (callerTenant == null) {
            throw new OperationNotPermittedException("Tenant context is required to list tenant roles");
        }
        return RoleSpecification.visibleToTenant(callerTenant.getId());
    }

    private Tenant resolveCallerTenant() {
        User user = SecurityUtils.getCurrentUser();
        if (user instanceof Staff staff && staff.isTenantStaff()) {
            return tenantRepository.findById(staff.getTenantId())
                    .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));
        }
        return null;
    }

    private void assertCanManageRole(Role role, RoleLevel assignableLevel, Tenant callerTenant) {
        if (role.getLevel() != assignableLevel) {
            throw new OperationNotPermittedException("You cannot update roles outside your scope");
        }

        if (assignableLevel == RoleLevel.PLATFORM) {
            if (role.getTenant() != null) {
                throw new OperationNotPermittedException("You cannot update tenant-owned roles");
            }
            return;
        }

        if (role.getTenant() == null) {
            throw new OperationNotPermittedException("Built-in tenant roles cannot be modified");
        }
        if (callerTenant == null || !role.getTenant().getId().equals(callerTenant.getId())) {
            throw new OperationNotPermittedException("You cannot update roles outside your tenant");
        }
    }

    private void assertGlobalRoleNameAvailable(String name) {
        if (roleRepository.existsByNameAndTenantIsNull(name)) {
            throw new OperationNotPermittedException("A role with this name already exists");
        }
    }

    private void assertTenantRoleNameAvailable(String name, Long tenantId) {
        if (roleRepository.existsByNameAndTenantIsNull(name)) {
            throw new OperationNotPermittedException("A role with this name already exists");
        }
        if (roleRepository.existsByNameAndTenant_Id(name, tenantId)) {
            throw new OperationNotPermittedException("A role with this name already exists for this tenant");
        }
    }

    private void assertGlobalRoleNameAvailableForUpdate(String name, String uuid) {
        if (roleRepository.existsByNameAndTenantIsNullAndUuidNot(name, uuid)) {
            throw new OperationNotPermittedException("A role with this name already exists");
        }
    }

    private void assertTenantRoleNameAvailableForUpdate(String name, Long tenantId, String uuid) {
        if (roleRepository.existsByNameAndTenantIsNull(name)) {
            throw new OperationNotPermittedException("A role with this name already exists");
        }
        if (roleRepository.existsByNameAndTenant_IdAndUuidNot(name, tenantId, uuid)) {
            throw new OperationNotPermittedException("A role with this name already exists for this tenant");
        }
    }

    private void assertRoleLevelQueryAllowed(String requestedRoleLevel, RoleLevel assignableLevel) {
        if (requestedRoleLevel == null || requestedRoleLevel.isBlank()) {
            return;
        }
        try {
            RoleLevel requested = RoleLevel.valueOf(requestedRoleLevel.toUpperCase());
            if (requested != assignableLevel) {
                throw new OperationNotPermittedException("You cannot list roles outside your scope");
            }
        } catch (IllegalArgumentException ex) {
            throw new OperationNotPermittedException("Invalid role level: " + requestedRoleLevel);
        }
    }

    private List<Permission> resolvePermissions(List<String> permissionCodes, RoleLevel roleLevel) {
        Set<String> uniqueCodes = new HashSet<>(permissionCodes);
        if (uniqueCodes.size() != permissionCodes.size()) {
            throw new OperationNotPermittedException("Duplicate permission codes are not allowed");
        }

        List<Permission> permissions = permissionRepository.findByCodeIn(uniqueCodes);
        if (permissions.size() != uniqueCodes.size()) {
            Set<String> foundCodes = new HashSet<>();
            permissions.forEach(permission -> foundCodes.add(permission.getCode()));
            uniqueCodes.removeAll(foundCodes);
            throw new ResourceNotFoundException("Unknown permission codes: " + uniqueCodes);
        }

        List<Permission> invalidLevel = permissions.stream()
                .filter(permission -> permission.getLevel() != roleLevel)
                .toList();
        if (!invalidLevel.isEmpty()) {
            throw new OperationNotPermittedException(
                    "Permissions must match role level " + roleLevel + ": "
                            + invalidLevel.stream().map(Permission::getCode).toList()
            );
        }

        return new ArrayList<>(permissions);
    }
}
