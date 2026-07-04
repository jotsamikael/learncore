package com.dodibo.learncore.user;

import com.dodibo.learncore.permission.dto.PermissionResponse;
import com.dodibo.learncore.user.dto.UpdateProfileRequest;
import com.dodibo.learncore.user.dto.UserProfileResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/users/me")
@RequiredArgsConstructor
@Tag(name = "User Profile")
public class UserController {

    private final UserService userService;

    @GetMapping
    public UserProfileResponse getMyProfile() {
        return userService.getMyProfile();
    }

    @PatchMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UserProfileResponse updateMyProfile(
            @Valid @ModelAttribute UpdateProfileRequest request,
            @RequestPart(value = "avatar", required = false) MultipartFile avatar
    ) {
        return userService.updateMyProfile(request, avatar);
    }



    @GetMapping("/permissions")
    public List<PermissionResponse> viewPermissions() {
        return userService.viewPermissions();
    }


}
