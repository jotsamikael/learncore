export const PermissionCodes = {
  PLATFORM_STAFF_READ: 'platform.staff.read',
  PLATFORM_STAFF_CREATE: 'platform.staff.create',
  PLATFORM_STAFF_UPDATE: 'platform.staff.update',
  PLATFORM_ROLE_CREATE: 'platform.role.create',
  PLATFORM_ROLE_READ: 'platform.role.read',
  PLATFORM_ROLE_DELETE: 'platform.role.delete',
  TENANT_CREATE: 'tenant.create',
  TENANT_READ: 'tenant.read',
  TENANT_UPDATE: 'tenant.update',
  TENANT_DELETE: 'tenant.delete',
  ADMIN_STAFF_READ: 'admin.staff.read',
  ADMIN_STAFF_CREATE: 'admin.staff.create',
  ADMIN_STAFF_UPDATE: 'admin.staff.update',
  ADMIN_TENANT_READ: 'admin.tenant.read',
  ADMIN_TENANT_UPDATE: 'admin.tenant.update',
  ADMIN_ROLE_CREATE: 'admin.role.create',
  ADMIN_ROLE_READ: 'admin.role.read',
  ADMIN_ROLE_DELETE: 'admin.role.delete',
  TENANT_STUDENT_READ: 'tenant.student.read',
  TENANT_STUDENT_UPDATE: 'tenant.student.update',
} as const;

export type PermissionCode = typeof PermissionCodes[keyof typeof PermissionCodes];
