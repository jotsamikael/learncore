import { SelectionModel } from '@angular/cdk/collections';

import { AfterViewInit, Component, OnDestroy, OnInit, TemplateRef, ViewChild } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
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
import { NgbDropdownModule, NgbModal, NgbModalRef, NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { Subject, debounceTime, finalize, merge, startWith, takeUntil } from 'rxjs';
import { PermissionCodes } from '../../../core/constants/permission-codes';
import { CommonService, ListMeta } from '../../../core/services/common.service';
import { GlobalFormBuilderService } from '../../../core/services/globalFormBuilder.service';
import { TenantsApiService } from '../../../core/services/tenants-api.service';
import { getApiErrorMessage } from '../../../core/utils/api-error.utils';
import { slugifyText } from '../../../core/utils/slug.utils';
import {
  buildFullPhoneNumber,
  findCountryByName,
  splitPhoneNumber,
} from '../../../core/utils/phone.utils';
import { FindTenantsQuery } from '../../../learncoreservices/models/find-tenants-query';
import { TenantResponse } from '../../../learncoreservices/models/tenant-response';
import { SharedModule } from '../../../shared/shared.module';
import { PermissionService } from '../../../core/services/permission.service';
import { chatMessagesData } from '../../../core/data/advancedForm';
import { CountryOption } from '../../../core/models/country-option';
import { NgSelectModule } from '@ng-select/ng-select';
import { DropzoneComponent, DropzoneConfigInterface, DropzoneModule } from 'ngx-dropzone-wrapper';

@Component({
  selector: 'app-tenants',
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
    NgSelectModule,
    DropzoneModule,
  ],
  templateUrl: './tenants.component.html',
  styleUrl: './tenants.component.scss',
})
export class TenantsComponent implements OnInit, AfterViewInit, OnDestroy {
  createForm!: FormGroup;
  detailForm!: FormGroup;
  createFormSubmitted = false;
  createFormError = '';
  createSubmitting = false;
  detailFormSubmitted = false;
  detailError = '';
  detailSaving = false;
  detailReadOnly = true;
  detailSnapshot: TenantResponse | null = null;
  detailTenantUuid: string | null = null;

  displayedColumns: string[] = [
    'select',
    'logoUrl',
    'name',
    'slug',
    'examFocus',
    'phone',
    'email',
    'country',
    'active',
    'lastModifiedDate',
    'actions',
  ];

  data: TenantResponse[] = [];
  selection = new SelectionModel<TenantResponse>(true, []);

  isLoading = true;
  loadError: string | null = null;
  pageSize = 10;
  readonly pageSizeOptions: number[] = [5, 10, 25, 50];
  breadCrumbItems!: Array<{ label?: string; active?: boolean }>;
  meta: ListMeta | null = null;
  pageTitle = 'Tenants';
  readonly countries: CountryOption[] = chatMessagesData as CountryOption[];
  readonly logoDropzoneConfig: DropzoneConfigInterface = {
    url: '#',
    autoProcessQueue: false,
    uploadMultiple: false,
    maxFiles: 1,
    maxFilesize: 5,
    acceptedFiles: 'image/jpeg,image/png,image/webp,image/gif,image/svg+xml',
    addRemoveLinks: true,
    dictDefaultMessage: 'Drop logo here or click to upload',
  };

  createLogoFile: File | null = null;
  createLogoDropzoneError = '';
  detailLogoFile: File | null = null;
  detailLogoDropzoneError = '';

  nameFilter = new FormControl('', { nonNullable: true });
  countryFilter = new FormControl('', { nonNullable: true });
  examFocusFilter = new FormControl('', { nonNullable: true });

  private readonly destroy$ = new Subject<void>();
  private readonly refresh$ = new Subject<void>();
  private createSlugManuallyEdited = false;

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  @ViewChild('tenantDetailModal') tenantDetailModalTpl!: TemplateRef<unknown>;
  @ViewChild('logoDropzone') logoDropzone?: DropzoneComponent;
  @ViewChild('detailLogoDropzone') detailLogoDropzone?: DropzoneComponent;

  constructor(
    private readonly formService: GlobalFormBuilderService,
    private readonly tenantsApi: TenantsApiService,
    private readonly modalService: NgbModal,
    private readonly permissionService: PermissionService,
    private readonly common: CommonService
  ) {}

  ngOnInit(): void {
    this.breadCrumbItems = [
      { label: 'Learncore' },
      { label: this.pageTitle, active: true },
    ];

    this.createForm = this.formService.createTenantForm();
    this.detailForm = this.formService.updateTenantForm();

    this.createForm
      .get('phoneLocal')
      ?.valueChanges.pipe(takeUntil(this.destroy$))
      .subscribe(() => this.syncFormPhone(this.createForm));

    this.createForm
      .get('name')
      ?.valueChanges.pipe(takeUntil(this.destroy$))
      .subscribe((name) => this.syncCreateSlug(name));

    this.detailForm
      .get('phoneLocal')
      ?.valueChanges.pipe(takeUntil(this.destroy$))
      .subscribe(() => this.syncFormPhone(this.detailForm));
  }

  ngAfterViewInit(): void {
    this.sort.active = 'lastModifiedDate';
    this.sort.direction = 'desc';

    merge(
      this.refresh$,
      this.nameFilter.valueChanges.pipe(debounceTime(300)),
      this.countryFilter.valueChanges.pipe(debounceTime(300)),
      this.examFocusFilter.valueChanges.pipe(debounceTime(300)),
      this.sort.sortChange,
      this.paginator.page
    )
      .pipe(startWith(undefined), takeUntil(this.destroy$))
      .subscribe(() => this.loadTenants());
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

  get canCreateTenant(): boolean {
    return this.permissionService.can(PermissionCodes.TENANT_CREATE);
  }

  get canDeleteTenant(): boolean {
    return this.permissionService.can(PermissionCodes.TENANT_DELETE);
  }

  isAllSelected(): boolean {
    return this.common.isAllSelected(this.selection, this.data);
  }

  masterToggle(): void {
    this.common.masterToggle(this.selection, this.data);
  }

  toggleRow(row: TenantResponse): void {
    this.selection.toggle(row);
  }

  refresh(): void {
    this.refresh$.next();
  }

  clearFilters(): void {
    this.nameFilter.setValue('');
    this.examFocusFilter.setValue('');
    this.countryFilter.setValue('');
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

  openCreateModal(content: TemplateRef<unknown>): void {
    this.createFormError = '';
    this.createFormSubmitted = false;
    this.createSlugManuallyEdited = false;
    this.common.openCreateModal(this.modalService, content, this.createForm, {
      name: '',
      slug: '',
      examFocus: '',
      countryOption: null,
      country: '',
      phoneLocal: '',
      phone: '',
      email: '',
      description: '',
    });
    setTimeout(() => this.resetCreateLogo());
  }

  onCreateSlugInput(): void {
    this.createSlugManuallyEdited = true;
  }

  onCreateLogoAdded(file: File): void {
    this.createLogoDropzoneError = '';
    this.createLogoFile = file;
  }

  onCreateLogoRemoved(): void {
    this.createLogoFile = null;
    this.createLogoDropzoneError = '';
  }

  onCreateLogoError(args: [File, string]): void {
    this.createLogoDropzoneError = args[1] || 'Invalid logo file.';
    this.createLogoFile = null;
  }

  onCreateLogoMaxFilesExceeded(file: File): void {
    const dropzone = this.logoDropzone?.directiveRef?.dropzone();
    if (!dropzone) {
      return;
    }
    dropzone.removeAllFiles(true);
    dropzone.addFile(file);
  }

  onCreateCountryChange(country: CountryOption | null): void {
    this.createForm.patchValue({ country: country?.countryName ?? '' }, { emitEvent: false });
    this.syncFormPhone(this.createForm);
  }

  onDetailCountryChange(country: CountryOption | null): void {
    this.detailForm.patchValue({ country: country?.countryName ?? '' }, { emitEvent: false });
    this.syncFormPhone(this.detailForm);
  }

  onDetailLogoAdded(file: File): void {
    this.detailLogoDropzoneError = '';
    this.detailLogoFile = file;
  }

  onDetailLogoRemoved(): void {
    this.detailLogoFile = null;
    this.detailLogoDropzoneError = '';
  }

  onDetailLogoError(args: [File, string]): void {
    this.detailLogoDropzoneError = args[1] || 'Invalid logo file.';
    this.detailLogoFile = null;
  }

  onDetailLogoMaxFilesExceeded(file: File): void {
    const dropzone = this.detailLogoDropzone?.directiveRef?.dropzone();
    if (!dropzone) {
      return;
    }
    dropzone.removeAllFiles(true);
    dropzone.addFile(file);
  }

  submitCreate(modal: NgbModalRef): void {
    this.createFormSubmitted = true;
    this.createFormError = '';
    this.syncFormPhone(this.createForm);
    if (this.createForm.invalid) {
      return;
    }

    const raw = this.createForm.getRawValue();
    this.createSubmitting = true;
    this.tenantsApi
      .createTenant(
        {
          name: String(raw.name).trim(),
          email: String(raw.email).trim(),
          examFocus: String(raw.examFocus).trim(),
          phone: String(raw.phone).trim(),
          slug: String(raw.slug).trim(),
          country: String(raw.country).trim(),
        },
        this.createLogoFile
      )
      .pipe(finalize(() => (this.createSubmitting = false)))
      .subscribe({
        next: () => {
          modal.close();
          this.createFormSubmitted = false;
          this.resetCreateLogo();
          this.refresh();
        },
        error: (err) => {
          this.createFormError = getApiErrorMessage(err, 'Failed to create tenant.');
        },
      });
  }

  onView(row: TenantResponse): void {
    this.openTenantDetailModal(row, false);
  }

  onEdit(row: TenantResponse): void {
    this.openTenantDetailModal(row, true);
  }

  openTenantDetailModal(row: TenantResponse, startInEditMode: boolean): void {
    this.detailError = '';
    this.detailFormSubmitted = false;
    if (!row.uuid) {
      this.detailSnapshot = null;
      this.detailTenantUuid = null;
      this.detailError = 'This tenant has no public id (uuid).';
      this.common.openModal(this.modalService, this.tenantDetailModalTpl);
      return;
    }

    this.detailSnapshot = row;
    this.detailTenantUuid = row.uuid;
    this.detailReadOnly = !startInEditMode || !this.canCreateTenant;
    this.patchDetailForm(row);
    this.common.openModal(this.modalService, this.tenantDetailModalTpl);
    setTimeout(() => this.resetDetailLogo());
  }

  toggleDetailEditMode(): void {
    const nextReadOnly = this.common.toggleDetailEditMode({
      readOnly: this.detailReadOnly,
      snapshot: this.detailSnapshot,
      saving: this.detailSaving,
      canEdit: this.canCreateTenant,
      form: this.detailForm,
      restoreForm: () => this.patchDetailForm(this.detailSnapshot!),
      onSubmittedReset: () => (this.detailFormSubmitted = false),
      onErrorClear: () => (this.detailError = ''),
    });
    if (nextReadOnly !== null) {
      this.detailReadOnly = nextReadOnly;
      if (this.detailReadOnly) {
        this.resetDetailLogo();
      } else {
        setTimeout(() => this.resetDetailLogo());
      }
    }
  }

  submitDetailUpdate(modal: NgbModalRef): void {
    this.detailFormSubmitted = true;
    this.detailError = '';
    this.syncFormPhone(this.detailForm);
    if (!this.detailTenantUuid || this.detailForm.invalid) {
      return;
    }

    const raw = this.detailForm.getRawValue();
    this.detailSaving = true;
    this.tenantsApi
      .updateTenant(
        this.detailTenantUuid,
        {
          name: String(raw.name).trim(),
          phone: String(raw.phone).trim(),
          email: String(raw.email).trim(),
          country: String(raw.country).trim(),
          examFocus: String(raw.examFocus).trim(),
          description: String(raw.description).trim(),
          logoUrl: this.detailSnapshot?.logoUrl,
        },
        this.detailLogoFile
      )
      .pipe(finalize(() => (this.detailSaving = false)))
      .subscribe({
        next: (updated) => {
          this.detailSnapshot = updated;
          this.detailReadOnly = true;
          this.detailFormSubmitted = false;
          this.patchDetailForm(updated);
          this.resetDetailLogo();
          modal.close();
          this.refresh();
        },
        error: (err) => {
          this.detailError = getApiErrorMessage(err, 'Failed to update tenant.');
        },
      });
  }

  async onDeleteTenant(row: TenantResponse): Promise<void> {
    if (!row.uuid || !this.canDeleteTenant) {
      return;
    }

    const confirmed = await this.common.confirmDelete(
      'Delete tenant?',
      `"${row.name}" will be removed permanently.`
    );
    if (!confirmed) {
      return;
    }

    this.tenantsApi.deleteTenant(row.uuid).subscribe({
      next: () => {
        this.selection.deselect(row);
        this.refresh();
      },
      error: (err) => {
        this.common.showErrorAlert('Error', getApiErrorMessage(err, 'Failed to delete tenant.'));
      },
    });
  }

  exportCsv(): void {
    this.common.exportCsv(this.getExportRows(this.data), 'tenants');
  }

  exportExcel(): void {
    this.common.exportExcel(this.getExportRows(this.data), 'tenants');
  }

  exportSelectedCsv(): void {
    const rows = this.selection.selected;
    if (!rows.length) {
      return;
    }
    this.common.exportSelectedCsv(this.getExportRows(rows), 'tenants');
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

  printTenantDetail(): void {
    const row = this.detailSnapshot;
    if (!row) {
      return;
    }
    const rows: [string, string][] = [
      ['UUID', row.uuid ?? ''],
      ['Slug', row.slug ?? ''],
      ['Name', row.name ?? ''],
      ['Exam Focus', row.examFocus ?? ''],
      ['Description', row.description ?? ''],
      ['Country', row.country ?? ''],
      ['Email', row.email ?? '—'],
      ['Phone', row.phone ?? '—'],
      ['Last modified', this.formatMedium(row.lastModifiedDate)],
    ];
    this.common.printHtmlTable('Tenant', rows);
  }

  private loadTenants(): void {
    if (!this.paginator || !this.sort) {
      return;
    }

    this.isLoading = true;
    this.loadError = null;
    this.selection.clear();

    const query = this.buildQuery(this.sort, this.paginator.pageIndex, this.pageSize);
    this.tenantsApi
      .getTenants(query)
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
          this.loadError = getApiErrorMessage(err, 'Failed to load tenants.');
        },
      });
  }

  private buildQuery(sort: Sort, pageIndex: number, size: number): FindTenantsQuery {
    const sortByMap: Record<string, string> = {
      name: 'name',
      examFocus: 'examFocus',
      email: 'email',
      phone: 'phone',
      country: 'country',
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
      examFocus: this.examFocusFilter.value.trim() || undefined,
      country: this.countryFilter.value.trim() || undefined,
    };
  }

  private patchDetailForm(row: TenantResponse): void {
    const countryOption = findCountryByName(this.countries, row.country);
    const phoneLocal = splitPhoneNumber(row.phone, countryOption);

    this.common.patchDetailForm(
      this.detailForm,
      {
        name: row.name ?? '',
        examFocus: row.examFocus ?? '',
        countryOption,
        country: row.country ?? '',
        phoneLocal,
        phone: row.phone ?? '',
        email: row.email ?? '',
        description: row.description ?? '',
      },
      this.detailReadOnly
    );
  }

  private syncFormPhone(form: FormGroup): void {
    const country = form.get('countryOption')?.value as CountryOption | null;
    const phoneLocal = form.get('phoneLocal')?.value as string | undefined;
    const full = buildFullPhoneNumber(country, phoneLocal);
    form.get('phone')?.setValue(full, { emitEvent: false });
  }

  private syncCreateSlug(name: unknown): void {
    if (this.createSlugManuallyEdited) {
      return;
    }
    const slug = slugifyText(String(name ?? ''));
    this.createForm.get('slug')?.setValue(slug, { emitEvent: false });
  }

  private resetCreateLogo(): void {
    this.createLogoFile = null;
    this.createLogoDropzoneError = '';
    this.logoDropzone?.directiveRef?.dropzone()?.removeAllFiles(true);
  }

  private resetDetailLogo(): void {
    this.detailLogoFile = null;
    this.detailLogoDropzoneError = '';
    this.detailLogoDropzone?.directiveRef?.dropzone()?.removeAllFiles(true);
  }

  private getExportRows(rows: TenantResponse[]): string[][] {
    const header = [
      'UUID',
      'Name',
      'Slug',
      'Description',
      'Country',
      'Phone',
      'Email',
      'Exam Focus',
      'Last modified',
    ];
    const body = rows.map((row) => [
      row.uuid ?? '',
      row.name ?? '',
      row.slug ?? '',
      row.description ?? '',
      row.country ?? '',
      row.phone ?? '',
      row.email ?? '',
      row.examFocus ?? '',
      this.formatMedium(row.lastModifiedDate, ''),
    ]);
    return [header, ...body];
  }
}