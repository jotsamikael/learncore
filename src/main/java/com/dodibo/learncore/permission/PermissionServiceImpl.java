package com.dodibo.learncore.permission;

import com.dodibo.learncore.exception.OperationNotPermittedException;
import com.dodibo.learncore.permission.dto.PermissionResponse;
import com.dodibo.learncore.role.RoleLevel;
import com.dodibo.learncore.security.AuthorizationService;
import com.dodibo.learncore.security.SecurityUtils;
import com.dodibo.learncore.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/*
* This class represents the implementation of the permission service.
* The implementation is responsible for the business logic of the permissions.
* The implementation is responsible for the retrieval of the permissions.
* The implementation is responsible for the deletion of the permissions.
* The implementation is responsible for the update of the permissions.
* The implementation is responsible for the creation of the permissions.
* */
@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;
    private final PermissionMapper permissionMapper;
    private final AuthorizationService authorizationService;

    @Override
    @Transactional(readOnly = true)
    public List<PermissionResponse> listAssignablePermissions() {
        RoleLevel level = resolveAssignableLevel(SecurityUtils.getCurrentUser());
        return permissionRepository.findByLevel(level).stream()
                .map(permissionMapper::toResponse)
                .toList();
    }

    @Override
    public RoleLevel resolveAssignableLevel(User user) {
        if (user.isPlatformUser()) {
            if (!authorizationService.can(PermissionCodes.PLATFORM_ROLE_CREATE)) {
                throw new OperationNotPermittedException("You are not allowed to manage platform roles");
            }
            return RoleLevel.PLATFORM;
        }

        if (!authorizationService.can(PermissionCodes.ADMIN_ROLE_CREATE)) {
            throw new OperationNotPermittedException("You are not allowed to manage tenant roles");
        }
        return RoleLevel.TENANT;
    }
}
