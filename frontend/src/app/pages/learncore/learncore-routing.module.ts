import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PermissionCodes } from '../../core/constants/permission-codes';
import { PermissionGuard } from '../../core/guards/permission.guard';
import { DashboardComponent } from './dashboard/dashboard.component';
import { FeaturePlaceholderComponent } from './feature-placeholder/feature-placeholder.component';

const routes: Routes = [
  {
    path: '',
    canActivateChild: [PermissionGuard],
    children: [
      {
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full',
      },
      {
        path: 'dashboard',
        component: DashboardComponent,
        data: {
          title: 'Dashboard',
          breadcrumb: 'Dashboard',
        },
      },
      {
        path: 'students',
        component: FeaturePlaceholderComponent,
        data: {
          title: 'Students',
          permissions: [PermissionCodes.TENANT_STUDENT_READ],
          scope: 'tenant',
        },
      },
      {
        path: 'staff',
        component: FeaturePlaceholderComponent,
        data: {
          title: 'Staff',
          permissions: [PermissionCodes.ADMIN_STAFF_READ],
          scope: 'tenant',
        },
      },
      {
        path: 'staff/create',
        component: FeaturePlaceholderComponent,
        data: {
          title: 'Invite Staff',
          permissions: [PermissionCodes.ADMIN_STAFF_CREATE],
          scope: 'tenant',
        },
      },
      {
        path: 'roles',
        component: FeaturePlaceholderComponent,
        data: {
          title: 'Roles',
          permissions: [PermissionCodes.ADMIN_ROLE_READ],
          scope: 'tenant',
        },
      },
      {
        path: 'settings',
        component: FeaturePlaceholderComponent,
        data: {
          title: 'Tenant Settings',
          permissions: [PermissionCodes.ADMIN_TENANT_READ],
          scope: 'tenant',
        },
      },
      {
        path: 'platform/tenants',
        component: FeaturePlaceholderComponent,
        data: {
          title: 'Tenants',
          permissions: [PermissionCodes.TENANT_READ],
          scope: 'platform',
        },
      },
      {
        path: 'platform/tenants/create',
        component: FeaturePlaceholderComponent,
        data: {
          title: 'Create Tenant',
          permissions: [PermissionCodes.TENANT_CREATE],
          scope: 'platform',
        },
      },
      {
        path: 'platform/staff',
        component: FeaturePlaceholderComponent,
        data: {
          title: 'Platform Staff',
          permissions: [PermissionCodes.PLATFORM_STAFF_READ],
          scope: 'platform',
        },
      },
      {
        path: 'platform/roles',
        component: FeaturePlaceholderComponent,
        data: {
          title: 'Platform Roles',
          permissions: [PermissionCodes.PLATFORM_ROLE_READ],
          scope: 'platform',
        },
      },
      {
        path: 'profile',
        component: FeaturePlaceholderComponent,
        data: {
          title: 'Profile',
        },
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class LearncoreRoutingModule {}
