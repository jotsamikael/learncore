package com.dodibo.learncore.permission;

import com.dodibo.learncore.role.RoleLevel;

import java.util.Set;

/*
* This class represents the codes for the permissions in the system.
* The codes are used to identify the permissions in the system.
* The codes are used to check if a user has a permission.
* The codes are used to assign permissions to roles.
* The codes are used to assign permissions to users.
* The codes are used to assign permissions to tenants.
* The codes are used to assign permissions to resources.
* The codes are used to assign permissions to resource types.
* The codes are used to assign permissions to resource actions.
* */
public final class PermissionCodes {

    public static final String PLATFORM_STAFF_READ = "platform.staff.read"; //The PLATFORM_STAFF_READ is a constant that represents the permission to read the platform staff.
    public static final String PLATFORM_STAFF_CREATE = "platform.staff.create"; //The PLATFORM_STAFF_CREATE is a constant that represents the permission to create the platform staff.
    public static final String PLATFORM_STAFF_UPDATE = "platform.staff.update";
    public static final String PLATFORM_TENANT_ADMIN_CREATE = "platform.tenant-admin.create"; //The PLATFORM_TENANT_ADMIN_CREATE is a constant that represents the permission to create the tenant admin.
    public static final String PLATFORM_STAFF_ASSIGN_SUPER_ADMIN = "platform.staff.assign-super-admin"; //The PLATFORM_STAFF_ASSIGN_SUPER_ADMIN is a constant that represents the permission to assign the super admin to the staff.
    public static final String PLATFORM_ROLE_CREATE = "platform.role.create"; //The PLATFORM_ROLE_CREATE is a constant that represents the permission to create the role.
    public static final String PLATFORM_ROLE_READ = "platform.role.read";
    public static final String PLATFORM_ROLE_DELETE = "platform.role.delete";

    public static final String ADMIN_STAFF_READ = "admin.staff.read"; //The ADMIN_STAFF_READ is a constant that represents the permission to read the admin staff.
    public static final String ADMIN_STAFF_CREATE = "admin.staff.create"; //The ADMIN_STAFF_CREATE is a constant that represents the permission to create the admin staff.
    public static final String ADMIN_STAFF_UPDATE = "admin.staff.update";
    public static final String ADMIN_TENANT_READ = "admin.tenant.read"; //The ADMIN_TENANT_READ is a constant that represents the permission to read the admin tenant.
    public static final String ADMIN_TENANT_UPDATE = "admin.tenant.update"; //The ADMIN_TENANT_UPDATE is a constant that represents the permission to update the admin tenant.
    public static final String ADMIN_ROLE_CREATE = "admin.role.create"; //The ADMIN_ROLE_CREATE is a constant that represents the permission to create the admin role.
    public static final String ADMIN_ROLE_READ = "admin.role.read";
    public static final String ADMIN_ROLE_DELETE = "admin.role.delete";

    public static final String TENANT_CREATE = "tenant.create"; //
    public static final String TENANT_READ = "tenant.read"; //
    public static final String TENANT_UPDATE = "tenant.update"; //
    public static final String TENANT_DELETE = "tenant.delete"; //

    //student permissions
    public static final String TENANT_STUDENT_READ = "tenant.student.read";
    public static final String TENANT_STUDENT_UPDATE= "tenant.student.update";

    //language perimissions
    public static final String TENANT_LANGUAGE_READ = "tenant.language.read";
    public static final String TENANT_LANGUAGE_CREATE = "tenant.language.create";
    public static final String TENANT_LANGUAGE_UPDATE = "tenant.language.update";
    public static final String TENANT_LANGUAGE_DELETE = "tenant.language.delete";

    //category permissions
    public static final String TENANT_CATEGORY_READ = "tenant.category.read";
    public static final String TENANT_CATEGORY_CREATE = "tenant.category.create";
    public static final String TENANT_CATEGORY_UPDATE = "tenant.category.update";
    public static final String TENANT_CATEGORY_DELETE = "tenant.category.delete";

    //question permissions
    public static final String TENANT_QUESTION_READ = "tenant.question.read";
    public static final String TENANT_QUESTION_CREATE = "tenant.question.create";
    public static final String TENANT_QUESTION_UPDATE = "tenant.question.update";
    public static final String TENANT_QUESTION_DELETE = "tenant.question.delete";

    //lesson permissions
    public static final String TENANT_LESSON_READ = "tenant.lesson.read";
    public static final String TENANT_LESSON_CREATE = "tenant.lesson.create";
    public static final String TENANT_LESSON_UPDATE = "tenant.lesson.update";
    public static final String TENANT_LESSON_DELETE = "tenant.lesson.delete";

    //daily quiz permissions
    public static final String TENANT_DAILY_QUIZ_READ = "tenant.daily-quiz.read";
    public static final String TENANT_DAILY_QUIZ_CREATE = "tenant.daily-quiz.create";
    public static final String TENANT_DAILY_QUIZ_UPDATE = "tenant.daily-quiz.update";
    public static final String TENANT_DAILY_QUIZ_DELETE = "tenant.daily-quiz.delete";

    //weekly quiz permissions
    public static final String TENANT_WEEKLY_QUIZ_READ = "tenant.weekly-quiz.read";
    public static final String TENANT_WEEKLY_QUIZ_CREATE = "tenant.weekly-quiz.create";
    public static final String TENANT_WEEKLY_QUIZ_UPDATE = "tenant.weekly-quiz.update";
    public static final String TENANT_WEEKLY_QUIZ_DELETE = "tenant.weekly-quiz.delete";

    //leaderboard permissions
    public static final String TENANT_LEADERBOARD_READ = "tenant.leaderboard.read";
    public static final String TENANT_LEADERBOARD_CREATE = "tenant.leaderboard.create";

    //bookmark permissions
    public static final String TENANT_BOOKMARK_READ = "tenant.bookmark.read";
    public static final String TENANT_BOOKMARK_CREATE = "tenant.bookmark.create";
    public static final String TENANT_BOOKMARK_DELETE = "tenant.bookmark.delete";

    //quiz session permissions
    public static final String TENANT_SESSION_READ = "tenant.session.read";
    public static final String TENANT_SESSION_CREATE = "tenant.session.create";

    //question statistics permissions
    public static final String TENANT_QUESTION_STATISTICS_READ = "tenant.question-statistics.read";

    public static final Set<String> PLATFORM = Set.of( //The PLATFORM is a constant that represents the set of platform permissions.
            PLATFORM_STAFF_READ,
            PLATFORM_STAFF_CREATE,
            PLATFORM_STAFF_UPDATE,
            PLATFORM_TENANT_ADMIN_CREATE,
            PLATFORM_STAFF_ASSIGN_SUPER_ADMIN,
            PLATFORM_ROLE_CREATE,
            PLATFORM_ROLE_READ,
            PLATFORM_ROLE_DELETE
    );

    public static final Set<String> TENANT = Set.of( //The TENANT is a constant that represents the set of tenant permissions.
            ADMIN_STAFF_READ,
            ADMIN_STAFF_CREATE,
            ADMIN_STAFF_UPDATE,
            ADMIN_TENANT_READ,
            ADMIN_TENANT_UPDATE,
            ADMIN_ROLE_CREATE,
            ADMIN_ROLE_READ,
            ADMIN_ROLE_DELETE,
            TENANT_STUDENT_READ,
            TENANT_STUDENT_UPDATE,
            TENANT_LANGUAGE_READ,
            TENANT_LANGUAGE_CREATE,
            TENANT_LANGUAGE_UPDATE,
            TENANT_LANGUAGE_DELETE,
            TENANT_CATEGORY_READ,
            TENANT_CATEGORY_CREATE,
            TENANT_CATEGORY_UPDATE,
            TENANT_CATEGORY_DELETE,
            TENANT_QUESTION_READ,
            TENANT_QUESTION_CREATE,
            TENANT_QUESTION_UPDATE,
            TENANT_QUESTION_DELETE,
            TENANT_LESSON_READ,
            TENANT_LESSON_CREATE,
            TENANT_LESSON_UPDATE,
            TENANT_LESSON_DELETE,
            TENANT_DAILY_QUIZ_READ,
            TENANT_DAILY_QUIZ_CREATE,
            TENANT_DAILY_QUIZ_UPDATE,
            TENANT_DAILY_QUIZ_DELETE,
            TENANT_WEEKLY_QUIZ_READ,
            TENANT_WEEKLY_QUIZ_CREATE,
            TENANT_WEEKLY_QUIZ_UPDATE,
            TENANT_WEEKLY_QUIZ_DELETE,
            TENANT_LEADERBOARD_READ,
            TENANT_LEADERBOARD_CREATE,
            TENANT_BOOKMARK_READ,
            TENANT_BOOKMARK_CREATE,
            TENANT_BOOKMARK_DELETE,
            TENANT_SESSION_READ,
            TENANT_SESSION_CREATE,
            TENANT_QUESTION_STATISTICS_READ
    );

    public static final Set<String> ALL = Set.of( //The ALL is a constant that represents the set of all permissions.
            PLATFORM_STAFF_READ,
            PLATFORM_STAFF_CREATE,
            PLATFORM_STAFF_UPDATE,
            PLATFORM_TENANT_ADMIN_CREATE,
            PLATFORM_STAFF_ASSIGN_SUPER_ADMIN,
            PLATFORM_ROLE_CREATE,
            PLATFORM_ROLE_READ,
            PLATFORM_ROLE_DELETE,
            TENANT_CREATE,
            TENANT_READ,
            TENANT_UPDATE,
            TENANT_DELETE,
            ADMIN_STAFF_READ,
            ADMIN_STAFF_CREATE,
            ADMIN_STAFF_UPDATE,
            ADMIN_TENANT_READ,
            ADMIN_TENANT_UPDATE,
            ADMIN_ROLE_CREATE,
            ADMIN_ROLE_READ,
            ADMIN_ROLE_DELETE,
            TENANT_STUDENT_READ,
            TENANT_STUDENT_UPDATE,
            TENANT_LANGUAGE_READ,
            TENANT_LANGUAGE_CREATE,
            TENANT_LANGUAGE_UPDATE,
            TENANT_LANGUAGE_DELETE,
            TENANT_CATEGORY_READ,
            TENANT_CATEGORY_CREATE,
            TENANT_CATEGORY_UPDATE,
            TENANT_CATEGORY_DELETE,
            TENANT_QUESTION_READ,
            TENANT_QUESTION_CREATE,
            TENANT_QUESTION_UPDATE,
            TENANT_QUESTION_DELETE,
            TENANT_LESSON_READ,
            TENANT_LESSON_CREATE,
            TENANT_LESSON_UPDATE,
            TENANT_LESSON_DELETE,
            TENANT_DAILY_QUIZ_READ,
            TENANT_DAILY_QUIZ_CREATE,
            TENANT_DAILY_QUIZ_UPDATE,
            TENANT_DAILY_QUIZ_DELETE,
            TENANT_WEEKLY_QUIZ_READ,
            TENANT_WEEKLY_QUIZ_CREATE,
            TENANT_WEEKLY_QUIZ_UPDATE,
            TENANT_WEEKLY_QUIZ_DELETE,
            TENANT_LEADERBOARD_READ,
            TENANT_LEADERBOARD_CREATE,
            TENANT_BOOKMARK_READ,
            TENANT_BOOKMARK_CREATE,
            TENANT_BOOKMARK_DELETE,
            TENANT_SESSION_READ,
            TENANT_SESSION_CREATE,
            TENANT_QUESTION_STATISTICS_READ
    );

    public static RoleLevel levelOf(String code) { //The levelOf method is a static method that returns the role level of a given permission code.
        if (PLATFORM.contains(code) || Set.of(TENANT_CREATE, TENANT_READ, TENANT_UPDATE, TENANT_DELETE).contains(code)) {
            return RoleLevel.PLATFORM;
        }
        if (TENANT.contains(code)) {
            return RoleLevel.TENANT;
        }
        throw new IllegalArgumentException("Unknown permission code: " + code);
    }

    private PermissionCodes() { //The PermissionCodes constructor is a private constructor that prevents instantiation of the class.
    }
}
