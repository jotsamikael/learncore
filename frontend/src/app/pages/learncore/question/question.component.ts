import { SelectionModel } from '@angular/cdk/collections';
import { CommonModule } from '@angular/common';
import {
  AfterViewInit,
  Component,
  OnDestroy,
  OnInit,
  TemplateRef,
  ViewChild,
} from '@angular/core';

import {
  FormArray,
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
  forkJoin,
  merge,
  Observable,
  of,
  Subject,
} from 'rxjs';

import {
  debounceTime,
  finalize,
  startWith,
  switchMap,
  takeUntil,
} from 'rxjs/operators';

import {
  CommonService,
  ListMeta,
} from '../../../core/services/common.service';

import { CategoriesApiService } from '../../../core/services/categories-api.service';
import { GlobalFormBuilderService } from '../../../core/services/globalFormBuilder.service';
import { PermissionService } from '../../../core/services/permission.service';
import { QuestionOptionApiService } from '../../../core/services/question-option-api.service';
import { QuestionsApiService } from '../../../core/services/questions-api.service';

import { getApiErrorMessage } from '../../../core/utils/api-error.utils';

import { CategoryResponse } from '../../../learncoreservices/models/category-response';
import { CreateQuestionForm } from '../../../learncoreservices/models/create-question-form';
import { CreateQuestionOptionForm } from '../../../learncoreservices/models/create-question-option-form';

import {
  FindQuestionsQuery,
  QuestionDifficultyLevel,
  QuestionTypeFilter,
} from '../../../learncoreservices/models/find-questions-query';

import { GetQuestionResponse } from '../../../learncoreservices/models/get-question-response';
import { QuestionOptionResponseDto } from '../../../learncoreservices/models/question-option-response-dto';
import { UpdateQuestionForm } from '../../../learncoreservices/models/update-question-form';
import { UpdateQuestionOptionForm } from '../../../learncoreservices/models/update-question-option-form';

import { SharedModule } from '../../../shared/shared.module';
import { PermissionCodes } from 'src/app/core/constants/permission-codes';


type QuestionType =
  | 'MCQ'
  | 'TRUE_OR_FALSE'
  | 'STRUCTURAL'
  | 'ESSAY';

type Difficulty =
  | 'EASY'
  | 'MEDIUM'
  | 'HARD'
  | 'EXPERT';

type GradingStrategy =
  | 'EXACT_MATCH'
  | 'KEYWORD_MATCH'
  | 'RUBRIC'
  | 'SEMANTIC_SIMILARITY'
  | 'AI';

interface OptionFormValue {
  uuid?: string | null;
  optionText: string;
  correct: boolean;
  imageUrl?: string | null;
  image?: File | null;
}

interface CriterionFormValue {
  uuid?: string;
  title: string;
  description: string;
  score: number;
  keywords?: string[];
}

interface WrittenAnswerPayload {
  referenceAnswer: string;
  maxScore: number;
  gradingStrategy: GradingStrategy;
  minimumScoreThreshold: number;
  gradingCriteria?: CriterionFormValue[];
}


@Component({
  selector: 'app-question',
  standalone: true,
  imports: [
    CommonModule,
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
  templateUrl: './question.component.html',
  styleUrl: './question.component.scss',
})
export class QuestionComponent
  implements OnInit, AfterViewInit, OnDestroy {

  pageTitle = 'Questions';

  breadCrumbItems = [
    { label: 'LearnCore' },
    { label: 'Question Bank', active: true },
  ];

  /*
   * ==========================================================
   * CONFIGURATION
   * ==========================================================
   */

  readonly questionTypes = [
    {
      value: 'MCQ' as QuestionType,
      label: 'Multiple Choice',
      shortLabel: 'MCQ',
      icon: 'checklist',
      description:
        'Learners choose the correct answer from several options.',
    },
    {
      value: 'TRUE_OR_FALSE' as QuestionType,
      label: 'True or False',
      shortLabel: 'True / False',
      icon: 'rule',
      description:
        'Learners decide whether a statement is true or false.',
    },
    {
      value: 'STRUCTURAL' as QuestionType,
      label: 'Structural',
      shortLabel: 'Structural',
      icon: 'short_text',
      description:
        'Learners provide a short written response.',
    },
    {
      value: 'ESSAY' as QuestionType,
      label: 'Essay',
      shortLabel: 'Essay',
      icon: 'article',
      description:
        'Learners provide a detailed long-form answer.',
    },
  ];

  readonly difficulties = [
    {
      value: 'EASY' as Difficulty,
      label: 'Easy',
    },
    {
      value: 'MEDIUM' as Difficulty,
      label: 'Medium',
    },
    {
      value: 'HARD' as Difficulty,
      label: 'Hard',
    },
    {
      value: 'EXPERT' as Difficulty,
      label: 'Expert',
    },
  ];

  readonly gradingStrategies = [
    {
      value: 'EXACT_MATCH' as GradingStrategy,
      label: 'Exact Match',
      icon: 'drag_handle',
      description:
        'The submitted answer must match the reference answer.',
    },
    {
      value: 'KEYWORD_MATCH' as GradingStrategy,
      label: 'Keyword Match',
      icon: 'key',
      description:
        'Checks whether expected concepts occur in the answer.',
    },
    {
      value: 'RUBRIC' as GradingStrategy,
      label: 'Rubric',
      icon: 'fact_check',
      description:
        'Scores the response against multiple grading criteria.',
    },
    {
      value: 'SEMANTIC_SIMILARITY' as GradingStrategy,
      label: 'Semantic Similarity',
      icon: 'psychology',
      description:
        'Compares the meaning of the answer with the reference.',
    },
    {
      value: 'AI' as GradingStrategy,
      label: 'AI',
      icon: 'auto_awesome',
      description:
        'Uses the configured AI grading engine.',
    },
  ];

  /*
   * ==========================================================
   * FORMS
   * ==========================================================
   */

  createForm!: FormGroup;
  detailForm!: FormGroup;

  createFormSubmitted = false;
  createFormError = '';
  createSubmitting = false;

  detailFormSubmitted = false;
  detailError = '';
  detailSaving = false;
  detailLoading = false;
  detailReadOnly = true;

  /*
   * ==========================================================
   * QUESTION STATE
   * ==========================================================
   */

  detailSnapshot: GetQuestionResponse | null = null;
  detailQuestionUuid: string | null = null;

  /*
   * Question images
   */
  createQuestionImage: File | null = null;
  createQuestionImagePreview: string | null = null;

  detailQuestionImage: File | null = null;
  detailQuestionImagePreview: string | null = null;

  /*
   * Option images are kept outside the form to avoid trying
   * to serialize File objects.
   */
  createOptionImages = new Map<number, File>();
  createOptionPreviews = new Map<number, string>();

  detailOptionImages = new Map<number, File>();
  detailOptionPreviews = new Map<number, string>();

  /*
   * Options that existed in DB and were removed while editing.
   */
  deletedOptionUuids: string[] = [];

  /*
   * ==========================================================
   * CATEGORIES
   * ==========================================================
   */

  categories: CategoryResponse[] = [];
  categoriesLoading = false;

  /*
   * ==========================================================
   * TABLE
   * ==========================================================
   */

  displayedColumns = [
    'select',
    'question',
    'type',
    'category',
    'difficulty',
    'grading',
    'actions',
  ];

  data: GetQuestionResponse[] = [];

  selection =
    new SelectionModel<GetQuestionResponse>(
      true,
      []
    );

  isLoading = true;
  loadError: string | null = null;

  meta: ListMeta | null = null;

  pageSize = 10;

  readonly pageSizeOptions = [
    5,
    10,
    25,
    50,
  ];

  /*
   * ==========================================================
   * FILTERS
   * ==========================================================
   */

  questionTextFilter =
    new FormControl('', {
      nonNullable: true,
    });

  typeFilter =
    new FormControl<QuestionTypeFilter | ''>(
      '',
      { nonNullable: true }
    );

  difficultyFilter =
    new FormControl<QuestionDifficultyLevel | ''>(
      '',
      { nonNullable: true }
    );

  categoryFilter =
    new FormControl('', {
      nonNullable: true,
    });

  /*
   * ==========================================================
   * RXJS
   * ==========================================================
   */

  private readonly destroy$ =
    new Subject<void>();

  private readonly refresh$ =
    new Subject<void>();

  @ViewChild(MatPaginator)
  paginator!: MatPaginator;

  @ViewChild(MatSort)
  sort!: MatSort;

  @ViewChild('questionDetailModal')
  questionDetailModalTpl!: TemplateRef<unknown>;

  constructor(
    private readonly formService:
      GlobalFormBuilderService,

    private readonly questionsApi:
      QuestionsApiService,

    private readonly questionOptionsApi:
      QuestionOptionApiService,

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
   * ==========================================================
   * LIFECYCLE
   * ==========================================================
   */

  ngOnInit(): void {
    this.createForm =
      this.formService.createQuestionForm();

    this.detailForm =
      this.formService.updateQuestionForm();

    this.configureQuestionTypeChanges(
      this.createForm,
      false
    );

    this.configureQuestionTypeChanges(
      this.detailForm,
      true
    );

    this.loadCategories();
  }

  ngAfterViewInit(): void {
    this.sort.active = 'questionText';
    this.sort.direction = 'asc';

    merge(
      this.refresh$,

      this.questionTextFilter
        .valueChanges
        .pipe(
          debounceTime(300)
        ),

      this.typeFilter.valueChanges,
      this.difficultyFilter.valueChanges,
      this.categoryFilter.valueChanges,

      this.sort.sortChange,
      this.paginator.page
    )
      .pipe(
        startWith(undefined),
        takeUntil(this.destroy$)
      )
      .subscribe(() => {
        this.loadQuestions();
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();

    this.cleanupPreviews();
  }

  /*
   * ==========================================================
   * PERMISSIONS
   * ==========================================================
   *
   * Replace with PermissionCodes constants when available.
   */

  get canCreateQuestion(): boolean {
    return this.permissionService.can(
      PermissionCodes.TENANT_QUESTION_READ
    );
  }

  get canUpdateQuestion(): boolean {
    return this.permissionService.can(
      PermissionCodes.TENANT_QUESTION_UPDATE
    );
  }

  get canDeleteQuestion(): boolean {
    return this.permissionService.can(
      PermissionCodes.TENANT_QUESTION_DELETE
    );
  }

  /*
   * ==========================================================
   * FORM GETTERS
   * ==========================================================
   */

  get cf() {
    return this.createForm.controls;
  }

  get df() {
    return this.detailForm.controls;
  }

  get createType(): QuestionType {
    return this.createForm.get(
      'questionType'
    )?.value as QuestionType;
  }

  get detailType(): QuestionType {
    return this.detailForm.get(
      'questionType'
    )?.value as QuestionType;
  }

  get createOptions(): FormArray {
    return this.createForm.get(
      'options'
    ) as FormArray;
  }

  get detailOptions(): FormArray {
    return this.detailForm.get(
      'options'
    ) as FormArray;
  }

  get createCriteria(): FormArray {
    return this.createForm.get(
      'gradingCriteria'
    ) as FormArray;
  }

  get detailCriteria(): FormArray {
    return this.detailForm.get(
      'gradingCriteria'
    ) as FormArray;
  }

  get createGradingStrategy(): GradingStrategy {
    return this.createForm.get(
      'gradingStrategy'
    )?.value as GradingStrategy;
  }

  get detailGradingStrategy(): GradingStrategy {
    return this.detailForm.get(
      'gradingStrategy'
    )?.value as GradingStrategy;
  }

  /*
   * ==========================================================
   * TYPE HELPERS
   * ==========================================================
   */

  isChoiceType(
    type?: string | null
  ): boolean {
    return (
      type === 'MCQ' ||
      type === 'TRUE_OR_FALSE'
    );
  }

  isWrittenType(
    type?: string | null
  ): boolean {
    return (
      type === 'STRUCTURAL' ||
      type === 'ESSAY'
    );
  }

  selectCreateType(
    type: QuestionType
  ): void {
    this.createForm
      .get('questionType')
      ?.setValue(type);
  }

  selectDetailType(
    type: QuestionType
  ): void {
    if (this.detailReadOnly) {
      return;
    }

    this.detailForm
      .get('questionType')
      ?.setValue(type);
  }

  /*
   * ==========================================================
   * OPTION MANAGEMENT
   * ==========================================================
   */

  addCreateOption(): void {
    this.createOptions.push(
      this.formService
        .createQuestionOptionForm()
    );
  }

  removeCreateOption(
    index: number
  ): void {
    this.revokeMapPreview(
      this.createOptionPreviews,
      index
    );

    this.createOptions.removeAt(index);

    this.reindexFileMap(
      this.createOptionImages,
      index
    );

    this.reindexPreviewMap(
      this.createOptionPreviews,
      index
    );
  }

  addDetailOption(): void {
    if (this.detailReadOnly) {
      return;
    }

    this.detailOptions.push(
      this.formService
        .createQuestionOptionForm()
    );
  }

  removeDetailOption(
    index: number
  ): void {
    if (this.detailReadOnly) {
      return;
    }

    const control =
      this.detailOptions.at(
        index
      ) as FormGroup;

    const uuid =
      control.get('uuid')?.value;

    if (uuid) {
      this.deletedOptionUuids.push(
        uuid
      );
    }

    this.revokeMapPreview(
      this.detailOptionPreviews,
      index
    );

    this.detailOptions.removeAt(index);

    this.reindexFileMap(
      this.detailOptionImages,
      index
    );

    this.reindexPreviewMap(
      this.detailOptionPreviews,
      index
    );
  }

  /*
   * MCQ can support multiple correct options.
   */
  toggleOptionCorrect(
    form: FormGroup,
    index: number
  ): void {
    const type =
      form.get(
        'questionType'
      )?.value as QuestionType;

    const options =
      form.get(
        'options'
      ) as FormArray;

    const option =
      options.at(
        index
      ) as FormGroup;

    if (
      type ===
      'TRUE_OR_FALSE'
    ) {
      options.controls.forEach(
        (
          control,
          optionIndex
        ) => {
          control
            .get('correct')
            ?.setValue(
              optionIndex === index
            );
        }
      );

      return;
    }

    option
      .get('correct')
      ?.setValue(
        !option.get(
          'correct'
        )?.value
      );
  }

  correctOptionCount(
    form: FormGroup
  ): number {
    const options =
      form.get(
        'options'
      ) as FormArray;

    return options.controls.filter(
      option =>
        option.get(
          'correct'
        )?.value === true
    ).length;
  }

  /*
   * ==========================================================
   * OPTION IMAGES
   * ==========================================================
   */

  onCreateOptionImage(
    event: Event,
    index: number
  ): void {
    const input =
      event.target as HTMLInputElement;

    const file =
      input.files?.[0] ?? null;

    this.revokeMapPreview(
      this.createOptionPreviews,
      index
    );

    if (!file) {
      this.createOptionImages.delete(
        index
      );

      return;
    }

    this.createOptionImages.set(
      index,
      file
    );

    this.createOptionPreviews.set(
      index,
      URL.createObjectURL(file)
    );
  }

  onDetailOptionImage(
    event: Event,
    index: number
  ): void {
    const input =
      event.target as HTMLInputElement;

    const file =
      input.files?.[0] ?? null;

    this.revokeMapPreview(
      this.detailOptionPreviews,
      index
    );

    if (!file) {
      this.detailOptionImages.delete(
        index
      );

      return;
    }

    this.detailOptionImages.set(
      index,
      file
    );

    this.detailOptionPreviews.set(
      index,
      URL.createObjectURL(file)
    );
  }

  getCreateOptionPreview(
    index: number
  ): string | null {
    return (
      this.createOptionPreviews.get(
        index
      ) ?? null
    );
  }

  getDetailOptionPreview(
    index: number
  ): string | null {
    return (
      this.detailOptionPreviews.get(
        index
      ) ??
      this.detailOptions
        .at(index)
        ?.get('imageUrl')
        ?.value ??
      null
    );
  }

  /*
   * ==========================================================
   * RUBRIC
   * ==========================================================
   */

  addCreateCriterion(): void {
    this.createCriteria.push(
      this.formService
        .createGradingCriterionForm()
    );
  }

  removeCreateCriterion(
    index: number
  ): void {
    this.createCriteria.removeAt(
      index
    );
  }

  addDetailCriterion(): void {
    if (this.detailReadOnly) {
      return;
    }

    this.detailCriteria.push(
      this.formService
        .createGradingCriterionForm()
    );
  }

  removeDetailCriterion(
    index: number
  ): void {
    if (this.detailReadOnly) {
      return;
    }

    this.detailCriteria.removeAt(
      index
    );
  }

  /*
   * ==========================================================
   * TABLE SELECTION
   * ==========================================================
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
    row: GetQuestionResponse
  ): void {
    this.selection.toggle(row);
  }

  /*
   * ==========================================================
   * FILTERS / PAGINATION
   * ==========================================================
   */

  refresh(): void {
    this.refresh$.next();
  }

  clearFilters(): void {
    this.questionTextFilter.setValue('');
    this.typeFilter.setValue('');
    this.difficultyFilter.setValue('');
    this.categoryFilter.setValue('');

    this.paginator?.firstPage();
  }

  onPaginatorPage(
    _event: PageEvent
  ): void {
    // handled by merge()
  }

  onPageSizeChange(
    next: number
  ): void {
    if (
      !this.paginator ||
      next === this.pageSize
    ) {
      return;
    }

    this.pageSize = next;
    this.paginator.pageIndex = 0;

    this.refresh();
  }

  /*
   * ==========================================================
   * CREATE QUESTION
   * ==========================================================
   */

  openCreateModal(
    content: TemplateRef<unknown>
  ): void {
    this.createFormSubmitted =
      false;

    this.createFormError = '';

    this.resetCreateState();

    this.createForm.reset({
      questionType: 'MCQ',
      categoryUuid: null,
      difficultyLevel: 'MEDIUM',
      questionText: '',
      explanation: '',

      referenceAnswer: '',
      maxScore: 1,
      gradingStrategy:
        'KEYWORD_MATCH',
      minimumScoreThreshold:
        0.7,
    });

    /*
     * Start MCQ with four answer slots.
     */
    for (
      let i = 0;
      i < 4;
      i++
    ) {
      this.addCreateOption();
    }

    this.modalService.open(
      content,
      {
        size: 'xl',
        scrollable: true,
        backdrop: 'static',
        modalDialogClass: 'question-modal-dialog',
      }
    );
  }

  submitCreate(
    modal: NgbModalRef
  ): void {
    this.createFormSubmitted =
      true;

    this.createFormError = '';

    if (!this.validateQuestionForm(
      this.createForm
    )) {
      return;
    }

    const questionBody =
      this.buildQuestionPayload(
        this.createForm
      ) as CreateQuestionForm;

    this.createSubmitting = true;

    /*
     * 1. Create question.
     * 2. If choice question, create options.
     */
    this.questionsApi
      .createQuestion(
        questionBody,
        this.createQuestionImage
      )
      .pipe(
        switchMap(
          createdQuestion => {
            if (
              !createdQuestion.uuid
            ) {
              throw new Error(
                'Question created without UUID.'
              );
            }

            if (
              !this.isChoiceType(
                createdQuestion.questionType
              )
            ) {
              return of(
                createdQuestion
              );
            }

            return this
              .createOptionsForQuestion(
                createdQuestion.uuid,
                this.createForm,
                this.createOptionImages
              )
              .pipe(
                switchMap(
                  () =>
                    of(
                      createdQuestion
                    )
                )
              );
          }
        ),

        finalize(
          () =>
            (this.createSubmitting =
              false)
        )
      )
      .subscribe({
        next: () => {
          modal.close();

          this.resetCreateState();

          this.refresh();
        },

        error: err => {
          this.createFormError =
            getApiErrorMessage(
              err,
              'The question could not be completely created.'
            );
        },
      });
  }

  /*
   * ==========================================================
   * VIEW / EDIT
   * ==========================================================
   */

  onView(
    row: GetQuestionResponse
  ): void {
    this.openQuestionDetailModal(
      row,
      false
    );
  }

  onEdit(
    row: GetQuestionResponse
  ): void {
    this.openQuestionDetailModal(
      row,
      true
    );
  }

  openQuestionDetailModal(
    row: GetQuestionResponse,
    edit: boolean
  ): void {
    if (!row.uuid) {
      return;
    }

    this.detailLoading = true;
    this.detailError = '';

    this.detailReadOnly =
      !edit ||
      !this.canUpdateQuestion;

    this.detailQuestionUuid =
      row.uuid;

    this.detailSnapshot = row;

    this.deletedOptionUuids = [];

    this.resetDetailOptionState();

    const modal =
      this.modalService.open(
        this.questionDetailModalTpl,
        {
          size: 'xl',
          scrollable: true,
          backdrop: 'static',
          modalDialogClass: 'question-modal-dialog',
        }
      );

    /*
     * Load question first.
     */
    this.questionsApi
      .getQuestion(row.uuid)
      .pipe(
        switchMap(
          question => {
            this.detailSnapshot =
              question;

            this.detailQuestionImagePreview =
              question.imageUrl ??
              null;

            this.patchQuestionForm(
              question
            );

            /*
             * Only MCQ / TRUE_FALSE
             * have options.
             */
            if (
              !this.isChoiceType(
                question.questionType
              )
            ) {
              return of({
                question,
                options: [],
              });
            }

            return this.questionOptionsApi
              .getQuestionOptions({
                questionUuid:
                  row.uuid,
                page: 0,
                size: 100,
                sortBy: 'id',
                sortDirection: 'ASC',
              })
              .pipe(
                switchMap(page =>
                  of({
                    question,
                    options:
                      page.content ??
                      [],
                  })
                )
              );
          }
        ),

        finalize(
          () =>
            (this.detailLoading =
              false)
        )
      )
      .subscribe({
        next: result => {
          this.patchOptions(
            result.options
          );

          this.setDetailFormMode();
        },

        error: err => {
          this.detailError =
            getApiErrorMessage(
              err,
              'Failed to load question details.'
            );
        },
      });

    modal.result.finally(() => {
      this.resetDetailOptionState();
    });
  }

  toggleDetailEditMode(): void {
    if (
      !this.canUpdateQuestion ||
      this.detailSaving
    ) {
      return;
    }

    this.detailReadOnly =
      !this.detailReadOnly;

    if (
      this.detailReadOnly
    ) {
      /*
       * Reload authoritative data when
       * cancelling editing.
       */
      if (this.detailSnapshot) {
        this.patchQuestionForm(
          this.detailSnapshot
        );

        if (
          this.detailQuestionUuid &&
          this.isChoiceType(
            this.detailSnapshot
              .questionType
          )
        ) {
          this.loadDetailOptions(
            this.detailQuestionUuid
          );
        }
      }
    }

    this.setDetailFormMode();
  }

  submitDetailUpdate(
    modal: NgbModalRef
  ): void {
    this.detailFormSubmitted =
      true;

    this.detailError = '';

    if (
      !this.detailQuestionUuid ||
      !this.validateQuestionForm(
        this.detailForm
      )
    ) {
      return;
    }

    const questionBody =
      this.buildQuestionPayload(
        this.detailForm
      ) as UpdateQuestionForm;

    this.detailSaving = true;

    /*
     * First update question.
     */
    this.questionsApi
      .updateQuestion(
        this.detailQuestionUuid,
        questionBody,
        this.detailQuestionImage
      )
      .pipe(
        /*
         * Then synchronize options.
         */
        switchMap(updated => {
          if (
            !this.isChoiceType(
              updated.questionType
            )
          ) {
            /*
             * If a question changed from MCQ
             * to written type, delete any
             * remaining persisted options.
             */
            return this
              .deleteOptions(
                this.deletedOptionUuids
              )
              .pipe(
                switchMap(
                  () => of(updated)
                )
              );
          }

          return this
            .syncQuestionOptions(
              this.detailQuestionUuid!,
              this.detailForm
            )
            .pipe(
              switchMap(
                () => of(updated)
              )
            );
        }),

        finalize(
          () =>
            (this.detailSaving =
              false)
        )
      )
      .subscribe({
        next: () => {
          modal.close();
          this.refresh();
        },

        error: err => {
          this.detailError =
            getApiErrorMessage(
              err,
              'The question could not be completely updated.'
            );
        },
      });
  }

  /*
   * ==========================================================
   * DELETE QUESTION
   * ==========================================================
   */

  async onDeleteQuestion(
    row: GetQuestionResponse
  ): Promise<void> {
    if (
      !row.uuid ||
      !this.canDeleteQuestion
    ) {
      return;
    }

    const confirmed =
      await this.common.confirmDelete(
        'Delete question?',
        'The question and its associated configuration will be removed.'
      );

    if (!confirmed) {
      return;
    }

    this.questionsApi
      .deleteQuestion(row.uuid)
      .subscribe({
        next: () => {
          this.selection.deselect(
            row
          );

          this.refresh();
        },

        error: err => {
          this.common.showErrorAlert(
            'Error',
            getApiErrorMessage(
              err,
              'Failed to delete question.'
            )
          );
        },
      });
  }

  /*
   * ==========================================================
   * QUESTION IMAGE
   * ==========================================================
   */

  onCreateQuestionImage(
    event: Event
  ): void {
    const input =
      event.target as HTMLInputElement;

    const file =
      input.files?.[0] ??
      null;

    this.createQuestionImage =
      file;

    this.revokeUrl(
      this.createQuestionImagePreview
    );

    this.createQuestionImagePreview =
      file
        ? URL.createObjectURL(
            file
          )
        : null;
  }

  onDetailQuestionImage(
    event: Event
  ): void {
    const input =
      event.target as HTMLInputElement;

    const file =
      input.files?.[0] ??
      null;

    this.detailQuestionImage =
      file;

    if (
      this.detailQuestionImagePreview
        ?.startsWith('blob:')
    ) {
      this.revokeUrl(
        this.detailQuestionImagePreview
      );
    }

    this.detailQuestionImagePreview =
      file
        ? URL.createObjectURL(
            file
          )
        : this.detailSnapshot
            ?.imageUrl ??
          null;
  }

  /*
   * ==========================================================
   * DISPLAY
   * ==========================================================
   */

  typeLabel(
    type?: string
  ): string {
    return (
      this.questionTypes.find(
        item =>
          item.value === type
      )?.shortLabel ??
      type ??
      '—'
    );
  }

  typeClass(
    type?: string
  ): string {
    switch (type) {
      case 'MCQ':
        return 'bg-primary-subtle text-primary';

      case 'TRUE_OR_FALSE':
        return 'bg-info-subtle text-info';

      case 'STRUCTURAL':
        return 'bg-warning-subtle text-warning';

      case 'ESSAY':
        return 'bg-secondary-subtle text-secondary';

      default:
        return 'bg-light text-muted';
    }
  }

  difficultyClass(
    difficulty?: string
  ): string {
    switch (difficulty) {
      case 'EASY':
        return 'bg-success-subtle text-success';

      case 'MEDIUM':
        return 'bg-info-subtle text-info';

      case 'HARD':
        return 'bg-warning-subtle text-warning';

      case 'EXPERT':
        return 'bg-danger-subtle text-danger';

      default:
        return 'bg-light text-muted';
    }
  }

  gradingLabel(
    strategy?: string
  ): string {
    return (
      this.gradingStrategies.find(
        item =>
          item.value ===
          strategy
      )?.label ??
      strategy ??
      '—'
    );
  }

  shortQuestion(
    text?: string
  ): string {
    if (!text) {
      return '—';
    }

    return text.length > 100
      ? `${text.substring(
          0,
          100
        )}…`
      : text;
  }

  /*
   * ==========================================================
   * EXPORT
   * ==========================================================
   */

  exportCsv(): void {
    this.common.exportCsv(
      this.exportRows(
        this.data
      ),
      'questions'
    );
  }

  exportExcel(): void {
    this.common.exportExcel(
      this.exportRows(
        this.data
      ),
      'questions'
    );
  }

  exportSelectedCsv(): void {
    if (
      this.selection.isEmpty()
    ) {
      return;
    }

    this.common.exportSelectedCsv(
      this.exportRows(
        this.selection.selected
      ),
      'questions'
    );
  }

  printTable(): void {
    this.common.printDataTable({
      title: this.pageTitle,

      table:
        this.exportRows(
          this.data
        ),

      metaLine:
        this.common.getExportMetaLine(
          this.meta
        ),
    });
  }

  /*
   * ==========================================================
   * LOAD QUESTIONS
   * ==========================================================
   */

  private loadQuestions(): void {
    if (
      !this.paginator ||
      !this.sort
    ) {
      return;
    }

    this.isLoading = true;
    this.loadError = null;

    this.selection.clear();

    this.questionsApi
      .getQuestions(
        this.buildQuery()
      )
      .pipe(
        finalize(
          () =>
            (this.isLoading =
              false)
        )
      )
      .subscribe({
        next: page => {
          this.data =
            page.content ??
            [];

          this.meta = {
            totalItems:
              page.totalElements ??
              0,

            totalPages:
              page.totalPages ??
              0,

            currentPage:
              (page.number ??
                0) + 1,
          };
        },

        error: err => {
          this.data = [];
          this.meta = null;

          this.loadError =
            getApiErrorMessage(
              err,
              'Failed to load questions.'
            );
        },
      });
  }

  private loadCategories(): void {
    this.categoriesLoading =
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
            (this.categoriesLoading =
              false)
        )
      )
      .subscribe({
        next: page => {
          this.categories =
            page.content ??
            [];
        },

        error: () => {
          this.categories = [];
        },
      });
  }

  private buildQuery():
    FindQuestionsQuery {
    return {
      page:
        this.paginator
          .pageIndex,

      size:
        this.pageSize,

      sortBy:
        this.sort.active ||
        'questionText',

      sortDirection:
        this.sort.direction ===
        'desc'
          ? 'DESC'
          : 'ASC',

      questionText:
        this.questionTextFilter
          .value.trim() ||
        undefined,

      questionType:
        this.typeFilter.value ||
        undefined,

      difficultyLevel:
        this.difficultyFilter
          .value ||
        undefined,

      categoryUuid:
        this.categoryFilter
          .value ||
        undefined,
    };
  }

  /*
   * ==========================================================
   * QUESTION PAYLOAD
   * ==========================================================
   */

  private buildQuestionPayload(
    form: FormGroup
  ):
    | CreateQuestionForm
    | UpdateQuestionForm {

    const raw =
      form.getRawValue();

    const writtenAnswerConfig =
      this.isWrittenType(
        raw.questionType
      )
        ? JSON.stringify(
            this.buildWrittenConfig(
              form
            )
          )
        : undefined;

    return {
      categoryUuid:
        raw.categoryUuid,

      difficultyLevel:
        raw.difficultyLevel,

      questionType:
        raw.questionType,

      questionText:
        String(
          raw.questionText
        ).trim(),

      explanation:
        String(
          raw.explanation ??
          ''
        ).trim() ||
        undefined,

      writtenAnswerConfig,
    };
  }

  private buildWrittenConfig(
    form: FormGroup
  ): WrittenAnswerPayload {
    const raw =
      form.getRawValue();

    const criteria =
      (
        raw.gradingCriteria ??
        []
      )
        .filter(
          (
            criterion:
              CriterionFormValue
          ) =>
            criterion.title
              ?.trim()
        )
        .map(
          (
            criterion:
              CriterionFormValue
          ) => ({
            title:
              criterion.title
                .trim(),

            description:
              criterion.description
                ?.trim() ??
              '',

            score:
              Number(
                criterion.score
              ),

            keywords:
              criterion.keywords ??
              [],
          })
        );

    return {
      referenceAnswer:
        String(
          raw.referenceAnswer ??
          ''
        ).trim(),

      maxScore:
        Number(
          raw.maxScore
        ),

      gradingStrategy:
        raw.gradingStrategy,

      minimumScoreThreshold:
        Number(
          raw.minimumScoreThreshold
        ),

      ...(criteria.length
        ? {
            gradingCriteria:
              criteria,
          }
        : {}),
    };
  }

  /*
   * ==========================================================
   * CREATE OPTIONS
   * ==========================================================
   */

  private createOptionsForQuestion(
    questionUuid: string,
    form: FormGroup,
    images: Map<number, File>
  ): Observable<unknown[]> {

    const options: OptionFormValue[] = (
      form.get('options') as FormArray
    ).getRawValue();

    const requests =
      options.map(
        (option, index) => {

          const body:
            CreateQuestionOptionForm = {
            questionUuid,

            optionText:
              option.optionText
                .trim(),

            correct:
              option.correct,
          };

          return this.questionOptionsApi
            .createQuestionOption(
              body,
              images.get(
                index
              ) ?? null
            );
        }
      );

    return requests.length
      ? forkJoin(requests)
      : of([]);
  }

  /*
   * ==========================================================
   * UPDATE OPTIONS
   * ==========================================================
   */

  private syncQuestionOptions(
    questionUuid: string,
    form: FormGroup
  ): Observable<unknown[]> {

    const options: OptionFormValue[] = (
      form.get('options') as FormArray
    ).getRawValue();

    const requests:
      Observable<unknown>[] =
      [];

    /*
     * Delete removed options.
     */
    for (
      const uuid of
      this.deletedOptionUuids
    ) {
      requests.push(
        this.questionOptionsApi
          .deleteQuestionOption(
            uuid
          )
      );
    }

    /*
     * Create or update current options.
     */
    options.forEach(
      (option, index) => {

        const image =
          this.detailOptionImages
            .get(index) ??
          null;

        if (option.uuid) {

          const body:
            UpdateQuestionOptionForm = {
            optionText:
              option.optionText
                .trim(),

            correct:
              option.correct,
          };

          requests.push(
            this.questionOptionsApi
              .updateQuestionOption(
                option.uuid,
                body,
                image
              )
          );

        } else {

          const body:
            CreateQuestionOptionForm = {
            questionUuid,

            optionText:
              option.optionText
                .trim(),

            correct:
              option.correct,
          };

          requests.push(
            this.questionOptionsApi
              .createQuestionOption(
                body,
                image
              )
          );
        }
      }
    );

    return requests.length
      ? forkJoin(requests)
      : of([]);
  }

  private deleteOptions(
    uuids: string[]
  ): Observable<unknown[]> {
    if (!uuids.length) {
      return of([]);
    }

    return forkJoin(
      uuids.map(
        uuid =>
          this.questionOptionsApi
            .deleteQuestionOption(
              uuid
            )
      )
    );
  }

  /*
   * ==========================================================
   * DETAIL PATCHING
   * ==========================================================
   */

  private patchQuestionForm(
    question: GetQuestionResponse
  ): void {

    this.clearOptions(
      this.detailForm
    );

    this.clearCriteria(
      this.detailForm
    );

    const config =
      question
        .writtenAnswerConfig;

    this.detailForm.patchValue(
      {
        questionType:
          question.questionType ??
          'MCQ',

        categoryUuid:
          question.categoryUuid ??
          null,

        difficultyLevel:
          question.difficultyLevel ??
          'MEDIUM',

        questionText:
          question.questionText ??
          '',

        explanation:
          question.explanation ??
          '',

        referenceAnswer:
          config
            ?.referenceAnswer ??
          '',

        maxScore:
          config?.maxScore ??
          1,

        gradingStrategy:
          config
            ?.gradingStrategy ??
          'KEYWORD_MATCH',

        minimumScoreThreshold:
          config
            ?.minimumScoreThreshold ??
          0.7,
      },
      {
        emitEvent: false,
      }
    );

    for (
      const criterion of
      config
        ?.gradingCriteria ??
      []
    ) {
      const group =
        this.formService
          .createGradingCriterionForm();

      group.patchValue({
        title:
          criterion.title ??
          '',

        description:
          criterion.description ??
          '',

        score:
          criterion.score ??
          1,
      });

      this.detailCriteria.push(
        group
      );
    }
  }

  private patchOptions(
    options:
      QuestionOptionResponseDto[]
  ): void {

    this.clearOptions(
      this.detailForm
    );

    for (
      const option of options
    ) {

      const group =
        this.formService
          .createQuestionOptionForm(
            option.optionText ??
              '',
            option.correct ??
              false
          );

      group.patchValue({
        uuid:
          option.uuid ??
          null,

        imageUrl:
          option.imageUrl ??
          null,
      });

      this.detailOptions.push(
        group
      );
    }

    this.setDetailFormMode();
  }

  private loadDetailOptions(
    questionUuid: string
  ): void {
    this.questionOptionsApi
      .getQuestionOptions({
        questionUuid,
        page: 0,
        size: 100,
        sortBy: 'id',
        sortDirection: 'ASC',
      })
      .subscribe({
        next: page => {
          this.patchOptions(
            page.content ??
            []
          );
        },
      });
  }

  /*
   * ==========================================================
   * DYNAMIC TYPE MANAGEMENT
   * ==========================================================
   */

  private configureQuestionTypeChanges(
    form: FormGroup,
    isDetail: boolean
  ): void {

    form.get('questionType')
      ?.valueChanges
      .pipe(
        takeUntil(
          this.destroy$
        )
      )
      .subscribe(
        (
          type:
            QuestionType
        ) => {

          /*
           * TRUE / FALSE:
           * always exactly two options.
           */
          if (
            type ===
            'TRUE_OR_FALSE'
          ) {

            this.clearOptions(
              form
            );

            const trueOption =
              this.formService
                .createQuestionOptionForm(
                  'True',
                  true
                );

            const falseOption =
              this.formService
                .createQuestionOptionForm(
                  'False',
                  false
                );

            (
              form.get(
                'options'
              ) as FormArray
            ).push(
              trueOption
            );

            (
              form.get(
                'options'
              ) as FormArray
            ).push(
              falseOption
            );

            return;
          }

          /*
           * MCQ:
           * default four options.
           */
          if (
            type === 'MCQ' &&
            (
              form.get(
                'options'
              ) as FormArray
            ).length === 0
          ) {

            for (
              let i = 0;
              i < 4;
              i++
            ) {
              (
                form.get(
                  'options'
                ) as FormArray
              ).push(
                this.formService
                  .createQuestionOptionForm()
              );
            }

            return;
          }

          /*
           * Written question:
           * no choice options.
           */
          if (
            this.isWrittenType(
              type
            )
          ) {

            /*
             * During editing, remember
             * persisted options so they
             * can be deleted on save.
             */
            if (isDetail) {

              const options =
                (
                  form.get(
                    'options'
                  ) as FormArray
                );

              for (
                const control of
                options.controls
              ) {

                const uuid =
                  control
                    .get('uuid')
                    ?.value;

                if (
                  uuid &&
                  !this.deletedOptionUuids
                    .includes(uuid)
                ) {
                  this.deletedOptionUuids
                    .push(uuid);
                }
              }
            }

            this.clearOptions(
              form
            );
          }
        }
      );
  }

  /*
   * ==========================================================
   * VALIDATION
   * ==========================================================
   */

  private validateQuestionForm(
    form: FormGroup
  ): boolean {

    if (form.invalid) {
      form.markAllAsTouched();

      this.setFormError(
        form,
        'Please complete all required fields.'
      );

      return false;
    }

    const type =
      form.get(
        'questionType'
      )?.value as QuestionType;

    if (
      this.isChoiceType(
        type
      )
    ) {

      const options =
        form.get(
          'options'
        ) as FormArray;

      if (
        options.length < 2
      ) {
        this.setFormError(
          form,
          'A choice question requires at least two answer options.'
        );

        return false;
      }

      const invalidText =
        options.controls.some(
          option =>
            !String(
              option.get(
                'optionText'
              )?.value ??
              ''
            ).trim()
        );

      if (invalidText) {
        this.setFormError(
          form,
          'Every answer option must have text.'
        );

        return false;
      }

      const correct =
        this.correctOptionCount(
          form
        );

      if (correct === 0) {
        this.setFormError(
          form,
          'Select at least one correct answer.'
        );

        return false;
      }

      if (
        type ===
          'TRUE_OR_FALSE' &&
        correct !== 1
      ) {
        this.setFormError(
          form,
          'A True / False question must have exactly one correct answer.'
        );

        return false;
      }
    }

    if (
      this.isWrittenType(
        type
      )
    ) {

      const reference =
        String(
          form.get(
            'referenceAnswer'
          )?.value ??
          ''
        ).trim();

      if (!reference) {
        this.setFormError(
          form,
          'A reference answer is required for written questions.'
        );

        return false;
      }

      const strategy =
        form.get(
          'gradingStrategy'
        )?.value;

      if (
        strategy ===
          'RUBRIC' &&
        (
          form.get(
            'gradingCriteria'
          ) as FormArray
        ).length === 0
      ) {
        this.setFormError(
          form,
          'Add at least one grading criterion for rubric grading.'
        );

        return false;
      }
    }

    return true;
  }

  private setFormError(
    form: FormGroup,
    message: string
  ): void {
    if (
      form ===
      this.createForm
    ) {
      this.createFormError =
        message;
    } else {
      this.detailError =
        message;
    }
  }

  /*
   * ==========================================================
   * FORM STATE
   * ==========================================================
   */

  private setDetailFormMode(): void {
    if (
      this.detailReadOnly
    ) {
      this.detailForm.disable({
        emitEvent: false,
      });
    } else {
      this.detailForm.enable({
        emitEvent: false,
      });
    }
  }

  private clearOptions(
    form: FormGroup
  ): void {
    (
      form.get(
        'options'
      ) as FormArray
    ).clear();
  }

  private clearCriteria(
    form: FormGroup
  ): void {
    (
      form.get(
        'gradingCriteria'
      ) as FormArray
    ).clear();
  }

  /*
   * ==========================================================
   * RESET / PREVIEW UTILITIES
   * ==========================================================
   */

  private resetCreateState(): void {
    this.clearOptions(
      this.createForm
    );

    this.clearCriteria(
      this.createForm
    );

    this.createQuestionImage =
      null;

    this.revokeUrl(
      this.createQuestionImagePreview
    );

    this.createQuestionImagePreview =
      null;

    this.revokeAllMapPreviews(
      this.createOptionPreviews
    );

    this.createOptionImages.clear();
    this.createOptionPreviews.clear();
  }

  private resetDetailOptionState(): void {
    this.revokeAllMapPreviews(
      this.detailOptionPreviews
    );

    this.detailOptionImages.clear();
    this.detailOptionPreviews.clear();

    this.deletedOptionUuids =
      [];
  }

  private cleanupPreviews(): void {
    this.revokeUrl(
      this.createQuestionImagePreview
    );

    if (
      this.detailQuestionImagePreview
        ?.startsWith('blob:')
    ) {
      this.revokeUrl(
        this.detailQuestionImagePreview
      );
    }

    this.revokeAllMapPreviews(
      this.createOptionPreviews
    );

    this.revokeAllMapPreviews(
      this.detailOptionPreviews
    );
  }

  private revokeUrl(
    value?: string | null
  ): void {
    if (
      value?.startsWith(
        'blob:'
      )
    ) {
      URL.revokeObjectURL(
        value
      );
    }
  }

  private revokeMapPreview(
    map: Map<number, string>,
    index: number
  ): void {
    const value =
      map.get(index);

    this.revokeUrl(value);

    map.delete(index);
  }

  private revokeAllMapPreviews(
    map: Map<number, string>
  ): void {
    map.forEach(
      value =>
        this.revokeUrl(
          value
        )
    );
  }

  private reindexFileMap(
    map: Map<number, File>,
    removedIndex: number
  ): void {
    const next =
      new Map<number, File>();

    map.forEach(
      (value, key) => {
        if (
          key < removedIndex
        ) {
          next.set(
            key,
            value
          );
        } else if (
          key > removedIndex
        ) {
          next.set(
            key - 1,
            value
          );
        }
      }
    );

    map.clear();

    next.forEach(
      (value, key) =>
        map.set(
          key,
          value
        )
    );
  }

  private reindexPreviewMap(
    map: Map<number, string>,
    removedIndex: number
  ): void {
    const next =
      new Map<number, string>();

    map.forEach(
      (value, key) => {
        if (
          key < removedIndex
        ) {
          next.set(
            key,
            value
          );
        } else if (
          key > removedIndex
        ) {
          next.set(
            key - 1,
            value
          );
        }
      }
    );

    map.clear();

    next.forEach(
      (value, key) =>
        map.set(
          key,
          value
        )
    );
  }

  /*
   * ==========================================================
   * EXPORT
   * ==========================================================
   */

  private exportRows(
    rows: GetQuestionResponse[]
  ): string[][] {
    return [
      [
        'UUID',
        'Question',
        'Type',
        'Category',
        'Difficulty',
        'Grading',
      ],

      ...rows.map(
        row => [
          row.uuid ?? '',
          row.questionText ?? '',
          row.questionType ?? '',
          row.categoryName ?? '',
          row.difficultyLevel ?? '',
          row.writtenAnswerConfig
            ?.gradingStrategy ??
            '',
        ]
      ),
    ];
  }
}