package com.dodibo.learncore.common.tenant;
/*
 * This enum is used to define the scope of the authentication.
 * PLATFORM_STAFF: The authentication is for a platform staff.
 * TENANT_USER: The authentication is for a tenant user.
 * JWT: The authentication is for a JWT token.
 */
public enum AuthScope {
    PLATFORM_STAFF,
    TENANT_USER,
    JWT
}
