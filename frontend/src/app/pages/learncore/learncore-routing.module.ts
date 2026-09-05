import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PermissionCodes } from '../../core/constants/permission-codes';
import { PermissionGuard } from '../../core/guards/permission.guard';
import { DashboardComponent } from './dashboard/dashboard.component';
import { FeaturePlaceholderComponent } from './feature-placeholder/feature-placeholder.component';
import { TenantsComponent } from './tenants/tenants.component';
import { PlatformStaffComponent } from './platform-staff/platform-staff.component';
import { TenantStaffComponent } from './tenant-staff/tenant-staff.component';
import { StudentsComponent } from './students/students.component';
import { ProfileComponent } from './profile/profile.component';
import { QuestionComponent } from './question/question.component';
import { ImportQuestionComponent } from './import-question/import-question.component';
import { QuestionReportComponent } from './question-report/question-report.component';
import { CategoryComponent } from './category/category.component';
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
        path: 'categories',
        component: CategoryComponent,
        data: {
          title: 'Categories',
          permissions: [PermissionCodes.TENANT_CATEGORY_READ],
          scope: 'tenant',
        },
      },
      {
        path: 'questions',
        component: QuestionComponent,
        data: {
          title: 'Questions',
          permissions: [PermissionCodes.TENANT_QUESTION_READ],
          scope: 'tenant',
        },
      },
      {
        path: 'import-questions',
        component: ImportQuestionComponent,
        data: {
          title: 'Import Questions',
          permissions: [PermissionCodes.TENANT_QUESTION_CREATE],
          scope: 'tenant',
        },
      },
      {
        path: 'question-report',
        component: QuestionReportComponent,
        data: {
          title: 'Question Report',
          permissions: [PermissionCodes.TENANT_QUESTION_READ],
          scope: 'tenant',
        },
      },
      {
        path: 'students',
        component: StudentsComponent,
        data: {
          title: 'Students',
          permissions: [PermissionCodes.TENANT_STUDENT_READ],
          scope: 'tenant',
        },
      },
      {
        path: 'staff',
        component: TenantStaffComponent,
        data: {
          title: 'Staff',
          permissions: [PermissionCodes.ADMIN_STAFF_READ],
          scope: 'tenant',
        },
      },
      
      {
        path: 'roles',
        loadComponent: () =>
          import('./roles/roles.component').then((m) => m.RolesComponent),
        data: {
          title: 'Roles',
          permissions: [PermissionCodes.ADMIN_ROLE_READ, PermissionCodes.PLATFORM_ROLE_READ],
          //scope: 'tenant',
        },
      },
        {
        path: 'tenants',
        component: TenantsComponent,
        data: {
          title: 'Tenants',
          permissions: [PermissionCodes.TENANT_READ],
          scope: 'platform',
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
        path: 'platform/staff',
        component: PlatformStaffComponent,
        data: {
          title: 'Platform Staff',
          permissions: [PermissionCodes.PLATFORM_STAFF_READ],
          scope: 'platform',
        },
      },
      {
        path: 'platform/roles',
        loadComponent: () =>
          import('./roles/roles.component').then((m) => m.RolesComponent),
        data: {
          title: 'Platform Roles',
          permissions: [PermissionCodes.PLATFORM_ROLE_READ],
          scope: 'platform',
        },
      },
      {
        path: 'profile',
        component: ProfileComponent,
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
