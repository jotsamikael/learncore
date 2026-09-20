import { Injectable } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import ClassicEditor from '@ckeditor/ckeditor5-build-classic';

@Injectable({
  providedIn: 'root',
})
export class GlobalFormBuilderService {
  constructor(private fb: FormBuilder) {}
  public Editor = ClassicEditor;

  loginForm(): FormGroup {
    return this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: [
        '',
        [Validators.required, Validators.minLength(6), Validators.maxLength(64)],
      ],
    });
  }

  createRoleForm(): FormGroup {
    return this.fb.group({
      name: ['', [Validators.required, Validators.minLength(1)]],
      description: ['', [Validators.required, Validators.minLength(1)]],
      permissionCodes: [[], Validators.required],
    });
  }

  updateRoleForm(): FormGroup {
    return this.createRoleForm();
  }

  createTenantForm(): FormGroup {
    return this.fb.group({
      name: ['', [Validators.required, Validators.minLength(1)]],
      slug: [
        '',
        [
          Validators.required,
          Validators.minLength(1),
          Validators.maxLength(80),
          Validators.pattern(/^[a-z0-9]+(?:-[a-z0-9]+)*$/),
        ],
      ],
      examFocus: ['', [Validators.required, Validators.minLength(1)]],
      countryOption: [null, Validators.required],
      country: ['', [Validators.required]],
      phoneLocal: ['', [Validators.required, Validators.pattern(/^[0-9]{6,15}$/)]],
      phone: ['', [Validators.required]],
      email: ['', [Validators.required, Validators.email]],
      description: ['', [Validators.required, Validators.minLength(1)]],
    });
  }

  updateTenantForm(): FormGroup {
    return this.fb.group({
      name: ['', [Validators.required, Validators.minLength(1)]],
      examFocus: ['', [Validators.required, Validators.minLength(1)]],
      countryOption: [null, Validators.required],
      country: ['', [Validators.required]],
      phoneLocal: ['', [Validators.required, Validators.pattern(/^[0-9]{6,15}$/)]],
      phone: ['', [Validators.required]],
      email: ['', [Validators.required, Validators.email]],
      description: ['', [Validators.required, Validators.minLength(1)]],
    });
  }

  passwordMatchValidator(form: FormGroup): null {
    const password = form.get('password')?.value;
    const confirmPassword = form.get('confirmPassword')?.value;

    if (password !== confirmPassword) {
      form.get('confirmPassword')?.setErrors({ passwordMismatch: true });
    } else {
      const errors = form.get('confirmPassword')?.errors;
      if (errors) {
        delete errors['passwordMismatch'];
        if (Object.keys(errors).length === 0) {
          form.get('confirmPassword')?.setErrors(null);
        } else {
          form.get('confirmPassword')?.setErrors(errors);
        }
      }
    }

    return null;
  }

  createCategoryForm(): FormGroup {
    return this.fb.group({
      name: ['', [Validators.required, Validators.minLength(1)]],
      slug: ['', [Validators.required, Validators.pattern(/^[a-z0-9]+(?:-[a-z0-9]+)*$/)]],
      description: ['', [Validators.required, Validators.minLength(1)]],
      parentUuid: [null],
      languageUuid: [null],
    });
  }
  
  updateCategoryForm(): FormGroup {
    return this.createCategoryForm();
  }

  createLanguageForm(): FormGroup {
    return this.fb.group({
      name: ['', [Validators.required, Validators.minLength(1)]],
      code: ['', [Validators.required, Validators.minLength(1)]],
    });
  }
  
  updateLanguageForm(): FormGroup {
    return this.createLanguageForm();
  }

  createQuestionForm(): FormGroup {
    return this.fb.group({
      questionType: ['MCQ', Validators.required],
      categoryUuid: [null, Validators.required],
      difficultyLevel: ['MEDIUM', Validators.required],
      questionText: ['', [Validators.required, Validators.minLength(1)]],
      explanation: [''],
      options: this.fb.array([]),
      referenceAnswer: [''],
      maxScore: [1, [Validators.min(0)]],
      gradingStrategy: ['KEYWORD_MATCH'],
      minimumScoreThreshold: [0.7, [Validators.min(0), Validators.max(1)]],
      gradingCriteria: this.fb.array([]),
    });
  }

  updateQuestionForm(): FormGroup {
    return this.createQuestionForm();
  }

  createQuestionOptionForm(optionText = '', correct = false): FormGroup {
    return this.fb.group({
      uuid: [null],
      optionText: [optionText, Validators.required],
      correct: [correct],
      imageUrl: [null],
      image: [null],
    });
  }

  createGradingCriterionForm(): FormGroup {
    return this.fb.group({
      title: ['', Validators.required],
      description: [''],
      score: [1, [Validators.required, Validators.min(0)]],
      keywords: this.fb.array([]),
    });
  }
}
