import { SelectionModel } from '@angular/cdk/collections';
import { NgTemplateOutlet } from '@angular/common';
import { AfterViewInit, Component, OnDestroy, OnInit, TemplateRef, ViewChild } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatChipsModule } from '@angular/material/chips';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatMenuModule } from '@angular/material/menu';
import { MatPaginator, MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { MatSort, MatSortModule, Sort } from '@angular/material/sort';
import { MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { ActivatedRoute } from '@angular/router';
import { NgbDropdownModule, NgbModal, NgbModalRef, NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { Subject, debounceTime, finalize, merge, startWith, takeUntil } from 'rxjs';
import { PermissionCodes } from '../../../core/constants/permission-codes';
import { CommonService, ListMeta } from '../../../core/services/common.service';
import { GlobalFormBuilderService } from '../../../core/services/globalFormBuilder.service';
import { PermissionService } from '../../../core/services/permission.service';
import { RolesApiService } from '../../../core/services/roles-api.service';
import { getApiErrorMessage } from '../../../core/utils/api-error.utils';
import { FindRolesQuery } from '../../../learncoreservices/models/find-roles-query';
import { PermissionResponse } from '../../../learncoreservices/models/permission-response';
import { RoleResponse } from '../../../learncoreservices/models/role-response';
import { SharedModule } from '../../../shared/shared.module';

@Component({
  selector: 'app-roles',
  standalone: true,
  imports: [
    NgTemplateOutlet,
    ReactiveFormsModule,
    SharedModule,
    NgbModule,
    NgbDropdownModule,
    MatTableModule,
    MatSortModule,
    MatPaginatorModule,
    MatCheckboxModule,
    MatChipsModule,
    MatIconModule,
    MatButtonModule,
    MatMenuModule,
    MatProgressSpinnerModule,
    MatTooltipModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
  ],
  templateUrl: './roles.component.html',
  styleUrl: './roles.component.scss',
})
export class RolesComponent implements OnInit, AfterViewInit, OnDestroy {
  createForm!: FormGroup;
  detailForm!: FormGroup;
  createFormSubmitted = false;
  createFormError = '';
  createSubmitting = false;
  detailFormSubmitted = false;
  detailError = '';
  detailSaving = false;
  detailReadOnly = true;
  detailSnapshot: RoleResponse | null = null;
  detailRoleUuid: string | null = null;

  displayedColumns: string[] = [
    'select',
    'name',
    'description',
    'level',
    'tenantUuid',
    'permissions',
    'lastModifiedDate',
    'actions',
  ];

  data: RoleResponse[] = [];
  selection = new SelectionModel<RoleResponse>(true, []);
  assignablePermissions: PermissionResponse[] = [];
  permissionsLoading = false;

  isLoading = true;
  loadError: string | null = null;
  pageSize = 10;
  readonly pageSizeOptions: number[] = [5, 10, 25, 50];
  breadCrumbItems!: Array<{ label?: string; active?: boolean }>;
  meta: ListMeta | null = null;
  pageTitle = 'Roles';
  isPlatformRoute = false;

  readonly levelOptions = [
    { value: '', label: 'All levels' },
    { value: 'PLATFORM', label: 'Platform' },
    { value: 'TENANT', label: 'Tenant' },
  ];

  nameFilter = new FormControl('', { nonNullable: true });
  descriptionFilter = new FormControl('', { nonNullable: true });
  levelFilter = new FormControl('', { nonNullable: true });

  private readonly destroy$ = new Subject<void>();
  private readonly refresh$ = new Subject<void>();

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  @ViewChild('roleDetailModal') roleDetailModalTpl!: TemplateRef<unknown>;

  constructor(
    private readonly formService: GlobalFormBuilderService,
    private readonly rolesApi: RolesApiService,
    private readonly modalService: NgbModal,
    private readonly permissionService: PermissionService,
    private readonly route: ActivatedRoute,
    private readonly common: CommonService
  ) {}

  ngOnInit(): void {
    this.isPlatformRoute = this.route.snapshot.data['scope'] === 'platform';
    this.pageTitle = this.isPlatformRoute ? 'Platform Roles' : 'Roles';
    this.breadCrumbItems = [
      { label: 'Learncore' },
      { label: this.pageTitle, active: true },
    ];

    this.createForm = this.formService.createRoleForm();
    this.detailForm = this.formService.updateRoleForm();
    this.loadAssignablePermissions();
  }

  ngAfterViewInit(): void {
    this.sort.active = 'lastModifiedDate';
    this.sort.direction = 'desc';

    merge(
      this.refresh$,
      this.nameFilter.valueChanges.pipe(debounceTime(300)),
      this.descriptionFilter.valueChanges.pipe(debounceTime(300)),
      this.levelFilter.valueChanges,
      this.sort.sortChange,
      this.paginator.page
    )
      .pipe(startWith(undefined), takeUntil(this.destroy$))
      .subscribe(() => this.loadRoles());
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  get cf() {
    return this.createForm.controls;
  }

  get df() {
    return this.detailForm.controls;
  }

  getPermissionCodes(form: FormGroup): string[] {
    return this.common.getFormArrayValue(form, 'permissionCodes');
  }

  permissionCodesControl(form: FormGroup): FormControl<string[]> {
    return this.common.getFormControl<string[]>(form, 'permissionCodes');
  }

  removePermissionCode(form: FormGroup, code: string): void {
    this.common.removeFormArrayValue(form, 'permissionCodes', code);
  }

  get canCreateRole(): boolean {
    return this.isPlatformRoute
      ? this.permissionService.can(PermissionCodes.PLATFORM_ROLE_CREATE)
      : this.permissionService.can(PermissionCodes.ADMIN_ROLE_CREATE);
  }

  get canDeleteRole(): boolean {
    return this.isPlatformRoute
      ? this.permissionService.can(PermissionCodes.PLATFORM_ROLE_DELETE)
      : this.permissionService.can(PermissionCodes.ADMIN_ROLE_DELETE);
  }

  isAllSelected(): boolean {
    return this.common.isAllSelected(this.selection, this.data);
  }

  masterToggle(): void {
    this.common.masterToggle(this.selection, this.data);
  }

  toggleRow(row: RoleResponse): void {
    this.selection.toggle(row);
  }

  refresh(): void {
    this.refresh$.next();
  }

  clearFilters(): void {
    this.nameFilter.setValue('');
    this.descriptionFilter.setValue('');
    this.levelFilter.setValue('');
    if (this.paginator) {
      this.paginator.firstPage();
    }
  }

  onPaginatorPage(_event: PageEvent): void {
    // Handled by merge subscription.
  }

  onPageSizeChange(next: number): void {
    if (next === this.pageSize || !this.paginator) {
      return;
    }
    this.pageSize = next;
    this.paginator.pageIndex = 0;
    this.refresh();
  }

  shortUuid(uuid?: string): string {
    return this.common.shortUuid(uuid);
  }

  formatMedium(value: unknown, empty = '—'): string {
    return this.common.formatMedium(value, empty);
  }

  permissionSummary(codes?: string[]): string {
    return this.common.summarizeList(codes);
  }

  openCreateModal(content: TemplateRef<unknown>): void {
    this.createFormError = '';
    this.createFormSubmitted = false;
    this.common.openCreateModal(this.modalService, content, this.createForm, {
      name: '',
      description: '',
      permissionCodes: [],
    });
  }

  submitCreate(modal: NgbModalRef): void {
    this.createFormSubmitted = true;
    this.createFormError = '';
    if (this.createForm.invalid) {
      return;
    }

    const raw = this.createForm.getRawValue();
    const permissionCodes = (raw.permissionCodes as string[] | null) ?? [];
    if (!permissionCodes.length) {
      this.createForm.get('permissionCodes')?.setErrors({ required: true });
      return;
    }

    this.createSubmitting = true;
    this.rolesApi
      .createRole({
        name: String(raw.name).trim(),
        description: String(raw.description).trim(),
        permissionCodes,
      })
      .pipe(finalize(() => (this.createSubmitting = false)))
      .subscribe({
        next: () => {
          modal.close();
          this.createFormSubmitted = false;
          this.refresh();
        },
        error: (err) => {
          this.createFormError = getApiErrorMessage(err, 'Failed to create role.');
        },
      });
  }

  onView(row: RoleResponse): void {
    this.openRoleDetailModal(row, false);
  }

  onEdit(row: RoleResponse): void {
    this.openRoleDetailModal(row, true);
  }

  openRoleDetailModal(row: RoleResponse, startInEditMode: boolean): void {
    this.detailError = '';
    this.detailFormSubmitted = false;
    if (!row.uuid) {
      this.detailSnapshot = null;
      this.detailRoleUuid = null;
      this.detailError = 'This role has no public id (uuid).';
      this.common.openModal(this.modalService, this.roleDetailModalTpl);
      return;
    }

    this.detailSnapshot = row;
    this.detailRoleUuid = row.uuid;
    this.detailReadOnly = !startInEditMode || !this.canCreateRole;
    this.patchDetailForm(row);
    this.common.openModal(this.modalService, this.roleDetailModalTpl);
  }

  toggleDetailEditMode(): void {
    const nextReadOnly = this.common.toggleDetailEditMode({
      readOnly: this.detailReadOnly,
      snapshot: this.detailSnapshot,
      saving: this.detailSaving,
      canEdit: this.canCreateRole,
      form: this.detailForm,
      restoreForm: () => this.patchDetailForm(this.detailSnapshot!),
      onSubmittedReset: () => (this.detailFormSubmitted = false),
      onErrorClear: () => (this.detailError = ''),
    });
    if (nextReadOnly !== null) {
      this.detailReadOnly = nextReadOnly;
    }
  }

  submitDetailUpdate(modal: NgbModalRef): void {
    this.detailFormSubmitted = true;
    this.detailError = '';
    if (!this.detailRoleUuid || this.detailForm.invalid) {
      return;
    }

    const raw = this.detailForm.getRawValue();
    const permissionCodes = (raw.permissionCodes as string[] | null) ?? [];
    if (!permissionCodes.length) {
      this.detailForm.get('permissionCodes')?.setErrors({ required: true });
      return;
    }

    this.detailSaving = true;
    this.rolesApi
      .updateRole(this.detailRoleUuid, {
        name: String(raw.name).trim(),
        description: String(raw.description).trim(),
        permissionCodes,
      })
      .pipe(finalize(() => (this.detailSaving = false)))
      .subscribe({
        next: (updated) => {
          this.detailSnapshot = updated;
          this.detailReadOnly = true;
          this.detailFormSubmitted = false;
          modal.close();
          this.refresh();
        },
        error: (err) => {
          this.detailError = getApiErrorMessage(err, 'Failed to update role.');
        },
      });
  }

  async onDeleteRole(row: RoleResponse): Promise<void> {
    if (!row.uuid || !this.canDeleteRole) {
      return;
    }

    const confirmed = await this.common.confirmDelete(
      'Delete role?',
      `"${row.name}" will be removed permanently.`
    );
    if (!confirmed) {
      return;
    }

    this.rolesApi.deleteRole(row.uuid).subscribe({
      next: () => {
        this.selection.deselect(row);
        this.refresh();
      },
      error: (err) => {
        this.common.showErrorAlert('Error', getApiErrorMessage(err, 'Failed to delete role.'));
      },
    });
  }

  exportCsv(): void {
    this.common.exportCsv(this.getExportRows(this.data), 'roles');
  }

  exportExcel(): void {
    this.common.exportExcel(this.getExportRows(this.data), 'roles');
  }

  exportSelectedCsv(): void {
    const rows = this.selection.selected;
    if (!rows.length) {
      return;
    }
    this.common.exportSelectedCsv(this.getExportRows(rows), 'roles');
  }

  exportPdf(): void {
    this.common.printDataTable({
      title: this.pageTitle,
      table: this.getExportRows(this.data),
      metaLine: this.common.getExportMetaLine(this.meta),
      forPdfHint: true,
    });
  }

  printTable(): void {
    this.common.printDataTable({
      title: this.pageTitle,
      table: this.getExportRows(this.data),
      metaLine: this.common.getExportMetaLine(this.meta),
    });
  }

  printRoleDetail(): void {
    const row = this.detailSnapshot;
    if (!row) {
      return;
    }
    const rows: [string, string][] = [
      ['UUID', row.uuid ?? ''],
      ['Name', row.name ?? ''],
      ['Description', row.description ?? ''],
      ['Level', row.level ?? ''],
      ['Tenant UUID', row.tenantUuid ?? '—'],
      ['Permissions', (row.permissionCodes ?? []).join(', ')],
      ['Created', this.formatMedium(row.createdDate)],
      ['Last modified', this.formatMedium(row.lastModifiedDate)],
    ];
    this.common.printHtmlTable('Role', rows);
  }

  private loadRoles(): void {
    if (!this.paginator || !this.sort) {
      return;
    }

    this.isLoading = true;
    this.loadError = null;
    this.selection.clear();

    const query = this.buildQuery(this.sort, this.paginator.pageIndex, this.pageSize);
    this.rolesApi
      .getRoles(query)
      .pipe(finalize(() => (this.isLoading = false)))
      .subscribe({
        next: (page) => {
          this.data = page.content ?? [];
          this.meta = {
            totalItems: page.totalElements ?? 0,
            totalPages: page.totalPages ?? 0,
            currentPage: (page.number ?? 0) + 1,
          };
        },
        error: (err) => {
          this.data = [];
          this.meta = null;
          this.loadError = getApiErrorMessage(err, 'Failed to load roles.');
        },
      });
  }

  private loadAssignablePermissions(): void {
    this.permissionsLoading = true;
    this.rolesApi
      .listAssignablePermissions()
      .pipe(finalize(() => (this.permissionsLoading = false)))
      .subscribe({
        next: (permissions) => {
          this.assignablePermissions = permissions ?? [];
        },
        error: () => {
          this.assignablePermissions = [];
        },
      });
  }

  private buildQuery(sort: Sort, pageIndex: number, size: number): FindRolesQuery {
    const sortByMap: Record<string, string> = {
      name: 'name',
      description: 'description',
      level: 'level',
      lastModifiedDate: 'lastModifiedDate',
    };
    const sortBy = sortByMap[sort.active] ?? 'lastModifiedDate';
    const sortDirection = sort.direction === 'asc' ? 'ASC' : 'DESC';

    return {
      page: pageIndex,
      size,
      sortBy,
      sortDirection,
      name: this.nameFilter.value.trim() || undefined,
      description: this.descriptionFilter.value.trim() || undefined,
      roleLevel: this.levelFilter.value || undefined,
    };
  }

  private patchDetailForm(row: RoleResponse): void {
    this.common.patchDetailForm(
      this.detailForm,
      {
        name: row.name ?? '',
        description: row.description ?? '',
        permissionCodes: row.permissionCodes ?? [],
      },
      this.detailReadOnly
    );
  }

  private getExportRows(rows: RoleResponse[]): string[][] {
    const header = [
      'UUID',
      'Name',
      'Description',
      'Level',
      'Tenant UUID',
      'Permissions',
      'Last modified',
    ];
    const body = rows.map((row) => [
      row.uuid ?? '',
      row.name ?? '',
      row.description ?? '',
      row.level ?? '',
      row.tenantUuid ?? '',
      (row.permissionCodes ?? []).join('; '),
      this.formatMedium(row.lastModifiedDate, ''),
    ]);
    return [header, ...body];
  }
}
