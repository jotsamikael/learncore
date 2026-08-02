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
            TENANT_LANGUAGE_CREATE
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
            TENANT_LANGUAGE_CREATE
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
