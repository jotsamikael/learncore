import { SelectionModel } from '@angular/cdk/collections';
import {
  AfterViewInit,
  Component,
  OnDestroy,
  OnInit,
  TemplateRef,
  ViewChild,
} from '@angular/core';

import {
  FormControl,
  FormGroup,
  ReactiveFormsModule,
} from '@angular/forms';

import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatMenuModule } from '@angular/material/menu';
import {
  MatPaginator,
  MatPaginatorModule,
  PageEvent,
} from '@angular/material/paginator';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import {
  MatSort,
  MatSortModule,
  Sort,
} from '@angular/material/sort';
import { MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';

import {
  NgbDropdownModule,
  NgbModal,
  NgbModalRef,
  NgbModule,
} from '@ng-bootstrap/ng-bootstrap';

import {
  Subject,
  debounceTime,
  finalize,
  merge,
  startWith,
  takeUntil,
} from 'rxjs';

import { CommonService, ListMeta } from '../../../core/services/common.service';
import { PermissionCodes } from '../../../core/constants/permission-codes';
import { GlobalFormBuilderService } from '../../../core/services/globalFormBuilder.service';
import { LanguagesApiService } from '../../../core/services/languages-api.service';
import { PermissionService } from '../../../core/services/permission.service';
import { getApiErrorMessage } from '../../../core/utils/api-error.utils';

import { CreateLanguageRequest } from '../../../learncoreservices/models/create-language-request';
import { FindLanguagesQuery } from '../../../learncoreservices/models/find-languages-query';
import { LanguageResponse } from '../../../learncoreservices/models/language-response';
import { UpdateLanguageRequest } from '../../../learncoreservices/models/update-language-request';

import { SharedModule } from '../../../shared/shared.module';

@Component({
  selector: 'app-languages',
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
  templateUrl: './language.component.html',
  styleUrl: './language.component.scss',
})
export class LanguagesComponent
  implements OnInit, AfterViewInit, OnDestroy
{
  pageTitle = 'Languages';

  breadCrumbItems: Array<{
    label?: string;
    active?: boolean;
  }> = [];

  /*
   * Forms
   */
  createForm!: FormGroup;
  detailForm!: FormGroup;

  /*
   * Create state
   */
  createFormSubmitted = false;
  createFormError = '';
  createSubmitting = false;

  /*
   * Detail / update state
   */
  detailFormSubmitted = false;
  detailError = '';
  detailSaving = false;
  detailReadOnly = true;

  detailSnapshot: LanguageResponse | null = null;
  detailLanguageUuid: string | null = null;

  /*
   * Table
   */
  displayedColumns: string[] = [
    'select',
    'name',
    'code',
    'uuid',
    'actions',
  ];

  data: LanguageResponse[] = [];

  selection = new SelectionModel<LanguageResponse>(
    true,
    []
  );

  /*
   * Loading / pagination
   */
  isLoading = true;
  loadError: string | null = null;

  meta: ListMeta | null = null;

  pageSize = 10;

  readonly pageSizeOptions: number[] = [
    5,
    10,
    25,
    50,
  ];

  /*
   * Filters
   */
  nameFilter = new FormControl('', {
    nonNullable: true,
  });

  codeFilter = new FormControl('', {
    nonNullable: true,
  });

  /*
   * RxJS
   */
  private readonly destroy$ =
    new Subject<void>();

  private readonly refresh$ =
    new Subject<void>();

  /*
   * View refs
   */
  @ViewChild(MatPaginator)
  paginator!: MatPaginator;

  @ViewChild(MatSort)
  sort!: MatSort;

  @ViewChild('languageDetailModal')
  languageDetailModalTpl!: TemplateRef<unknown>;

  constructor(
    private readonly formService: GlobalFormBuilderService,
    private readonly languagesApi: LanguagesApiService,
    private readonly modalService: NgbModal,
    private readonly permissionService: PermissionService,
    private readonly common: CommonService
  ) {}

  /*
   * Lifecycle
   */

  ngOnInit(): void {
    this.breadCrumbItems = [
      {
        label: 'Learncore',
      },
      {
        label: this.pageTitle,
        active: true,
      },
    ];

    this.createForm =
      this.formService.createLanguageForm();

    this.detailForm =
      this.formService.updateLanguageForm();
  }

  ngAfterViewInit(): void {
    this.sort.active = 'name';
    this.sort.direction = 'asc';

    merge(
      this.refresh$,

      this.nameFilter.valueChanges.pipe(
        debounceTime(300)
      ),

      this.codeFilter.valueChanges.pipe(
        debounceTime(300)
      ),

      this.sort.sortChange,

      this.paginator.page
    )
      .pipe(
        startWith(undefined),
        takeUntil(this.destroy$)
      )
      .subscribe(() => {
        this.loadLanguages();
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  /*
   * Forms
   */

  get cf() {
    return this.createForm.controls;
  }

  get df() {
    return this.detailForm.controls;
  }

  get canCreateLanguage(): boolean {
    return this.permissionService.can(
      PermissionCodes.TENANT_LANGUAGE_CREATE
    );
  }

  get canUpdateLanguage(): boolean {
    return this.permissionService.can(
      PermissionCodes.TENANT_LANGUAGE_UPDATE
    );
  }

  get canDeleteLanguage(): boolean {
    return this.permissionService.can(
      PermissionCodes.TENANT_LANGUAGE_DELETE
    );
  }

  /*
   * Selection
   */

  isAllSelected(): boolean {
    return this.common.isAllSelected(
      this.selection,
      this.data
    );
  }

  masterToggle(): void {
    this.common.masterToggle(
      this.selection,
      this.data
    );
  }

  toggleRow(
    row: LanguageResponse
  ): void {
    this.selection.toggle(row);
  }

  /*
   * Pagination / filters
   */

  refresh(): void {
    this.refresh$.next();
  }

  clearFilters(): void {
    this.nameFilter.setValue('');
    this.codeFilter.setValue('');

    if (this.paginator) {
      this.paginator.firstPage();
    }
  }

  onPaginatorPage(
    _event: PageEvent
  ): void {
    // Handled by merge subscription.
  }

  onPageSizeChange(
    next: number
  ): void {
    if (
      next === this.pageSize ||
      !this.paginator
    ) {
      return;
    }

    this.pageSize = next;
    this.paginator.pageIndex = 0;

    this.refresh();
  }

  /*
   * Helpers
   */

  shortUuid(
    uuid?: string
  ): string {
    return this.common.shortUuid(uuid);
  }

  /*
   * Create
   */

  openCreateModal(
    content: TemplateRef<unknown>
  ): void {
    this.createFormError = '';
    this.createFormSubmitted = false;

    this.common.openCreateModal(
      this.modalService,
      content,
      this.createForm,
      {
        name: '',
        code: '',
      }
    );
  }

  submitCreate(
    modal: NgbModalRef
  ): void {
    this.createFormSubmitted = true;
    this.createFormError = '';

    if (this.createForm.invalid) {
      this.createForm.markAllAsTouched();
      return;
    }

    const raw =
      this.createForm.getRawValue();

    const body: CreateLanguageRequest = {
      name: String(raw.name).trim(),
      code: String(raw.code)
        .trim()
        .toLowerCase(),
    };

    this.createSubmitting = true;

    this.languagesApi
      .createLanguage(body)
      .pipe(
        finalize(
          () =>
            (this.createSubmitting = false)
        )
      )
      .subscribe({
        next: () => {
          modal.close();

          this.createFormSubmitted = false;

          this.refresh();
        },

        error: (err) => {
          this.createFormError =
            getApiErrorMessage(
              err,
              'Failed to create language.'
            );
        },
      });
  }

  /*
   * View / edit
   */

  onView(
    row: LanguageResponse
  ): void {
    this.openLanguageDetailModal(
      row,
      false
    );
  }

  onEdit(
    row: LanguageResponse
  ): void {
    this.openLanguageDetailModal(
      row,
      true
    );
  }

  openLanguageDetailModal(
    row: LanguageResponse,
    startInEditMode: boolean
  ): void {
    this.detailError = '';
    this.detailFormSubmitted = false;

    if (!row.uuid) {
      this.detailSnapshot = null;
      this.detailLanguageUuid = null;

      this.detailError =
        'This language has no public id (uuid).';

      this.common.openModal(
        this.modalService,
        this.languageDetailModalTpl
      );

      return;
    }

    this.detailSnapshot = row;
    this.detailLanguageUuid = row.uuid;

    this.detailReadOnly =
      !startInEditMode ||
      !this.canUpdateLanguage;

    this.patchDetailForm(row);

    this.common.openModal(
      this.modalService,
      this.languageDetailModalTpl
    );

    /*
     * Fetch authoritative record.
     */
    this.languagesApi
      .getLanguage(row.uuid)
      .subscribe({
        next: (language) => {
          this.detailSnapshot =
            language;

          this.patchDetailForm(
            language
          );
        },

        error: (err) => {
          this.detailError =
            getApiErrorMessage(
              err,
              'Failed to load language details.'
            );
        },
      });
  }

  toggleDetailEditMode(): void {
    const nextReadOnly =
      this.common.toggleDetailEditMode({
        readOnly:
          this.detailReadOnly,

        snapshot:
          this.detailSnapshot,

        saving:
          this.detailSaving,

        canEdit:
          this.canUpdateLanguage,

        form:
          this.detailForm,

        restoreForm: () =>
          this.patchDetailForm(
            this.detailSnapshot!
          ),

        onSubmittedReset: () =>
          (this.detailFormSubmitted =
            false),

        onErrorClear: () =>
          (this.detailError = ''),
      });

    if (nextReadOnly !== null) {
      this.detailReadOnly =
        nextReadOnly;
    }
  }

  submitDetailUpdate(
    modal: NgbModalRef
  ): void {
    this.detailFormSubmitted = true;
    this.detailError = '';

    if (
      !this.detailLanguageUuid ||
      this.detailForm.invalid
    ) {
      this.detailForm.markAllAsTouched();
      return;
    }

    const raw =
      this.detailForm.getRawValue();

    const body: UpdateLanguageRequest = {
      name: String(raw.name).trim(),

      code: String(raw.code)
        .trim()
        .toLowerCase(),
    };

    this.detailSaving = true;

    this.languagesApi
      .updateLanguage(
        this.detailLanguageUuid,
        body
      )
      .pipe(
        finalize(
          () =>
            (this.detailSaving = false)
        )
      )
      .subscribe({
        next: (updated) => {
          this.detailSnapshot =
            updated;

          this.detailReadOnly = true;

          this.detailFormSubmitted =
            false;

          modal.close();

          this.refresh();
        },

        error: (err) => {
          this.detailError =
            getApiErrorMessage(
              err,
              'Failed to update language.'
            );
        },
      });
  }

  /*
   * Delete
   */

  async onDeleteLanguage(
    row: LanguageResponse
  ): Promise<void> {
    if (
      !row.uuid ||
      !this.canDeleteLanguage
    ) {
      return;
    }

    const confirmed =
      await this.common.confirmDelete(
        'Delete language?',
        `"${row.name}" will be removed permanently.`
      );

    if (!confirmed) {
      return;
    }

    this.languagesApi
      .deleteLanguage(row.uuid)
      .subscribe({
        next: () => {
          this.selection.deselect(row);
          this.refresh();
        },

        error: (err) => {
          this.common.showErrorAlert(
            'Error',
            getApiErrorMessage(
              err,
              'Failed to delete language.'
            )
          );
        },
      });
  }

  /*
   * Export
   */

  exportCsv(): void {
    this.common.exportCsv(
      this.getExportRows(this.data),
      'languages'
    );
  }

  exportExcel(): void {
    this.common.exportExcel(
      this.getExportRows(this.data),
      'languages'
    );
  }

  exportSelectedCsv(): void {
    const rows =
      this.selection.selected;

    if (!rows.length) {
      return;
    }

    this.common.exportSelectedCsv(
      this.getExportRows(rows),
      'languages'
    );
  }

  exportPdf(): void {
    this.common.printDataTable({
      title: this.pageTitle,

      table:
        this.getExportRows(
          this.data
        ),

      metaLine:
        this.common.getExportMetaLine(
          this.meta
        ),

      forPdfHint: true,
    });
  }

  printTable(): void {
    this.common.printDataTable({
      title: this.pageTitle,

      table:
        this.getExportRows(
          this.data
        ),

      metaLine:
        this.common.getExportMetaLine(
          this.meta
        ),
    });
  }

  printLanguageDetail(): void {
    const row =
      this.detailSnapshot;

    if (!row) {
      return;
    }

    const rows: [
      string,
      string
    ][] = [
      [
        'UUID',
        row.uuid ?? '',
      ],
      [
        'Name',
        row.name ?? '',
      ],
      [
        'Code',
        row.code ?? '',
      ],
    ];

    this.common.printHtmlTable(
      'Language',
      rows
    );
  }

  /*
   * Data
   */

  private loadLanguages(): void {
    if (
      !this.paginator ||
      !this.sort
    ) {
      return;
    }

    this.isLoading = true;
    this.loadError = null;

    this.selection.clear();

    const query =
      this.buildQuery(
        this.sort,
        this.paginator.pageIndex,
        this.pageSize
      );

    this.languagesApi
      .getLanguages(query)
      .pipe(
        finalize(
          () =>
            (this.isLoading = false)
        )
      )
      .subscribe({
        next: (page) => {
          this.data =
            page.content ?? [];

          this.meta = {
            totalItems:
              page.totalElements ?? 0,

            totalPages:
              page.totalPages ?? 0,

            currentPage:
              (page.number ?? 0) + 1,
          };
        },

        error: (err) => {
          this.data = [];
          this.meta = null;

          this.loadError =
            getApiErrorMessage(
              err,
              'Failed to load languages.'
            );
        },
      });
  }

  /*
   * Query
   */

  private buildQuery(
    sort: Sort,
    pageIndex: number,
    size: number
  ): FindLanguagesQuery {
    const sortByMap: Record<
      string,
      string
    > = {
      name: 'name',
      code: 'code',
    };

    const sortBy =
      sortByMap[sort.active] ??
      'name';

    const sortDirection =
      sort.direction === 'desc'
        ? 'DESC'
        : 'ASC';

    return {
      page:
        pageIndex,

      size,

      sortBy,

      sortDirection,

      name:
        this.nameFilter.value
          .trim() ||
        undefined,

      code:
        this.codeFilter.value
          .trim() ||
        undefined,
    };
  }

  /*
   * Detail form
   */

  private patchDetailForm(
    row: LanguageResponse
  ): void {
    this.common.patchDetailForm(
      this.detailForm,
      {
        name:
          row.name ?? '',

        code:
          row.code ?? '',
      },
      this.detailReadOnly
    );
  }

  /*
   * Export mapping
   */

  private getExportRows(
    rows: LanguageResponse[]
  ): string[][] {
    const header = [
      'UUID',
      'Name',
      'Code',
    ];

    const body =
      rows.map((row) => [
        row.uuid ?? '',
        row.name ?? '',
        row.code ?? '',
      ]);

    return [
      header,
      ...body,
    ];
  }
}