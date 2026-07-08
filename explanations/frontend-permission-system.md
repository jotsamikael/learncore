Here is how permission-based access works in your Angular app, and how to wire menus to it.

---

## Overview: three layers of access control

Access is enforced at **three levels**, which complement each other:

| Layer | Purpose | What happens if denied |
|--------|---------|-------------------------|
| **1. Route guards** | Block navigation to a page | Redirect to `/auth/errors/403` |
| **2. Sidebar menu** | Hide links the user cannot use | Item not shown |
| **3. Component UI** | Hide buttons/actions inside a page | Button/modal not rendered |

Routes protect the page. Menus improve UX. Component checks handle finer actions (Create, Delete, Edit).

---

## End-to-end flow

```mermaid
flowchart TD
    Login[User logs in] --> Session[SessionService saves JWT + roles]
    Session --> Perms[LearncoreAuthService.loadPermissions]
    Perms --> Store[permissions stored in sessionStorage]
    Store --> Menu[Sidebar refreshes via MenuService]
    Store --> Nav[User clicks route]
    Nav --> AuthGuard{AuthGuard: logged in?}
    AuthGuard -->|No| LoginPage[/auth/login]
    AuthGuard -->|Yes| PermGuard{PermissionGuard}
    PermGuard --> Scope{scope matches user type?}
    Scope -->|No| Forbidden403[/auth/errors/403]
    Scope -->|Yes| Codes{has required permission(s)?}
    Codes -->|No| Forbidden403
    Codes -->|Yes| Component[Component loads]
    Component --> UI[PermissionService / hasPermission hides actions]
```

---

## 1. Where permissions come from

On login (`learncore-auth.service.ts`):

1. JWT/session is saved with `permissions: []`.
2. `loadPermissions()` calls the API and stores permission **codes** in the session.
3. `SessionService.session$` emits → sidebar re-filters the menu.

```56:60:learncore/frontend/src/app/core/services/learncore-auth.service.ts
  loadPermissions(): Observable<string[]> {
    return this.api.getMyPermissions().pipe(
      map(permissions => permissions.map(permission => permission.code ?? '').filter(Boolean)),
      tap(codes => this.session.updatePermissions(codes))
    );
  }
```

Session shape (`user-session.model.ts`):

- `tenantUuid: null` → **platform user** (superadmin / platform staff)
- `tenantUuid` set → **tenant user**
- `permissions: string[]` → e.g. `'tenant.read'`, `'platform.role.read'`

Use constants from `permission-codes.ts` so routes, menus, and components stay aligned.

---

## 2. Route-level protection (`learncore-routing.module.ts`)

All LearnCore child routes run under `PermissionGuard`:

```9:13:learncore/frontend/src/app/pages/learncore/learncore-routing.module.ts
const routes: Routes = [
  {
    path: '',
    canActivateChild: [PermissionGuard],
    children: [
```

Before that, `AuthGuard` on the main layout ensures the user is logged in (`app-routing.module.ts`).

### Route `data` fields

Each route can define:

| Field | Meaning |
|--------|---------|
| `permissions` | Required permission code(s) |
| `permissionMode` | `'any'` (default) or `'all'` |
| `scope` | `'tenant'` or `'platform'` |

Example — platform tenants:

```64:71:learncore/frontend/src/app/pages/learncore/learncore-routing.module.ts
        {
        path: 'tenants',
        component: TenantsComponent,
        data: {
          title: 'Tenants',
          permissions: [PermissionCodes.TENANT_READ],
          scope: 'platform',
        },
      },
```

### What `PermissionGuard` checks (in order)

```28:49:learncore/frontend/src/app/core/guards/permission.guard.ts
  private checkPermission(route: ActivatedRouteSnapshot): boolean | UrlTree {
    const permissions = route.data['permissions'] as string[] | undefined;
    const permissionMode = (route.data['permissionMode'] as 'any' | 'all' | undefined) ?? 'any';
    const scope = route.data['scope'] as MenuScope | undefined;

    if (scope === 'tenant' && this.permissionService.isPlatformUser()) {
      return this.router.createUrlTree(['/auth/errors/403']);
    }

    if (scope === 'platform' && !this.permissionService.isPlatformUser()) {
      return this.router.createUrlTree(['/auth/errors/403']);
    }

    if (!permissions?.length) {
      return true;
    }

    const allowed = permissionMode === 'all'
      ? this.permissionService.canAll(permissions)
      : this.permissionService.canAny(permissions);

    return allowed ? true : this.router.createUrlTree(['/auth/errors/403']);
  }
```

Important:

1. **Scope is checked before permissions.**  
   A superadmin with `admin.role.read` still gets **403** on `scope: 'tenant'` routes (e.g. `/learncore/staff`).

2. **No `permissions` = open to any logged-in user in that scope.**  
   e.g. `dashboard`, `profile`.

3. **`permissionMode: 'any'` (default)** — user needs **one** of the listed permissions.  
   `/learncore/roles` allows either `admin.role.read` or `platform.role.read`.

4. **`scope` commented out** on `/learncore/roles` — only permission check runs; both tenant and platform users can try the route if they have a matching permission.

---

## 3. Sidebar menu filtering

Menu definitions live in `menu.ts`. `SidebarComponent` calls `MenuService.getVisibleMenu()` and refreshes when the session changes:

```54:56:learncore/frontend/src/app/layouts/sidebar/sidebar.component.ts
  private refreshMenu(): void {
    this.menuItems = this.menuService.getVisibleMenu(MENU);
  }
```

`MenuService` uses the **same rules** as the guard:

```50:64:learncore/frontend/src/app/core/services/menu.service.ts
  private isMenuItemVisible(item: MenuItem, context: MenuFilterContext): boolean {
    if (item.scope === 'tenant' && context.isPlatformUser) {
      return false;
    }
    if (item.scope === 'platform' && !context.isPlatformUser) {
      return false;
    }
    if (!item.permissions?.length) {
      return true;
    }
    const mode = item.permissionMode ?? 'any';
    return mode === 'all'
      ? this.permissionService.canAll(item.permissions)
      : this.permissionService.canAny(item.permissions);
  }
```

Empty section titles (with no visible items below) are removed automatically.

### How to make a menu visible only for specific permissions

Add or edit an entry in `menu.ts`:

```typescript
{
  id: 44,
  label: 'Tenants',
  icon: 'ri-building-4-line',
  link: '/learncore/tenants',
  permissions: ['tenant.read'],      // required permission code(s)
  permissionMode: 'any',             // optional: 'any' | 'all' (default: 'any')
  scope: 'platform',                 // optional: hide from tenant users
},
```

**Examples:**

| Goal | Menu config |
|------|-------------|
| Only platform users with tenant read | `permissions: ['tenant.read']`, `scope: 'platform'`, `link: '/learncore/tenants'` |
| Tenant admins with staff read | `permissions: ['admin.staff.read']`, `scope: 'tenant'`, `link: '/learncore/staff'` |
| Either role read permission | `permissions: ['platform.role.read', 'admin.role.read']`, `permissionMode: 'any'` |
| Must have both (rare) | `permissions: ['a', 'b']`, `permissionMode: 'all'` |
| Everyone logged in | omit `permissions` (and optionally `scope`) |

**Keep menu `link` and route `path` in sync**, and align `permissions` + `scope` with `learncore-routing.module.ts`.

Current example from your menu:

```44:50:learncore/frontend/src/app/layouts/sidebar/menu.ts
  {
    id: 43,
    label: 'Roles',
    icon: 'ri-lock-line',
    link: '/learncore/roles',
    permissions: ['platform.role.read','admin.role.read'],
  },
```

That shows Roles if the user has **either** permission. The route allows the same. Platform-only roles should use `/learncore/platform/roles` with `platform.role.read` and `scope: 'platform'`.

---

## 4. In-component UI (buttons, modals, actions)

Even if a user reaches a page, individual actions are gated in components via `PermissionService`.

**Tenants example:**

```195:201:learncore/frontend/src/app/pages/learncore/tenants/tenants.component.ts
  get canCreateTenant(): boolean {
    return this.permissionService.can(PermissionCodes.TENANT_CREATE);
  }

  get canDeleteTenant(): boolean {
    return this.permissionService.can(PermissionCodes.TENANT_DELETE);
  }
```

Template:

```html
@if (canCreateTenant) {
  <button (click)="openCreateModal(...)">Create Tenant</button>
}
```

**`hasPermission` directive** (for template-only checks):

```html
<button *hasPermission="'tenant.create'">Create</button>

<!-- multiple permissions, any -->
<div *hasPermission="['admin.role.read', 'platform.role.read']">...</div>

<!-- must have all -->
<div *hasPermission="['a', 'b']" hasPermissionMode="all">...</div>
```

Declared in `CoreModule`, imported by `LayoutsModule`. Standalone components (tenants, roles) use `PermissionService` directly instead.

`PermissionService` API:

- `can(code)` — single permission
- `canAny(codes)` — at least one
- `canAll(codes)` — all required
- `isPlatformUser()` / `isTenantUser()`

---

## 5. Practical checklist when adding a feature

### A. Protect the route (`learncore-routing.module.ts`)

```typescript
{
  path: 'my-feature',
  component: MyFeatureComponent,
  data: {
    title: 'My Feature',
    permissions: [PermissionCodes.SOME_READ],
    scope: 'tenant',  // or 'platform', or omit
  },
},
```

### B. Add a menu item (`menu.ts`)

```typescript
{
  label: 'My Feature',
  icon: 'ri-...',
  link: '/learncore/my-feature',
  permissions: ['some.read'],
  scope: 'tenant',
},
```

### C. Gate actions inside the component

```typescript
get canCreate(): boolean {
  return this.permissionService.can(PermissionCodes.SOME_CREATE);
}
```

Or use `*hasPermission` in templates.

---

## 6. Common pitfalls in your current setup

1. **Menu vs route mismatch**  
   Menu item “Tenants” links to `/learncore/tenants` but has no `scope: 'platform'`, while the route requires `scope: 'platform'`. A tenant user might see the link (if they had `tenant.read`) but get 403 on click. Add `scope: 'platform'` on the menu item.

2. **Scope blocks before permission**  
   Superadmin on `/learncore/roles` (no scope) works. Superadmin on `/learncore/staff` (`scope: 'tenant'`) → 403 even with permissions. Use `/learncore/platform/...` for platform features.

3. **Route permission ≠ action permission**  
   `/learncore/tenants` needs `tenant.read`; “Create Tenant” needs `tenant.create`. Route gets you in; component getters control create/delete/edit.

4. **Hiding menu ≠ securing API**  
   Frontend guards are UX and basic protection. Backend `@PreAuthorize` remains the real enforcement.

5. **Stale permissions after login**  
   `restoreSessionIfNeeded()` refreshes tokens but does not refetch permissions. Permissions can be stale until re-login unless you call `loadPermissions()` on app init.

---

## Summary

| Mechanism | File(s) | Controls |
|-----------|---------|----------|
| Login + permission load | `learncore-auth.service.ts`, `session.service.ts` | What codes the user has |
| Route access | `learncore-routing.module.ts`, `permission.guard.ts` | Who can open a URL |
| Sidebar visibility | `menu.ts`, `menu.service.ts`, `sidebar.component.ts` | Which nav links appear |
| Buttons/actions | `permission.service.ts`, `has-permission.directive.ts`, component getters | Create/Delete/Edit inside a page |

To show a menu only to users with specific permissions: add `permissions` (and usually `scope`) on the `MenuItem` in `menu.ts`, mirror the same `permissions` and `scope` on the matching route in `learncore-routing.module.ts`, and use `PermissionService` or `*hasPermission` for actions inside the component.

If you want, switch to Agent mode and I can align your current `menu.ts` entries with all routes in `learncore-routing.module.ts` (staff, settings, platform tenants/roles, etc.).