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
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { MatButtonModule } from '@angular/material/button';
import { MatMenuModule } from '@angular/material/menu';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
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
import { CKEditorModule } from '@ckeditor/ckeditor5-angular';
import {
  NgbModal,
  NgbModalRef,
  NgbModule,
  NgbNavModule,
} from '@ng-bootstrap/ng-bootstrap';
import {
  Subject,
  debounceTime,
  finalize,
  merge,
  startWith,
  takeUntil,
} from 'rxjs';

import {
  QuestionEditor,
  questionRichTextEditorConfig,
} from '../../../core/editors/question-rich-text.editor';
import { PermissionCodes } from '../../../core/constants/permission-codes';
import { CategoriesApiService } from '../../../core/services/categories-api.service';
import {
  CommonService,
  ListMeta,
} from '../../../core/services/common.service';
import { GlobalFormBuilderService } from '../../../core/services/globalFormBuilder.service';
import {
  FindLessonsQuery,
  LessonsApiService,
} from '../../../core/services/lessons-api.service';
import { PermissionService } from '../../../core/services/permission.service';
import { getApiErrorMessage } from '../../../core/utils/api-error.utils';
import { CategoryResponse } from '../../../learncoreservices/models/category-response';
import { CreateLessonRequest } from '../../../learncoreservices/models/create-lesson-request';
import { LessonResponse } from '../../../learncoreservices/models/lesson-response';
import { UpdateLessonRequest } from '../../../learncoreservices/models/update-lesson-request';
import { SharedModule } from '../../../shared/shared.module';
import { LessonQuestionsPanelComponent } from './lesson-questions-panel.component';

@Component({
  selector: 'app-lesson',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    SharedModule,
    NgbModule,
    NgbNavModule,
    CKEditorModule,
    MatTableModule,
    MatSortModule,
    MatPaginatorModule,
    MatIconModule,
    MatButtonModule,
    MatProgressSpinnerModule,
    MatTooltipModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatMenuModule,
    LessonQuestionsPanelComponent,
  ],
  templateUrl: './lesson.component.html',
  styleUrl: './lesson.component.scss',
})
export class LessonComponent implements OnInit, AfterViewInit, OnDestroy {
  pageTitle = 'Lessons';

  breadCrumbItems: Array<{ label?: string; active?: boolean }> = [];

  readonly QuestionEditor = QuestionEditor;
  readonly lessonContentEditorConfig = questionRichTextEditorConfig;

  createForm!: FormGroup;
  detailForm!: FormGroup;

  createFormSubmitted = false;
  createFormError = '';
  createSubmitting = false;

  detailFormSubmitted = false;
  detailError = '';
  detailSaving = false;
  detailReadOnly = true;
  detailActiveTab = 1;

  detailSnapshot: LessonResponse | null = null;
  detailLessonUuid: string | null = null;

  selectedLesson: LessonResponse | null = null;

  categories: CategoryResponse[] = [];
  categoriesLoading = false;

  displayedColumns = [
    'position',
    'title',
    'category',
    'readTime',
    'premium',
    'exercises',
    'actions',
  ];

  data: LessonResponse[] = [];
  isLoading = true;
  loadError: string | null = null;
  meta: ListMeta | null = null;
  pageSize = 10;
  readonly pageSizeOptions = [5, 10, 25, 50];

  categoryFilter = new FormControl<string>('', { nonNullable: true });
  titleFilter = new FormControl('', { nonNullable: true });
  premiumFilter = new FormControl<'all' | 'yes' | 'no'>('all', {
    nonNullable: true,
  });

  private readonly destroy$ = new Subject<void>();
  private readonly refresh$ = new Subject<void>();

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  @ViewChild('lessonDetailModal') lessonDetailModalTpl!: TemplateRef<unknown>;

  constructor(
    private readonly formService: GlobalFormBuilderService,
    private readonly lessonsApi: LessonsApiService,
    private readonly categoriesApi: CategoriesApiService,
    private readonly modalService: NgbModal,
    private readonly permissionService: PermissionService,
    private readonly common: CommonService,
    private readonly sanitizer: DomSanitizer
  ) {}

  ngOnInit(): void {
    this.breadCrumbItems = [
      { label: 'Learncore' },
      { label: 'Lessons', active: true },
    ];
    this.createForm = this.formService.createLessonForm();
    this.detailForm = this.formService.updateLessonForm();
    this.loadCategories();
  }

  ngAfterViewInit(): void {
    this.sort.active = 'position';
    this.sort.direction = 'asc';

    merge(
      this.refresh$,
      this.categoryFilter.valueChanges,
      this.titleFilter.valueChanges.pipe(debounceTime(300)),
      this.premiumFilter.valueChanges,
      this.sort.sortChange,
      this.paginator.page
    )
      .pipe(startWith(undefined), takeUntil(this.destroy$))
      .subscribe(() => this.loadLessons());
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

  get canCreateLesson(): boolean {
    return this.permissionService.can(PermissionCodes.TENANT_LESSON_CREATE);
  }

  get canUpdateLesson(): boolean {
    return this.permissionService.can(PermissionCodes.TENANT_LESSON_UPDATE);
  }

  get canDeleteLesson(): boolean {
    return this.permissionService.can(PermissionCodes.TENANT_LESSON_DELETE);
  }

  shortUuid(uuid?: string): string {
    return this.common.shortUuid(uuid);
  }

  detailContentPreview(): SafeHtml {
    const html = this.detailForm.get('content')?.value;
    if (!html) {
      return this.sanitizer.bypassSecurityTrustHtml(
        '<span class="text-muted">—</span>'
      );
    }
    return this.sanitizer.bypassSecurityTrustHtml(String(html));
  }

  stripHtml(html?: string | null): string {
    if (!html) {
      return '—';
    }
    const text =
      new DOMParser().parseFromString(html, 'text/html').body.textContent?.trim() ??
      '';
    return text.length > 80 ? `${text.slice(0, 77)}…` : text || '—';
  }

  refresh(): void {
    this.refresh$.next();
  }

  clearFilters(): void {
    this.categoryFilter.setValue('');
    this.titleFilter.setValue('');
    this.premiumFilter.setValue('all');
    this.paginator?.firstPage();
  }

  onPaginatorPage(_event: PageEvent): void {}

  onPageSizeChange(next: number): void {
    if (next === this.pageSize || !this.paginator) {
      return;
    }
    this.pageSize = next;
    this.paginator.pageIndex = 0;
    this.refresh();
  }

  selectLesson(row: LessonResponse): void {
    this.selectedLesson = row;
  }

  isSelected(row: LessonResponse): boolean {
    return this.selectedLesson?.uuid === row.uuid;
  }

  openCreateModal(content: TemplateRef<unknown>): void {
    this.createFormError = '';
    this.createFormSubmitted = false;
    const defaultCategory = this.categoryFilter.value || null;
    this.common.openCreateModal(
      this.modalService,
      content,
      this.createForm,
      {
        categoryUuid: defaultCategory,
        title: '',
        content: '',
        estimatedReadMinutes: 5,
        position: null,
        isPremium: false,
      },
      { size: 'xl' }
    );
  }

  submitCreate(modal: NgbModalRef): void {
    this.createFormSubmitted = true;
    this.createFormError = '';
    if (this.createForm.invalid) {
      this.createForm.markAllAsTouched();
      return;
    }
    const raw = this.createForm.getRawValue();
    const body: CreateLessonRequest = {
      categoryUuid: String(raw.categoryUuid),
      title: String(raw.title).trim(),
      content: String(raw.content).trim(),
      estimatedReadMinutes: Number(raw.estimatedReadMinutes),
      isPremium: !!raw.isPremium,
    };
    if (raw.position != null && raw.position !== '') {
      body.position = Number(raw.position);
    }
    this.createSubmitting = true;
    this.lessonsApi
      .createLesson(body)
      .pipe(finalize(() => (this.createSubmitting = false)))
      .subscribe({
        next: (created) => {
          modal.close();
          this.createFormSubmitted = false;
          this.selectedLesson = created;
          this.refresh();
        },
        error: (err) => {
          this.createFormError = getApiErrorMessage(
            err,
            'Failed to create lesson.'
          );
        },
      });
  }

  onView(row: LessonResponse): void {
    this.openLessonDetailModal(row, false, 1);
  }

  onManageQuestions(row: LessonResponse): void {
    this.selectLesson(row);
    this.openLessonDetailModal(row, false, 2);
  }

  onEdit(row: LessonResponse): void {
    this.openLessonDetailModal(row, true, 1);
  }

  openLessonDetailModal(
    row: LessonResponse,
    startInEditMode: boolean,
    tab: number
  ): void {
    this.detailError = '';
    this.detailFormSubmitted = false;
    this.detailActiveTab = tab;
    this.selectLesson(row);

    if (!row.uuid) {
      this.detailSnapshot = null;
      this.detailLessonUuid = null;
      this.detailError = 'This lesson has no public id (uuid).';
      this.common.openModal(this.modalService, this.lessonDetailModalTpl);
      return;
    }

    this.detailSnapshot = row;
    this.detailLessonUuid = row.uuid;
    this.detailReadOnly = !startInEditMode || !this.canUpdateLesson;
    this.patchDetailForm(row);
    this.common.openModal(this.modalService, this.lessonDetailModalTpl, {
      size: 'xl',
    });

    this.lessonsApi.getLesson(row.uuid).subscribe({
      next: (lesson) => {
        this.detailSnapshot = lesson;
        this.selectedLesson = lesson;
        this.patchDetailForm(lesson);
      },
      error: (err) => {
        this.detailError = getApiErrorMessage(
          err,
          'Failed to load lesson details.'
        );
      },
    });
  }

  toggleDetailEditMode(): void {
    const nextReadOnly = this.common.toggleDetailEditMode({
      readOnly: this.detailReadOnly,
      snapshot: this.detailSnapshot,
      saving: this.detailSaving,
      canEdit: this.canUpdateLesson,
      form: this.detailForm,
      restoreForm: () =>
        this.patchDetailForm(this.detailSnapshot!),
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
    if (!this.detailLessonUuid || this.detailForm.invalid) {
      this.detailForm.markAllAsTouched();
      return;
    }
    const raw = this.detailForm.getRawValue();
    const body: UpdateLessonRequest = {
      categoryUuid: String(raw.categoryUuid),
      title: String(raw.title).trim(),
      content: String(raw.content).trim(),
      estimatedReadMinutes: Number(raw.estimatedReadMinutes),
      position: Number(raw.position),
      isPremium: !!raw.isPremium,
    };
    this.detailSaving = true;
    this.lessonsApi
      .updateLesson(this.detailLessonUuid, body)
      .pipe(finalize(() => (this.detailSaving = false)))
      .subscribe({
        next: (updated) => {
          this.detailSnapshot = updated;
          this.selectedLesson = updated;
          this.detailReadOnly = true;
          this.detailFormSubmitted = false;
          this.refresh();
        },
        error: (err) => {
          this.detailError = getApiErrorMessage(
            err,
            'Failed to update lesson.'
          );
        },
      });
  }

  async onDeleteLesson(row: LessonResponse): Promise<void> {
    if (!row.uuid || !this.canDeleteLesson) {
      return;
    }
    const confirmed = await this.common.confirmDelete(
      'Delete lesson?',
      `"${row.title}" and its question links will be removed.`
    );
    if (!confirmed) {
      return;
    }
    this.lessonsApi.deleteLesson(row.uuid).subscribe({
      next: () => {
        if (this.selectedLesson?.uuid === row.uuid) {
          this.selectedLesson = null;
        }
        this.refresh();
      },
      error: (err) => {
        this.common.showErrorAlert(
          'Error',
          getApiErrorMessage(err, 'Failed to delete lesson.')
        );
      },
    });
  }

  onQuestionsChanged(): void {
    this.refresh();
    if (this.detailLessonUuid) {
      this.lessonsApi.getLesson(this.detailLessonUuid).subscribe({
        next: (lesson) => {
          this.detailSnapshot = lesson;
          this.selectedLesson = lesson;
        },
      });
    }
  }

  private loadLessons(): void {
    if (!this.paginator || !this.sort) {
      return;
    }
    this.isLoading = true;
    this.loadError = null;

    const query = this.buildQuery(
      this.sort,
      this.paginator.pageIndex,
      this.pageSize
    );

    this.lessonsApi
      .getLessons(query)
      .pipe(finalize(() => (this.isLoading = false)))
      .subscribe({
        next: (page) => {
          this.data = page.content ?? [];
          this.meta = {
            totalItems: page.totalElements ?? 0,
            totalPages: page.totalPages ?? 0,
            currentPage: (page.number ?? 0) + 1,
          };
          if (this.selectedLesson?.uuid) {
            const updated = this.data.find(
              (l) => l.uuid === this.selectedLesson?.uuid
            );
            if (updated) {
              this.selectedLesson = updated;
            }
          }
        },
        error: (err) => {
          this.data = [];
          this.meta = null;
          this.loadError = getApiErrorMessage(
            err,
            'Failed to load lessons.'
          );
        },
      });
  }

  private loadCategories(): void {
    this.categoriesLoading = true;
    this.categoriesApi
      .getCategories({
        page: 0,
        size: 500,
        sortBy: 'name',
        sortDirection: 'ASC',
      })
      .pipe(finalize(() => (this.categoriesLoading = false)))
      .subscribe({
        next: (page) => {
          this.categories = page.content ?? [];
        },
        error: () => {
          this.categories = [];
        },
      });
  }

  private buildQuery(
    sort: Sort,
    pageIndex: number,
    size: number
  ): FindLessonsQuery {
    const sortByMap: Record<string, string> = {
      title: 'title',
      position: 'position',
    };
    const sortBy = sortByMap[sort.active] ?? 'position';
    const sortDirection = sort.direction === 'desc' ? 'DESC' : 'ASC';
    const premium = this.premiumFilter.value;
    return {
      page: pageIndex,
      size,
      sortBy,
      sortDirection,
      title: this.titleFilter.value.trim() || undefined,
      categoryUuid: this.categoryFilter.value || undefined,
      isPremium:
        premium === 'all' ? undefined : premium === 'yes',
    };
  }

  private patchDetailForm(row: LessonResponse): void {
    this.common.patchDetailForm(
      this.detailForm,
      {
        categoryUuid: row.categoryUuid ?? null,
        title: row.title ?? '',
        content: row.content ?? '',
        estimatedReadMinutes: row.estimatedReadMinutes ?? 5,
        position: row.position ?? 1,
        isPremium: !!row.isPremium,
      },
      this.detailReadOnly
    );
  }
}
