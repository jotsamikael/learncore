package com.dodibo.learncore.user;

import com.dodibo.learncore.exception.OperationNotPermittedException;
import com.dodibo.learncore.fileUpload.FilePurpose;
import com.dodibo.learncore.fileUpload.FileUploadService;
import com.dodibo.learncore.permission.Permission;
import com.dodibo.learncore.permission.PermissionMapper;
import com.dodibo.learncore.permission.dto.PermissionResponse;
import com.dodibo.learncore.role.Role;
import com.dodibo.learncore.security.SecurityUtils;
import com.dodibo.learncore.student.Student;
import com.dodibo.learncore.student.StudentRepository;
import com.dodibo.learncore.user.dto.UpdateProfileRequest;
import com.dodibo.learncore.user.dto.UserProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final UserMapper userMapper;
    private final FileUploadService fileUploadService;
    private final PermissionMapper permissionMapper;

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getMyProfile() {
        Long userId = SecurityUtils.getCurrentUser().getId();
        User user = userRepository.findByIdWithTenant(userId)
                .orElseThrow(() -> new OperationNotPermittedException("User not found"));
        return userMapper.toProfileResponse(user);
    }

    @Override
    @Transactional
    public UserProfileResponse updateMyProfile(UpdateProfileRequest request, MultipartFile avatar) {
        Long userId = SecurityUtils.getCurrentUser().getId();
        User user = userRepository.findByIdWithTenant(userId)
                .orElseThrow(() -> new OperationNotPermittedException("User not found"));

        boolean hasTextUpdate = hasProfileTextUpdate(request);
        boolean hasAvatar = avatar != null && !avatar.isEmpty();

        if (!hasTextUpdate && !hasAvatar) {
            throw new OperationNotPermittedException("No profile fields to update");
        }

        if (StringUtils.hasText(request.getFirstname())) {
            user.setFirstname(request.getFirstname().trim());
        }
        if (StringUtils.hasText(request.getLastname())) {
            user.setLastname(request.getLastname().trim());
        }

        if (user instanceof Student student && StringUtils.hasText(request.getUsername())) {
            String username = request.getUsername().trim();
            if (studentRepository.existsByUsernameAndTenant_IdAndIdNot(
                    username, student.getTenantId(), student.getId())) {
                throw new OperationNotPermittedException("Username is already taken");
            }
            student.setUsername(username);
        }

        if (hasAvatar) {
            user.setAvatarUrl(fileUploadService.uploadImage(avatar, FilePurpose.PROFILE_PICTURE).url());
        }

        return userMapper.toProfileResponse(userRepository.save(user));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PermissionResponse> viewPermissions() {
        Long userId = SecurityUtils.getCurrentUser().getId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new OperationNotPermittedException("User not found"));

        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            return List.of();
        }

        Set<String> seenCodes = new LinkedHashSet<>();
        List<PermissionResponse> permissions = new ArrayList<>();
        for (Role role : user.getRoles()) {
            if (role.getPermissions() == null) {
                continue;
            }
            for (Permission permission : role.getPermissions()) {
                if (seenCodes.add(permission.getCode())) {
                    permissions.add(permissionMapper.toResponse(permission));
                }
            }
        }
        return permissions;
    }

    private boolean hasProfileTextUpdate(UpdateProfileRequest request) {
        return StringUtils.hasText(request.getFirstname())
                || StringUtils.hasText(request.getLastname())
                || StringUtils.hasText(request.getUsername());
    }
}
