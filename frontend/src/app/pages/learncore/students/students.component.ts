import { SelectionModel } from '@angular/cdk/collections';
import { DecimalPipe } from '@angular/common';
import { AfterViewInit, Component, OnDestroy, OnInit, TemplateRef, ViewChild } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
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
import { NgbDropdownModule, NgbModal, NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { Subject, debounceTime, finalize, merge, startWith, takeUntil } from 'rxjs';

import { PermissionCodes } from '../../../core/constants/permission-codes';
import { CommonService, ListMeta } from '../../../core/services/common.service';
import { PermissionService } from '../../../core/services/permission.service';
import { getApiErrorMessage } from '../../../core/utils/api-error.utils';

import { SharedModule } from '../../../shared/shared.module';
import { StudentsApiService } from 'src/app/core/services/students-api.service';
import { StudentDetailResponse, PageStudentResponse, FindStudentsQuery } from 'src/app/learncoreservices/models';

@Component({
  selector: 'app-students',
  standalone: true,
  imports: [
    DecimalPipe,
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
  templateUrl: './students.component.html',
  styleUrl: './students.component.scss',
})
export class StudentsComponent implements OnInit, AfterViewInit, OnDestroy {
  displayedColumns: string[] = [
    'select',
    'firstname',
    'lastname',
    'username',
    'email',
    'level',
    'xp',
    'streakDays',
    'status',
    'actions',
  ];

  data: any[] = []; // Leverages StudentResponse variations
  selection = new SelectionModel<any>(true, []);

  detailSnapshot: StudentDetailResponse | null = null;

  isLoading = true;
  loadError: string | null = null;
  pageSize = 10;
  readonly pageSizeOptions: number[] = [5, 10, 25, 50];
  breadCrumbItems!: Array<{ label?: string; active?: boolean }>;
  meta: ListMeta | null = null;

  pageTitle = 'Student Registry';
  isPlatformRoute = false;

  // Search Filter inputs matching FindStudentsQuery[cite: 6]
  firstnameFilter = new FormControl('', { nonNullable: true });
  lastnameFilter = new FormControl('', { nonNullable: true });
  usernameFilter = new FormControl('', { nonNullable: true });
  emailFilter = new FormControl('', { nonNullable: true });
  levelFilter = new FormControl<number | ''>('', { nonNullable: true });
  tenantUuidFilter = new FormControl('', { nonNullable: true });

  private readonly destroy$ = new Subject<void>();
  private readonly refresh$ = new Subject<void>();

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  @ViewChild('studentDetailModal') studentDetailModalTpl!: TemplateRef<unknown>;

  constructor(
    private readonly studentsApi: StudentsApiService,
    private readonly modalService: NgbModal,
    private readonly permissionService: PermissionService,
    private readonly route: ActivatedRoute,
    private readonly common: CommonService
  ) {}

  ngOnInit(): void {
    // Determine scope perspective[cite: 3]
    this.isPlatformRoute = this.route.snapshot.data['scope'] === 'platform';
    this.pageTitle = this.isPlatformRoute ? 'Global Student Registry' : 'Student Accounts';

    this.breadCrumbItems = [
      { label: 'Learncore' },
      { label: this.pageTitle, active: true },
    ];
  }

  ngAfterViewInit(): void {
    this.sort.active = 'lastname';
    this.sort.direction = 'asc';

    // Hook inputs to debounce pipeline[cite: 3]
    merge(
      this.refresh$,
      this.firstnameFilter.valueChanges.pipe(debounceTime(300)),
      this.lastnameFilter.valueChanges.pipe(debounceTime(300)),
      this.usernameFilter.valueChanges.pipe(debounceTime(300)),
      this.emailFilter.valueChanges.pipe(debounceTime(300)),
      this.levelFilter.valueChanges.pipe(debounceTime(300)),
      this.tenantUuidFilter.valueChanges.pipe(debounceTime(300)),
      this.sort.sortChange,
      this.paginator.page
    )
      .pipe(startWith(undefined), takeUntil(this.destroy$))
      .subscribe(() => this.loadStudents());
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  get canUpdateStudent(): boolean {
    return this.isPlatformRoute
      ? this.permissionService.can(PermissionCodes.PLATFORM_ROLE_CREATE)
      : this.permissionService.can(PermissionCodes.ADMIN_ROLE_CREATE);
  }

  isAllSelected(): boolean {
    return this.common.isAllSelected(this.selection, this.data);
  }

  masterToggle(): void {
    this.common.masterToggle(this.selection, this.data);
  }

  toggleRow(row: any): void {
    this.selection.toggle(row);
  }

  refresh(): void {
    this.refresh$.next();
  }

  clearFilters(): void {
    this.firstnameFilter.setValue('');
    this.lastnameFilter.setValue('');
    this.usernameFilter.setValue('');
    this.emailFilter.setValue('');
    this.levelFilter.setValue('');
    this.tenantUuidFilter.setValue('');
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

  shortUuid(uuid?: string): string {
    return this.common.shortUuid(uuid);
  }

  private loadStudents(): void {
    if (!this.paginator || !this.sort) { return; }

    this.isLoading = true;
    this.loadError = null;
    this.selection.clear();

    const query = this.buildQuery(this.sort, this.paginator.pageIndex, this.pageSize);
    this.studentsApi
      .getStudents(query)
      .pipe(finalize(() => (this.isLoading = false)))
      .subscribe({
        next: (page: PageStudentResponse) => {
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
          this.loadError = getApiErrorMessage(err, 'Failed to fetch student registry data.');
        },
      });
  }

  private buildQuery(sort: Sort, pageIndex: number, size: number): FindStudentsQuery {
    const direction = sort.direction === 'desc' ? 'DESC' : 'ASC';
    const lvlVal = this.levelFilter.value;

    return {
      page: pageIndex,
      size,
      sortBy: sort.active || 'lastname',
      sortDirection: direction,
      firstname: this.firstnameFilter.value.trim() || undefined,
      lastname: this.lastnameFilter.value.trim() || undefined,
      username: this.usernameFilter.value.trim() || undefined,
      email: this.emailFilter.value.trim() || undefined,
      level: lvlVal !== '' ? lvlVal : undefined,
      tenantUuid: this.isPlatformRoute ? (this.tenantUuidFilter.value.trim() || undefined) : undefined,
    };
  }

  onView(row: any): void {
    if (!row.uuid) { return; }
    this.isLoading = true;

    this.studentsApi.getStudentDetails(row.uuid)
      .pipe(finalize(() => this.isLoading = false))
      .subscribe({
        next: (details: StudentDetailResponse) => {
          this.detailSnapshot = details;
          this.common.openModal(this.modalService, this.studentDetailModalTpl);
        },
        error: (err) => {
          this.common.showErrorAlert('Fetch failed', getApiErrorMessage(err, 'Could not load student detailed ledger.'));
        },
      });
  }

  onToggleStatus(row: any): void {
    if (!row.uuid) { return; }
    this.studentsApi.changeStudentStatus(row.uuid).subscribe({
      next: () => {
        this.refresh();
      },
      error: (err) => {
        this.common.showErrorAlert('Operation failed', getApiErrorMessage(err, 'Unable to alter student active system status.'));
      },
    });
  }

  printStudentCard(): void {
    const s = this.detailSnapshot;
    if (!s) { return; }
    const rows: [string, string][] = [
      ['Student UUID', s.uuid ?? ''],
      ['Name', `${s.firstname} ${s.lastname}`],
      ['Username', `@${s.username}`],
      ['Email', s.email ?? ''],
      ['Tenant Context', s.tenantUuid ?? '—'],
      ['Gamified Level', `Lvl ${s.level ?? 1}`],
      ['XP Points', `${s.xp ?? 0}`],
      ['Active Streak', `${s.streakDays ?? 0} active days`],
      ['Access Status', s.accountLocked ? 'Suspended' : 'Active'],
    ];
    this.common.printHtmlTable('Student Record Sheet', rows);
  }

  private getExportRows(rows: any[]): string[][] {
    const header = ['UUID', 'First Name', 'Last Name', 'Username', 'Email', 'Level', 'XP', 'Streak', 'Status'];
    const body = rows.map((r) => [
      r.uuid ?? '',
      r.firstname ?? '',
      r.lastname ?? '',
      r.username ?? '',
      r.email ?? '',
      (r.level ?? 1).toString(),
      (r.xp ?? 0).toString(),
      (r.streakDays ?? 0).toString(),
      r.accountLocked ? 'Suspended' : 'Active',
    ]);
    return [header, ...body];
  }

  exportCsv(): void { this.common.exportCsv(this.getExportRows(this.data), 'students'); }
  exportExcel(): void { this.common.exportExcel(this.getExportRows(this.data), 'students'); }
  exportSelectedCsv(): void {
    if (!this.selection.selected.length) return;
    this.common.exportSelectedCsv(this.getExportRows(this.selection.selected), 'students');
  }
  exportPdf(): void {
    this.common.printDataTable({ title: this.pageTitle, table: this.getExportRows(this.data), metaLine: this.common.getExportMetaLine(this.meta), forPdfHint: true });
  }
  printTable(): void {
    this.common.printDataTable({ title: this.pageTitle, table: this.getExportRows(this.data), metaLine: this.common.getExportMetaLine(this.meta) });
  }
}