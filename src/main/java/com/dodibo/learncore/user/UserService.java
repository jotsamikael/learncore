package com.dodibo.learncore.user;

import com.dodibo.learncore.permission.dto.PermissionResponse;
import com.dodibo.learncore.user.dto.UpdateProfileRequest;
import com.dodibo.learncore.user.dto.UserProfileResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {

    UserProfileResponse getMyProfile();

    UserProfileResponse updateMyProfile(UpdateProfileRequest request, MultipartFile avatar);

    List<PermissionResponse> viewPermissions();
}
