import { SelectionModel } from '@angular/cdk/collections';
import { AfterViewInit, Component, OnDestroy, OnInit, TemplateRef, ViewChild } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
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
import { PermissionService } from '../../../core/services/permission.service';
import { getApiErrorMessage } from '../../../core/utils/api-error.utils';

import { SharedModule } from '../../../shared/shared.module';
import { TenantStaffApiService } from 'src/app/core/services/tenant-staff-api.service';
import { TenantStaffDetailsResponse, StaffResponse, FindStaffQuery } from 'src/app/learncoreservices/models';

@Component({
  selector: 'app-tenant-staff',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    SharedModule,
    NgbModule,
    NgbDropdownModule,
    MatTableModule,
    MatSortModule,
    MatPaginatorModule,
    MatCheckboxModule,
    MatIconModule,
    MatButtonModule,
    MatMenuModule,
    MatProgressSpinnerModule,
    MatTooltipModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
  ],
  templateUrl: './tenant-staff.component.html',
  styleUrl: './tenant-staff.component.scss',
})
export class TenantStaffComponent implements OnInit, AfterViewInit, OnDestroy {
  createForm!: FormGroup;
  detailForm!: FormGroup;

  createFormSubmitted = false;
  createFormError = '';
  createSubmitting = false;

  detailFormSubmitted = false;
  detailError = '';
  detailSaving = false;
  detailReadOnly = true;
  detailSnapshot: TenantStaffDetailsResponse | null = null;
  detailStaffUuid: string | null = null;

  displayedColumns: string[] = [
    'select',
    'firstname',
    'lastname',
    'email',
    'positionName',
    'status',
    'actions',
  ];

  data: StaffResponse[] = [];
  selection = new SelectionModel<StaffResponse>(true, []);

  isLoading = true;
  loadError: string | null = null;
  pageSize = 10;
  readonly pageSizeOptions: number[] = [5, 10, 25, 50];
  breadCrumbItems!: Array<{ label?: string; active?: boolean }>;
  meta: ListMeta | null = null;
  
  pageTitle = 'Tenant Staff Management';
  isPlatformRoute = false;
  routeTenantUuid?: string;

  firstnameFilter = new FormControl('', { nonNullable: true });
  lastnameFilter = new FormControl('', { nonNullable: true });
  emailFilter = new FormControl('', { nonNullable: true });
  positionFilter = new FormControl('', { nonNullable: true });

  private readonly destroy$ = new Subject<void>();
  private readonly refresh$ = new Subject<void>();

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  @ViewChild('staffDetailModal') staffDetailModalTpl!: TemplateRef<unknown>;

  constructor(
    private readonly staffApi: TenantStaffApiService,
    private readonly modalService: NgbModal,
    private readonly permissionService: PermissionService,
    private readonly route: ActivatedRoute,
    private readonly common: CommonService
  ) {}

  ngOnInit(): void {
    // Dynamic context checking matching your Roles component execution layout[cite: 3]
    this.isPlatformRoute = this.route.snapshot.data['scope'] === 'platform';
    this.routeTenantUuid = this.route.snapshot.params['tenantUuid'];
    
    this.pageTitle = this.isPlatformRoute ? 'Global Tenant Staff Profiles' : 'Staff Roster';
    this.breadCrumbItems = [
      { label: 'Learncore' },
      { label: this.pageTitle, active: true },
    ];

    this.initForms();
  }

  private initForms(): void {
    this.createForm = new FormGroup({
      tenantUuid: new FormControl(this.routeTenantUuid || '', this.isPlatformRoute ? [Validators.required] : []),
      firstname: new FormControl('', [Validators.required]),
      lastname: new FormControl('', [Validators.required]),
      email: new FormControl('', [Validators.required, Validators.email]),
      positionName: new FormControl('', [Validators.required]),
    });

    this.detailForm = new FormGroup({
      firstname: new FormControl({ value: '', disabled: true }, [Validators.required]),
      lastname: new FormControl({ value: '', disabled: true }, [Validators.required]),
      positionName: new FormControl({ value: '', disabled: true }, [Validators.required]),
    });
  }

  ngAfterViewInit(): void {
    this.sort.active = 'lastname';
    this.sort.direction = 'asc';

    merge(
      this.refresh$,
      this.firstnameFilter.valueChanges.pipe(debounceTime(300)),
      this.lastnameFilter.valueChanges.pipe(debounceTime(300)),
      this.emailFilter.valueChanges.pipe(debounceTime(300)),
      this.positionFilter.valueChanges.pipe(debounceTime(300)),
      this.sort.sortChange,
      this.paginator.page
    )
      .pipe(startWith(undefined), takeUntil(this.destroy$))
      .subscribe(() => this.loadStaffMembers());
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  get sf() { return this.createForm.controls; }
  get df() { return this.detailForm.controls; }

  get canCreateStaff(): boolean {
    return this.isPlatformRoute
      ? this.permissionService.can(PermissionCodes.PLATFORM_ROLE_CREATE)
      : this.permissionService.can(PermissionCodes.ADMIN_ROLE_CREATE);
  }

  get canUpdateStaff(): boolean {
    return this.isPlatformRoute
      ? this.permissionService.can(PermissionCodes.PLATFORM_ROLE_CREATE)
      : this.permissionService.can(PermissionCodes.ADMIN_ROLE_CREATE);
  }

  isAllSelected(): boolean { return this.common.isAllSelected(this.selection, this.data); }
  masterToggle(): void { this.common.masterToggle(this.selection, this.data); }
  toggleRow(row: StaffResponse): void { this.selection.toggle(row); }
  refresh(): void { this.refresh$.next(); }
  shortUuid(uuid?: string): string { return this.common.shortUuid(uuid); }

  clearFilters(): void {
    this.firstnameFilter.setValue('');
    this.lastnameFilter.setValue('');
    this.emailFilter.setValue('');
    this.positionFilter.setValue('');
    if (this.paginator) {
      this.paginator.firstPage();
    }
  }

  onPaginatorPage(_event: PageEvent): void {}

  onPageSizeChange(next: number): void {
    if (next === this.pageSize || !this.paginator) { return; }
    this.pageSize = next;
    this.paginator.pageIndex = 0;
    this.refresh();
  }

  private loadStaffMembers(): void {
    if (!this.paginator || !this.sort) { return; }

    this.isLoading = true;
    this.loadError = null;
    this.selection.clear();

    const query = this.buildQuery(this.sort, this.paginator.pageIndex, this.pageSize);
    
    // Explicitly pass query and tenant scope tracking parameters[cite: 5]
    this.staffApi
      .getTenantStaff(query, this.routeTenantUuid)
      .pipe(finalize(() => (this.isLoading = false)))
      .subscribe({
        next: (page) => {
          this.data = page.content ?? [];
          console.log(this.data);
          this.meta = {
            totalItems: page.totalElements ?? 0,
            totalPages: page.totalPages ?? 0,
            currentPage: (page.number ?? 0) + 1,
          };
        },
        error: (err) => {
          this.data = [];
          this.meta = null;
          this.loadError = getApiErrorMessage(err, 'Failed to fetch active tenant staff registry.');
        },
      });
  }

  private buildQuery(sort: Sort, pageIndex: number, size: number): FindStaffQuery {
    return {
      page: pageIndex,
      size,
      sortBy: sort.active || 'lastname',
      sortDirection: sort.direction === 'desc' ? 'DESC' : 'ASC',
      firstname: this.firstnameFilter.value.trim() || undefined,
      lastname: this.lastnameFilter.value.trim() || undefined,
      email: this.emailFilter.value.trim() || undefined,
      positionName: this.positionFilter.value.trim() || undefined,
    };
  }

  openCreateModal(content: TemplateRef<unknown>): void {
    this.createFormError = '';
    this.createFormSubmitted = false;
    this.createForm.reset({
      tenantUuid: this.routeTenantUuid || '',
      firstname: '',
      lastname: '',
      email: '',
      positionName: ''
    });
    this.common.openModal(this.modalService, content);
  }

  submitCreate(modal: NgbModalRef): void {
    this.createFormSubmitted = true;
    this.createFormError = '';
    if (this.createForm.invalid) { return; }

    this.createSubmitting = true;
    this.staffApi
      .createTenantStaff(this.createForm.getRawValue())
      .pipe(finalize(() => (this.createSubmitting = false)))
      .subscribe({
        next: () => {
          modal.close();
          this.refresh();
        },
        error: (err) => this.createFormError = getApiErrorMessage(err, 'Failed to create tenant staff member.'),
      });
  }

  onView(row: StaffResponse): void {
    this.fetchAndDisplayDetails(row, true);
  }

  onEdit(row: StaffResponse): void {
    this.fetchAndDisplayDetails(row, false);
  }

  private fetchAndDisplayDetails(row: StaffResponse, readOnly: boolean): void {
    if (!row.uuid) { return; }
    this.detailError = '';
    this.detailStaffUuid = row.uuid;
    this.detailReadOnly = readOnly;

    this.isLoading = true;
    this.staffApi.getTenantStaffDetails(row.uuid)
      .pipe(finalize(() => this.isLoading = false))
      .subscribe({
        next: (fullDetails) => {
          this.detailSnapshot = fullDetails;
          this.patchDetailForm(fullDetails);
          this.common.openModal(this.modalService, this.staffDetailModalTpl);
        },
        error: (err) => this.common.showErrorAlert('Fetch failed', getApiErrorMessage(err, 'Could not resolve detailed staff metadata.')),
      });
  }

  toggleDetailEditMode(): void {
    if (!this.detailSnapshot) return;
    this.detailReadOnly = !this.detailReadOnly;
    if (this.detailReadOnly) {
      this.patchDetailForm(this.detailSnapshot);
    } else {
      this.detailForm.enable();
    }
  }

  private patchDetailForm(details: TenantStaffDetailsResponse): void {
    this.detailForm.patchValue({
      firstname: details.firstname ?? '',
      lastname: details.lastname ?? '',
      positionName: details.positionName ?? '',
    });
    if (this.detailReadOnly) {
      this.detailForm.disable();
    }
  }

  submitDetailUpdate(modal: NgbModalRef): void {
    this.detailFormSubmitted = true;
    if (this.detailForm.invalid || !this.detailStaffUuid) { return; }

    this.detailSaving = true;
    this.staffApi.updateTenantStaff(this.detailStaffUuid, this.detailForm.getRawValue())
      .pipe(finalize(() => this.detailSaving = false))
      .subscribe({
        next: () => {
          modal.close();
          this.refresh();
        },
        error: (err) => this.detailError = getApiErrorMessage(err, 'Could not commit structural profile updates.'),
      });
  }

  onToggleStatus(row: StaffResponse): void {
    if (!row.uuid) { return; }
    this.staffApi.changeStaffStatus(row.uuid).subscribe({
      next: () => this.refresh(),
      error: (err) => this.common.showErrorAlert('Error changing state', getApiErrorMessage(err, 'Status modification rejected.')),
    });
  }

  private getExportRows(rows: StaffResponse[]): string[][] {
    const header = ['UUID', 'First Name', 'Last Name', 'Email', 'Position', 'Status'];
    const body = rows.map((r) => [
      r.uuid ?? '',
      r.firstname ?? '',
      r.lastname ?? '',
      r.email ?? '',
      r.positionName ?? '',
      r.accountLocked ? 'Suspended' : 'Active',
    ]);
    return [header, ...body];
  }

  exportCsv(): void { this.common.exportCsv(this.getExportRows(this.data), 'tenant-staff'); }
  exportExcel(): void { this.common.exportExcel(this.getExportRows(this.data), 'tenant-staff'); }
  exportSelectedCsv(): void {
    if (!this.selection.selected.length) return;
    this.common.exportSelectedCsv(this.getExportRows(this.selection.selected), 'tenant-staff');
  }
  exportPdf(): void {
    this.common.printDataTable({ title: this.pageTitle, table: this.getExportRows(this.data), metaLine: this.common.getExportMetaLine(this.meta), forPdfHint: true });
  }
  printTable(): void {
    this.common.printDataTable({ title: this.pageTitle, table: this.getExportRows(this.data), metaLine: this.common.getExportMetaLine(this.meta) });
  }
}