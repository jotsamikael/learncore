package com.dodibo.learncore.student;

import com.dodibo.learncore.student.dto.StudentDetailResponse;
import com.dodibo.learncore.student.dto.StudentResponse;
import org.springframework.stereotype.Component;

@Component
public class StudentMapper {

    public StudentResponse toResponse(Student student) {
        if (student == null) {
            return null;
        }

        return StudentResponse.builder()
                .uuid(student.getUuid())
                .username(student.getUsername())
                .email(student.getEmail())
                .firstname(student.getFirstname())
                .lastname(student.getLastname())
                .xp(student.getXp())
                .level(student.getLevel())
                .lastModifiedDate(student.getLastModifiedDate())
                .accountLocked(student.isAccountLocked())
                .enabled(student.isEnabled())
                .build();
    }

    public StudentDetailResponse toDetailResponse(Student student) {
        if (student == null) {
            return null;
        }

        return StudentDetailResponse.builder()
                .uuid(student.getUuid())
                .firstname(student.getFirstname())
                .lastname(student.getLastname())
                .email(student.getEmail())
                .username(student.getUsername())
                .lastModifiedDate(student.getLastModifiedDate())
                .accountLocked(student.isAccountLocked())
                .enabled(student.isEnabled())
                .tenantUuid(student.getTenant() != null ? student.getTenant().getUuid() : null)
                .profilePictureUrl(student.getAvatarUrl())
                .xp(student.getXp())
                .level(student.getLevel())
                .streakDays(student.getStreakDays())
                .referCode(student.getRefer_code())
                .subscriptionType(student.getSubscriptionType() != null
                        ? student.getSubscriptionType().name()
                        : null)
                .enabled(student.isEnabled())
                .createdDate(student.getCreatedDate())
                .build();
    }
}
