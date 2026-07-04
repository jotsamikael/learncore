package com.dodibo.learncore.common.tenant;

/**
 * TenantContext is a class that holds the tenant id and the authentication scope.
 * It is used to store the tenant id and the authentication scope in the thread local.
 */
public final class TenantContext {

    private static final ThreadLocal<Long> TENANT_ID = new ThreadLocal<>();
    private static final ThreadLocal<AuthScope> AUTH_SCOPE = new ThreadLocal<>();

    private TenantContext() {
    }

    public static void setTenantId(Long tenantId) {
        TENANT_ID.set(tenantId);
    }

    public static Long getTenantId() {
        return TENANT_ID.get();
    }

    public static void setAuthScope(AuthScope scope) {
        AUTH_SCOPE.set(scope);
    }

    public static AuthScope getAuthScope() {
        return AUTH_SCOPE.get();
    }

    public static void clear() {
        TENANT_ID.remove();
        AUTH_SCOPE.remove();
    }
}
