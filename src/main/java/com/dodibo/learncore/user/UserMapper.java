package com.dodibo.learncore.user;

import com.dodibo.learncore.role.Role;
import com.dodibo.learncore.student.Student;
import com.dodibo.learncore.user.dto.UserProfileResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserMapper {

    public UserProfileResponse toProfileResponse(User user) {
        if (user == null) {
            return null;
        }

        List<String> roleNames = user.getRoles() == null
                ? List.of()
                : user.getRoles().stream().map(Role::getName).toList();

        UserProfileResponse.UserProfileResponseBuilder builder = UserProfileResponse.builder()
                .uuid(user.getUuid())
                .email(user.getEmail())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .dateOfBirth(user.getDateOfBirth())
                .avatarUrl(user.getAvatarUrl())
                .tenantUuid(user.getTenant() != null ? user.getTenant().getUuid() : null)
                .roles(roleNames)
                .createdDate(user.getCreatedDate());

        if (user instanceof Student student) {
            builder.username(student.getUsername());
        }

        return builder.build();
    }
}
