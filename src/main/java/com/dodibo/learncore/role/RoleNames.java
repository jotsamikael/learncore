package com.dodibo.learncore.role;

import java.util.Set;

/*
 * This class is used to define the names of the roles.
 * It is used to define the names of the platform roles.
 * It is used to define the names of the tenant staff roles.
 * It is used to check if a role is a platform role.
 * It is used to check if a role is a tenant staff role.
 * */
public final class RoleNames {

    public static final String SUPER_ADMIN = "SUPER_ADMIN";
    public static final String PLATFORM_MANAGER = "PLATFORM_MANAGER";
    public static final String PLATFORM_ANALYST = "PLATFORM_ANALYST";
    public static final String TENANT_ADMIN = "TENANT_ADMIN";
    public static final String COURSE_EDITOR = "COURSE_EDITOR";
    public static final String QUESTION_EDITOR = "QUESTION_EDITOR";
    public static final String STUDENT = "STUDENT";

    public static final Set<String> PLATFORM_ROLES = Set.of(
            SUPER_ADMIN, PLATFORM_MANAGER, PLATFORM_ANALYST
    );

    public static final Set<String> TENANT_STAFF_ROLES = Set.of(
            TENANT_ADMIN, COURSE_EDITOR, QUESTION_EDITOR
    );

    private RoleNames() {
    }

    public static boolean isPlatformRole(String roleName) {
        return PLATFORM_ROLES.contains(roleName);
    }

    public static boolean isTenantStaffRole(String roleName) {
        return TENANT_STAFF_ROLES.contains(roleName);
    }
}
