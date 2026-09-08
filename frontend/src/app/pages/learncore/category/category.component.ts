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

import { CategoriesApiService } from '../../../core/services/categories-api.service';
import { PermissionCodes } from '../../../core/constants/permission-codes';
import {
  CommonService,
  ListMeta,
} from '../../../core/services/common.service';
import { GlobalFormBuilderService } from '../../../core/services/globalFormBuilder.service';
import { PermissionService } from '../../../core/services/permission.service';
import { getApiErrorMessage } from '../../../core/utils/api-error.utils';

import { CategoryResponse } from '../../../learncoreservices/models/category-response';
import { CreateCategoryForm } from '../../../learncoreservices/models/create-category-form';
import { FindCategoriesQuery } from '../../../learncoreservices/models/find-categories-query';
import { UpdateCategoryForm } from '../../../learncoreservices/models/update-category-form';

import { SharedModule } from '../../../shared/shared.module';

@Component({
  selector: 'app-category',
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
  templateUrl: './category.component.html',
  styleUrl: './category.component.scss',
})
export class CategoryComponent
  implements OnInit, AfterViewInit, OnDestroy
{
  pageTitle = 'Categories';

  breadCrumbItems: Array<{
    label?: string;
    active?: boolean;
  }> = [];

  /*
   * ------------------------------------------------------------------
   * Forms
   * ------------------------------------------------------------------
   */

  createForm!: FormGroup;
  detailForm!: FormGroup;

  createFormSubmitted = false;
  createFormError = '';
  createSubmitting = false;

  detailFormSubmitted = false;
  detailError = '';
  detailSaving = false;
  detailReadOnly = true;

  /*
   * ------------------------------------------------------------------
   * Images
   * ------------------------------------------------------------------
   */

  createImage: File | null = null;
  createImagePreview: string | null = null;

  detailImage: File | null = null;
  detailImagePreview: string | null = null;

  /*
   * ------------------------------------------------------------------
   * Detail
   * ------------------------------------------------------------------
   */

  detailSnapshot: CategoryResponse | null = null;
  detailCategoryUuid: string | null = null;

  /*
   * ------------------------------------------------------------------
   * Table
   * ------------------------------------------------------------------
   */

  displayedColumns: string[] = [
    'select',
    'image',
    'name',
    'slug',
    'parent',
    'language',
    'description',
    'actions',
  ];

  data: CategoryResponse[] = [];

  selection = new SelectionModel<CategoryResponse>(
    true,
    []
  );

  /*
   * Parent category list used by create/update forms.
   *
   * For MVP this loads up to 500 categories.
   * If the category tree becomes large, replace this with
   * an autocomplete/search endpoint.
   */
  parentCategories: CategoryResponse[] = [];
  parentCategoriesLoading = false;

  /*
   * ------------------------------------------------------------------
   * List state
   * ------------------------------------------------------------------
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
   * ------------------------------------------------------------------
   * Filters
   * ------------------------------------------------------------------
   */

  nameFilter = new FormControl('', {
    nonNullable: true,
  });

  descriptionFilter = new FormControl('', {
    nonNullable: true,
  });

  /*
   * ------------------------------------------------------------------
   * RxJS
   * ------------------------------------------------------------------
   */

  private readonly destroy$ =
    new Subject<void>();

  private readonly refresh$ =
    new Subject<void>();

  /*
   * ------------------------------------------------------------------
   * Material / modal refs
   * ------------------------------------------------------------------
   */

  @ViewChild(MatPaginator)
  paginator!: MatPaginator;

  @ViewChild(MatSort)
  sort!: MatSort;

  @ViewChild('categoryDetailModal')
  categoryDetailModalTpl!: TemplateRef<unknown>;

  constructor(
    private readonly formService:
      GlobalFormBuilderService,

    private readonly categoriesApi:
      CategoriesApiService,

    private readonly modalService:
      NgbModal,

    private readonly permissionService:
      PermissionService,

    private readonly common:
      CommonService
  ) {}

  /*
   * ------------------------------------------------------------------
   * Lifecycle
   * ------------------------------------------------------------------
   */

  ngOnInit(): void {
    this.breadCrumbItems = [
      {
        label: 'Learncore',
      },
      {
        label: 'Categories',
        active: true,
      },
    ];

    this.createForm =
      this.formService.createCategoryForm();

    this.detailForm =
      this.formService.updateCategoryForm();

    this.loadParentCategories();
  }

  ngAfterViewInit(): void {
    this.sort.active = 'name';
    this.sort.direction = 'asc';

    merge(
      this.refresh$,

      this.nameFilter.valueChanges.pipe(
        debounceTime(300)
      ),

      this.descriptionFilter.valueChanges.pipe(
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
        this.loadCategories();
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();

    this.revokeImagePreview(
      this.createImagePreview
    );

    this.revokeImagePreview(
      this.detailImagePreview
    );
  }

  /*
   * ------------------------------------------------------------------
   * Form helpers
   * ------------------------------------------------------------------
   */

  get cf() {
    return this.createForm.controls;
  }

  get df() {
    return this.detailForm.controls;
  }

  get canCreateCategory(): boolean {
    return this.permissionService.can(
      PermissionCodes.TENANT_CATEGORY_CREATE
    );
  }

  get canUpdateCategory(): boolean {
    return this.permissionService.can(
      PermissionCodes.TENANT_CATEGORY_UPDATE
    );
  }

  get canDeleteCategory(): boolean {
    return this.permissionService.can(
      PermissionCodes.TENANT_CATEGORY_DELETE
    );
  }

  /*
   * ------------------------------------------------------------------
   * Selection
   * ------------------------------------------------------------------
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
    row: CategoryResponse
  ): void {
    this.selection.toggle(row);
  }

  /*
   * ------------------------------------------------------------------
   * Filters / pagination
   * ------------------------------------------------------------------
   */

  refresh(): void {
    this.refresh$.next();
  }

  clearFilters(): void {
    this.nameFilter.setValue('');
    this.descriptionFilter.setValue('');

    if (this.paginator) {
      this.paginator.firstPage();
    }
  }

  onPaginatorPage(
    _event: PageEvent
  ): void {
    // Handled by merge() subscription.
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
   * ------------------------------------------------------------------
   * Display helpers
   * ------------------------------------------------------------------
   */

  shortUuid(uuid?: string): string {
    return this.common.shortUuid(uuid);
  }

  parentLabel(
    row: CategoryResponse
  ): string {
    return row.parentName || 'Root category';
  }

  languageLabel(
    row: CategoryResponse
  ): string {
    return row.languageCode || '—';
  }

  /*
   * ------------------------------------------------------------------
   * Create modal
   * ------------------------------------------------------------------
   */

  openCreateModal(
    content: TemplateRef<unknown>
  ): void {
    this.createFormError = '';
    this.createFormSubmitted = false;

    this.createImage = null;

    this.revokeImagePreview(
      this.createImagePreview
    );

    this.createImagePreview = null;

    this.common.openCreateModal(
      this.modalService,
      content,
      this.createForm,
      {
        name: '',
        slug: '',
        description: '',
        parentUuid: null,
        languageUuid: null,
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

    const body: CreateCategoryForm = {
      name:
        String(raw.name).trim(),

      slug:
        String(raw.slug).trim(),

      description:
        String(raw.description).trim(),

      parentUuid:
        raw.parentUuid || undefined,

      languageUuid:
        raw.languageUuid || undefined,
    };

    this.createSubmitting = true;

    this.categoriesApi
      .createCategory(
        body,
        this.createImage
      )
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

          this.loadParentCategories();

          this.refresh();
        },

        error: (err) => {
          this.createFormError =
            getApiErrorMessage(
              err,
              'Failed to create category.'
            );
        },
      });
  }

  /*
   * ------------------------------------------------------------------
   * Detail / edit
   * ------------------------------------------------------------------
   */

  onView(
    row: CategoryResponse
  ): void {
    this.openCategoryDetailModal(
      row,
      false
    );
  }

  onEdit(
    row: CategoryResponse
  ): void {
    this.openCategoryDetailModal(
      row,
      true
    );
  }

  openCategoryDetailModal(
    row: CategoryResponse,
    startInEditMode: boolean
  ): void {
    this.detailError = '';
    this.detailFormSubmitted = false;

    this.detailImage = null;

    this.revokeImagePreview(
      this.detailImagePreview
    );

    this.detailImagePreview =
      row.imageUrl ?? null;

    if (!row.uuid) {
      this.detailSnapshot = null;
      this.detailCategoryUuid = null;

      this.detailError =
        'This category has no public id (uuid).';

      this.common.openModal(
        this.modalService,
        this.categoryDetailModalTpl
      );

      return;
    }

    this.detailSnapshot = row;
    this.detailCategoryUuid = row.uuid;

    this.detailReadOnly =
      !startInEditMode ||
      !this.canUpdateCategory;

    this.patchDetailForm(row);

    this.common.openModal(
      this.modalService,
      this.categoryDetailModalTpl
    );

    /*
     * Fetch authoritative detail.
     */
    this.categoriesApi
      .getCategory(row.uuid)
      .subscribe({
        next: (category) => {
          this.detailSnapshot = category;

          if (!this.detailImage) {
            this.detailImagePreview =
              category.imageUrl ?? null;
          }

          this.patchDetailForm(category);
        },

        error: (err) => {
          this.detailError =
            getApiErrorMessage(
              err,
              'Failed to load category details.'
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
          this.canUpdateCategory,

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
      !this.detailCategoryUuid ||
      this.detailForm.invalid
    ) {
      this.detailForm.markAllAsTouched();
      return;
    }

    const raw =
      this.detailForm.getRawValue();

    const body: UpdateCategoryForm = {
      name:
        String(raw.name).trim(),

      slug:
        String(raw.slug).trim(),

      description:
        String(raw.description).trim(),

      parentUuid:
        raw.parentUuid || undefined,

      languageUuid:
        raw.languageUuid || undefined,
    };

    this.detailSaving = true;

    this.categoriesApi
      .updateCategory(
        this.detailCategoryUuid,
        body,
        this.detailImage
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

          this.detailReadOnly =
            true;

          this.detailFormSubmitted =
            false;

          modal.close();

          this.loadParentCategories();

          this.refresh();
        },

        error: (err) => {
          this.detailError =
            getApiErrorMessage(
              err,
              'Failed to update category.'
            );
        },
      });
  }

  /*
   * ------------------------------------------------------------------
   * Delete
   * ------------------------------------------------------------------
   */

  async onDeleteCategory(
    row: CategoryResponse
  ): Promise<void> {
    if (
      !row.uuid ||
      !this.canDeleteCategory
    ) {
      return;
    }

    const confirmed =
      await this.common.confirmDelete(
        'Delete category?',
        `"${row.name}" will be removed permanently.`
      );

    if (!confirmed) {
      return;
    }

    this.categoriesApi
      .deleteCategory(row.uuid)
      .subscribe({
        next: () => {
          this.selection.deselect(row);

          this.loadParentCategories();

          this.refresh();
        },

        error: (err) => {
          this.common.showErrorAlert(
            'Error',
            getApiErrorMessage(
              err,
              'Failed to delete category.'
            )
          );
        },
      });
  }

  /*
   * ------------------------------------------------------------------
   * Image handling
   * ------------------------------------------------------------------
   */

  onCreateImageSelected(
    event: Event
  ): void {
    const input =
      event.target as HTMLInputElement;

    const file =
      input.files?.[0] ?? null;

    this.createImage = file;

    this.revokeImagePreview(
      this.createImagePreview
    );

    this.createImagePreview =
      file
        ? URL.createObjectURL(file)
        : null;
  }

  onDetailImageSelected(
    event: Event
  ): void {
    const input =
      event.target as HTMLInputElement;

    const file =
      input.files?.[0] ?? null;

    this.detailImage = file;

    if (
      this.detailImagePreview &&
      this.detailImagePreview.startsWith(
        'blob:'
      )
    ) {
      URL.revokeObjectURL(
        this.detailImagePreview
      );
    }

    this.detailImagePreview =
      file
        ? URL.createObjectURL(file)
        : this.detailSnapshot
            ?.imageUrl ??
          null;
  }

  removeCreateImage(): void {
    this.createImage = null;

    this.revokeImagePreview(
      this.createImagePreview
    );

    this.createImagePreview = null;
  }

  removeDetailSelectedImage(): void {
    this.detailImage = null;

    this.revokeImagePreview(
      this.detailImagePreview
    );

    this.detailImagePreview =
      this.detailSnapshot
        ?.imageUrl ??
      null;
  }

  /*
   * ------------------------------------------------------------------
   * Export
   * ------------------------------------------------------------------
   */

  exportCsv(): void {
    this.common.exportCsv(
      this.getExportRows(this.data),
      'categories'
    );
  }

  exportExcel(): void {
    this.common.exportExcel(
      this.getExportRows(this.data),
      'categories'
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
      'categories'
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

  printCategoryDetail(): void {
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
        'Slug',
        row.slug ?? '',
      ],
      [
        'Description',
        row.description ?? '',
      ],
      [
        'Parent category',
        row.parentName ??
          'Root category',
      ],
      [
        'Parent UUID',
        row.parentUuid ?? '—',
      ],
      [
        'Language',
        row.languageCode ?? '—',
      ],
      [
        'Language UUID',
        row.languageUuid ?? '—',
      ],
      [
        'Image URL',
        row.imageUrl ?? '—',
      ],
    ];

    this.common.printHtmlTable(
      'Category',
      rows
    );
  }

  /*
   * ------------------------------------------------------------------
   * Data loading
   * ------------------------------------------------------------------
   */

  private loadCategories(): void {
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

    this.categoriesApi
      .getCategories(query)
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
              'Failed to load categories.'
            );
        },
      });
  }

  private loadParentCategories(): void {
    this.parentCategoriesLoading =
      true;

    this.categoriesApi
      .getCategories({
        page: 0,
        size: 500,
        sortBy: 'name',
        sortDirection: 'ASC',
      })
      .pipe(
        finalize(
          () =>
            (this.parentCategoriesLoading =
              false)
        )
      )
      .subscribe({
        next: (page) => {
          this.parentCategories =
            page.content ?? [];
        },

        error: () => {
          this.parentCategories = [];
        },
      });
  }

  /*
   * ------------------------------------------------------------------
   * Query
   * ------------------------------------------------------------------
   */

  private buildQuery(
    sort: Sort,
    pageIndex: number,
    size: number
  ): FindCategoriesQuery {
    const sortByMap: Record<
      string,
      string
    > = {
      name: 'name',
      slug: 'slug',
      description:
        'description',
    };

    const sortBy =
      sortByMap[sort.active] ??
      'name';

    const sortDirection =
      sort.direction === 'desc'
        ? 'DESC'
        : 'ASC';

    return {
      page: pageIndex,

      size,

      sortBy,

      sortDirection,

      name:
        this.nameFilter.value
          .trim() ||
        undefined,

      description:
        this.descriptionFilter.value
          .trim() ||
        undefined,
    };
  }

  /*
   * ------------------------------------------------------------------
   * Detail form
   * ------------------------------------------------------------------
   */

  private patchDetailForm(
    row: CategoryResponse
  ): void {
    this.common.patchDetailForm(
      this.detailForm,
      {
        name:
          row.name ?? '',

        slug:
          row.slug ?? '',

        description:
          row.description ?? '',

        parentUuid:
          row.parentUuid ?? null,

        languageUuid:
          row.languageUuid ?? null,
      },
      this.detailReadOnly
    );
  }

  /*
   * ------------------------------------------------------------------
   * Export mapping
   * ------------------------------------------------------------------
   */

  private getExportRows(
    rows: CategoryResponse[]
  ): string[][] {
    const header = [
      'UUID',
      'Name',
      'Slug',
      'Description',
      'Parent',
      'Parent UUID',
      'Language',
      'Language UUID',
      'Image URL',
    ];

    const body =
      rows.map((row) => [
        row.uuid ?? '',
        row.name ?? '',
        row.slug ?? '',
        row.description ?? '',
        row.parentName ?? '',
        row.parentUuid ?? '',
        row.languageCode ?? '',
        row.languageUuid ?? '',
        row.imageUrl ?? '',
      ]);

    return [
      header,
      ...body,
    ];
  }

  private revokeImagePreview(
    value: string | null
  ): void {
    if (
      value?.startsWith('blob:')
    ) {
      URL.revokeObjectURL(value);
    }
  }
}