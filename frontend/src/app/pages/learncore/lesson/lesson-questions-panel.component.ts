import {
  Component,
  EventEmitter,
  Input,
  OnChanges,
  Output,
  SimpleChanges,
  TemplateRef,
  ViewChild,
} from '@angular/core';
import {
  FormArray,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
} from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { MatTooltipModule } from '@angular/material/tooltip';
import { CKEditorModule } from '@ckeditor/ckeditor5-angular';
import {
  NgbModal,
  NgbModalRef,
  NgbModule,
} from '@ng-bootstrap/ng-bootstrap';
import {
  forkJoin,
  of,
  switchMap,
  finalize,
} from 'rxjs';

import { LessonQuestionsApiService } from '../../../core/services/lesson-questions-api.service';
import { PermissionCodes } from '../../../core/constants/permission-codes';
import { QuestionsApiService } from '../../../core/services/questions-api.service';
import { QuestionOptionApiService } from '../../../core/services/question-option-api.service';
import { PermissionService } from '../../../core/services/permission.service';
import { CommonService } from '../../../core/services/common.service';
import { GlobalFormBuilderService } from '../../../core/services/globalFormBuilder.service';
import { getApiErrorMessage } from '../../../core/utils/api-error.utils';
import {
  QuestionEditor,
  questionExplanationEditorConfig,
  questionRichTextEditorConfig,
  questionOptionEditorConfig,
} from '../../../core/editors/question-rich-text.editor';
import { LessonResponse } from '../../../learncoreservices/models/lesson-response';
import { LessonQuestionResponse } from '../../../learncoreservices/models/lesson-question-response';
import { CreateQuestionForm } from '../../../learncoreservices/models/create-question-form';
import { UpdateQuestionForm } from '../../../learncoreservices/models/update-question-form';
import { CreateQuestionOptionForm } from '../../../learncoreservices/models/create-question-option-form';
import { UpdateQuestionOptionForm } from '../../../learncoreservices/models/update-question-option-form';
import { GetQuestionResponse } from '../../../learncoreservices/models/get-question-response';
import { SharedModule } from '../../../shared/shared.module';

/** Default empty answer slots when creating an MCQ in this modal. */
const MCQ_DEFAULT_OPTION_COUNT = 5;

@Component({
  selector: 'app-lesson-questions-panel',
  standalone: true,
  imports: [
    FormsModule,
    ReactiveFormsModule,
    SharedModule,
    NgbModule,
    MatButtonModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatProgressSpinnerModule,
    MatTooltipModule,
    CKEditorModule,
  ],
  templateUrl: './lesson-questions-panel.component.html',
  styleUrl: './lesson-questions-panel.component.scss',
})
export class LessonQuestionsPanelComponent implements OnChanges {
  @Input() lesson: LessonResponse | null = null;
  @Output() assignmentsChanged = new EventEmitter<void>();

  readonly QuestionEditor = QuestionEditor;
  readonly questionEditorConfig = questionRichTextEditorConfig;
  readonly explanationEditorConfig = questionExplanationEditorConfig;
  readonly optionEditorConfig = questionOptionEditorConfig;

  assignments: LessonQuestionResponse[] = [];
  loading = false;
  loadError = '';

  addSearch = '';
  addCandidates: GetQuestionResponse[] = [];
  addSearchLoading = false;
  addSubmitting = false;
  addError = '';
  selectedQuestionUuid: string | null = null;

  questionForm!: FormGroup;
  questionFormSubmitted = false;
  questionFormError = '';
  questionFormSubmitting = false;
  questionDialogMode: 'create' | 'edit' = 'create';
  editingQuestionUuid: string | null = null;
  editingAssignmentId: number | null = null;

  @ViewChild('addQuestionModal')
  addQuestionModalTpl!: TemplateRef<unknown>;

  @ViewChild('questionFormModal')
  questionFormModalTpl!: TemplateRef<unknown>;

  constructor(
    private readonly lessonQuestionsApi: LessonQuestionsApiService,
    private readonly questionsApi: QuestionsApiService,
    private readonly questionOptionsApi: QuestionOptionApiService,
    private readonly formService: GlobalFormBuilderService,
    private readonly modalService: NgbModal,
    private readonly permissionService: PermissionService,
    private readonly common: CommonService
  ) {
    this.questionForm = this.formService.createQuestionForm();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['lesson']) {
      this.loadAssignments();
    }
  }

  get canAssign(): boolean {
    return this.permissionService.can(PermissionCodes.TENANT_QUESTION_READ);
  }

  get canCreateQuestion(): boolean {
    return this.permissionService.can(PermissionCodes.TENANT_QUESTION_CREATE);
  }

  get canUpdateQuestion(): boolean {
    return this.permissionService.can(PermissionCodes.TENANT_QUESTION_UPDATE);
  }

  get canDeleteQuestion(): boolean {
    return this.permissionService.can(PermissionCodes.TENANT_QUESTION_DELETE);
  }

  get qf() {
    return this.questionForm.controls;
  }

  get options(): FormArray {
    return this.questionForm.get('options') as FormArray;
  }

  stripHtml(html?: string | null): string {
    if (!html) {
      return '—';
    }
    const doc = new DOMParser().parseFromString(html, 'text/html');
    const text = doc.body.textContent?.trim() ?? '';
    return text.length > 120 ? `${text.slice(0, 117)}…` : text || '—';
  }

  loadAssignments(): void {
    if (!this.lesson?.uuid) {
      this.assignments = [];
      return;
    }

    this.loading = true;
    this.loadError = '';

    this.lessonQuestionsApi
      .getLessonQuestions({
        lessonUuid: this.lesson.uuid,
        page: 0,
        size: 200,
        sortBy: 'displayOrder',
        sortDirection: 'ASC',
      })
      .pipe(finalize(() => (this.loading = false)))
      .subscribe({
        next: (page) => {
          this.assignments = page.content ?? [];
        },
        error: (err) => {
          this.assignments = [];
          this.loadError = getApiErrorMessage(
            err,
            'Failed to load lesson questions.'
          );
        },
      });
  }

  openAddExistingModal(): void {
    if (!this.lesson?.uuid) {
      return;
    }
    this.addError = '';
    this.addSearch = '';
    this.addCandidates = [];
    this.selectedQuestionUuid = null;
    this.modalService.open(this.addQuestionModalTpl, {
      size: 'lg',
      modalDialogClass: 'learncore-scrollable-modal',
    });
  }

  searchQuestionsForAdd(): void {
    const term = this.addSearch.trim();
    if (!term || !this.lesson?.categoryUuid) {
      this.addCandidates = [];
      return;
    }

    this.addSearchLoading = true;
    this.questionsApi
      .getQuestions({
        page: 0,
        size: 25,
        questionText: term,
        categoryUuid: this.lesson.categoryUuid,
        sortBy: 'createdDate',
        sortDirection: 'DESC',
      })
      .pipe(finalize(() => (this.addSearchLoading = false)))
      .subscribe({
        next: (page) => {
          const assigned = new Set(
            this.assignments.map((a) => a.questionUuid).filter(Boolean)
          );
          this.addCandidates = (page.content ?? []).filter(
            (q) => q.uuid && !assigned.has(q.uuid)
          );
        },
        error: () => {
          this.addCandidates = [];
        },
      });
  }

  submitAddExisting(modal: NgbModalRef): void {
    if (!this.lesson?.uuid || !this.selectedQuestionUuid) {
      this.addError = 'Select a question to add.';
      return;
    }

    const nextOrder =
      this.assignments.length > 0
        ? Math.max(...this.assignments.map((a) => a.displayOrder ?? 0)) + 1
        : 1;

    this.addSubmitting = true;
    this.addError = '';

    this.lessonQuestionsApi
      .assignQuestion({
        lessonUuid: this.lesson.uuid,
        questionUuid: this.selectedQuestionUuid,
        displayOrder: nextOrder,
      })
      .pipe(finalize(() => (this.addSubmitting = false)))
      .subscribe({
        next: () => {
          modal.close();
          this.loadAssignments();
          this.assignmentsChanged.emit();
        },
        error: (err) => {
          this.addError = getApiErrorMessage(err, 'Failed to add question.');
        },
      });
  }

  openCreateQuestionModal(): void {
    this.openQuestionModal('create');
  }

  openEditQuestionModal(row: LessonQuestionResponse): void {
    if (!row.questionUuid) {
      return;
    }
    this.editingAssignmentId = row.id ?? null;
    this.openQuestionModal('edit', row.questionUuid);
  }

  private openQuestionModal(mode: 'create' | 'edit', questionUuid?: string): void {
    this.questionDialogMode = mode;
    this.questionFormSubmitted = false;
    this.questionFormError = '';
    this.editingQuestionUuid = questionUuid ?? null;

    this.questionForm = this.formService.createQuestionForm();
    this.questionForm.patchValue({
      categoryUuid: this.lesson?.categoryUuid ?? null,
      questionType: 'MCQ',
      difficultyLevel: 'MEDIUM',
    });
    this.syncChoiceOptions(this.questionForm);

    this.modalService.open(this.questionFormModalTpl, {
      size: 'xl',
      backdrop: 'static',
      modalDialogClass: 'learncore-scrollable-modal question-modal-dialog',
    });

    if (mode === 'edit' && questionUuid) {
      this.questionsApi.getQuestion(questionUuid).subscribe({
        next: (question) => {
          this.questionForm.patchValue({
            categoryUuid: question.categoryUuid ?? this.lesson?.categoryUuid,
            questionType: question.questionType ?? 'MCQ',
            difficultyLevel: question.difficultyLevel ?? 'MEDIUM',
            questionText: question.questionText ?? '',
            explanation: question.explanation ?? '',
          });
          this.loadOptionsIntoForm(question);
        },
        error: (err) => {
          this.questionFormError = getApiErrorMessage(
            err,
            'Failed to load question.'
          );
        },
      });
    }
  }

  private loadOptionsIntoForm(question: GetQuestionResponse): void {
    if (!question.uuid || !this.isChoiceType(question.questionType)) {
      return;
    }

    this.questionOptionsApi
      .getQuestionOptions({
        questionUuid: question.uuid,
        page: 0,
        size: 50,
      })
      .subscribe({
        next: (page) => {
          this.clearOptions(this.questionForm);
          for (const opt of page.content ?? []) {
            this.options.push(
              this.formService.createQuestionOptionForm(
                opt.optionText ?? '',
                !!opt.correct
              )
            );
            const last = this.options.at(this.options.length - 1);
            last.patchValue({ uuid: opt.uuid ?? null });
          }
          if (this.options.length === 0) {
            this.syncChoiceOptions(this.questionForm);
          }
        },
      });
  }

  submitQuestionForm(modal: NgbModalRef): void {
    this.questionFormSubmitted = true;
    this.questionFormError = '';

    if (!this.validateQuestionForm()) {
      return;
    }

    const body = this.buildQuestionPayload();
    this.questionFormSubmitting = true;

    if (this.questionDialogMode === 'create') {
      this.questionsApi
        .createQuestion(body as CreateQuestionForm)
        .pipe(
          switchMap((created) => {
            if (!created.uuid) {
              throw new Error('Question created without UUID.');
            }
            return this.createOptionsForQuestion(created.uuid).pipe(
              switchMap(() => this.assignCreatedQuestion(created.uuid!))
            );
          }),
          finalize(() => (this.questionFormSubmitting = false))
        )
        .subscribe({
          next: () => {
            modal.close();
            this.loadAssignments();
            this.assignmentsChanged.emit();
          },
          error: (err) => {
            this.questionFormError = getApiErrorMessage(
              err,
              'Failed to create question.'
            );
          },
        });
      return;
    }

    if (!this.editingQuestionUuid) {
      this.questionFormSubmitting = false;
      return;
    }

    this.questionsApi
      .updateQuestion(
        this.editingQuestionUuid,
        body as UpdateQuestionForm
      )
      .pipe(
        switchMap(() =>
          this.syncOptionsForQuestion(this.editingQuestionUuid!)
        ),
        finalize(() => (this.questionFormSubmitting = false))
      )
      .subscribe({
        next: () => {
          modal.close();
          this.loadAssignments();
          this.assignmentsChanged.emit();
        },
        error: (err) => {
          this.questionFormError = getApiErrorMessage(
            err,
            'Failed to update question.'
          );
        },
      });
  }

  private assignCreatedQuestion(questionUuid: string) {
    if (!this.lesson?.uuid) {
      return of(null);
    }
    const nextOrder =
      this.assignments.length > 0
        ? Math.max(...this.assignments.map((a) => a.displayOrder ?? 0)) + 1
        : 1;
    return this.lessonQuestionsApi.assignQuestion({
      lessonUuid: this.lesson.uuid,
      questionUuid,
      displayOrder: nextOrder,
    });
  }

  async removeFromLesson(row: LessonQuestionResponse): Promise<void> {
    if (row.id == null) {
      return;
    }
    const confirmed = await this.common.confirmDelete(
      'Remove from lesson?',
      'The question stays in the bank but is no longer part of this lesson.'
    );
    if (!confirmed) {
      return;
    }
    this.lessonQuestionsApi.removeAssignment(row.id).subscribe({
      next: () => {
        this.loadAssignments();
        this.assignmentsChanged.emit();
      },
      error: (err) => {
        this.common.showErrorAlert(
          'Error',
          getApiErrorMessage(err, 'Failed to remove question.')
        );
      },
    });
  }

  async deleteQuestionPermanently(row: LessonQuestionResponse): Promise<void> {
    if (!row.questionUuid || !this.canDeleteQuestion) {
      return;
    }
    const confirmed = await this.common.confirmDelete(
      'Delete question?',
      'This removes the question everywhere, not only from this lesson.'
    );
    if (!confirmed) {
      return;
    }
    this.questionsApi.deleteQuestion(row.questionUuid).subscribe({
      next: () => {
        this.loadAssignments();
        this.assignmentsChanged.emit();
      },
      error: (err) => {
        this.common.showErrorAlert(
          'Error',
          getApiErrorMessage(err, 'Failed to delete question.')
        );
      },
    });
  }

  saveDisplayOrder(row: LessonQuestionResponse, value: string): void {
    if (row.id == null) {
      return;
    }
    const order = Number(value);
    if (!Number.isFinite(order) || order < 1) {
      return;
    }
    this.lessonQuestionsApi
      .updateAssignment(row.id, { displayOrder: order })
      .subscribe({
        next: () => {
          this.loadAssignments();
          this.assignmentsChanged.emit();
        },
        error: (err) => {
          this.common.showErrorAlert(
            'Error',
            getApiErrorMessage(err, 'Failed to update order.')
          );
        },
      });
  }

  onQuestionTypeChange(): void {
    this.syncChoiceOptions(this.questionForm);
  }

  addOptionRow(): void {
    this.options.push(this.formService.createQuestionOptionForm());
  }

  removeOptionRow(index: number): void {
    if (this.options.length <= 2) {
      return;
    }
    this.options.removeAt(index);
  }

  setCorrectOption(index: number): void {
    this.options.controls.forEach((control, i) => {
      control.get('correct')?.setValue(i === index);
    });
  }

  isChoiceType(type?: string | null): boolean {
    return type === 'MCQ' || type === 'TRUE_FALSE';
  }

  private validateQuestionForm(): boolean {
    if (this.questionForm.invalid) {
      this.questionForm.markAllAsTouched();
      return false;
    }
    const type = this.questionForm.get('questionType')?.value;
    if (this.isChoiceType(type)) {
      if (this.options.length < 2) {
        this.questionFormError = 'Add at least two options.';
        return false;
      }
      const hasCorrect = this.options.controls.some(
        (c) => !!c.get('correct')?.value
      );
      if (!hasCorrect) {
        this.questionFormError = 'Mark one option as correct.';
        return false;
      }
    }
    return true;
  }

  private buildQuestionPayload(): CreateQuestionForm | UpdateQuestionForm {
    const raw = this.questionForm.getRawValue();
    return {
      categoryUuid: raw.categoryUuid,
      difficultyLevel: raw.difficultyLevel,
      questionType: raw.questionType,
      questionText: String(raw.questionText).trim(),
      explanation: String(raw.explanation ?? '').trim() || undefined,
    };
  }

  private createOptionsForQuestion(questionUuid: string) {
    if (!this.isChoiceType(this.questionForm.get('questionType')?.value)) {
      return of([]);
    }
    const requests = this.options.controls.map((control) => {
      const option = control.getRawValue();
      const body: CreateQuestionOptionForm = {
        questionUuid,
        optionText: String(option.optionText).trim(),
        correct: !!option.correct,
      };
      return this.questionOptionsApi.createQuestionOption(body);
    });
    return requests.length ? forkJoin(requests) : of([]);
  }

  private syncOptionsForQuestion(questionUuid: string) {
    if (!this.isChoiceType(this.questionForm.get('questionType')?.value)) {
      return of(null);
    }
    const requests = this.options.controls.map((control) => {
      const option = control.getRawValue();
      const text = String(option.optionText).trim();
      const correct = !!option.correct;
      if (option.uuid) {
        const body: UpdateQuestionOptionForm = {
          optionText: text,
          correct,
        };
        return this.questionOptionsApi.updateQuestionOption(option.uuid, body);
      }
      const body: CreateQuestionOptionForm = {
        questionUuid,
        optionText: text,
        correct,
      };
      return this.questionOptionsApi.createQuestionOption(body);
    });
    return requests.length ? forkJoin(requests) : of([]);
  }

  private syncChoiceOptions(form: FormGroup): void {
    const type = form.get('questionType')?.value;
    this.clearOptions(form);
    if (type === 'TRUE_FALSE') {
      this.options.push(
        this.formService.createQuestionOptionForm('True', true)
      );
      this.options.push(
        this.formService.createQuestionOptionForm('False', false)
      );
      return;
    }
    if (type === 'MCQ') {
      for (let i = 0; i < MCQ_DEFAULT_OPTION_COUNT; i++) {
        this.options.push(
          this.formService.createQuestionOptionForm('', i === 0)
        );
      }
    }
  }

  private clearOptions(form: FormGroup): void {
    const arr = form.get('options') as FormArray;
    while (arr.length) {
      arr.removeAt(0);
    }
  }
}
