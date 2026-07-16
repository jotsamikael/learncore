import { Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { NgbNavModule } from '@ng-bootstrap/ng-bootstrap';
import { Subject, finalize, takeUntil } from 'rxjs';
import { SharedModule } from '../../../shared/shared.module';
import { ProfileApiService } from '../../../core/services/profile-api.service';
import { SessionService } from '../../../core/services/session.service';
import { getApiErrorMessage } from '../../../core/utils/api-error.utils';
import { UserProfileResponse } from '../../../learncoreservices/models/user-profile-response';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [ReactiveFormsModule, SharedModule, NgbNavModule],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.scss',
})
export class ProfileComponent implements OnInit, OnDestroy {
  @ViewChild('avatarInput') avatarInput!: ElementRef<HTMLInputElement>;

  profileForm!: FormGroup;
  profile: UserProfileResponse | null = null;
  breadCrumbItems!: Array<{ label?: string; active?: boolean }>;

  isLoading = true;
  isSaving = false;
  loadError = '';
  saveError = '';
  saveSuccess = '';
  formSubmitted = false;

  avatarPreviewUrl: string | null = null;
  avatarFile: File | null = null;
  avatarError = '';

  readonly usernameMax = 50;
  activeTab = 1;

  private readonly destroy$ = new Subject<void>();
  private objectUrl: string | null = null;

  constructor(
    private readonly profileApi: ProfileApiService,
    private readonly session: SessionService
  ) {}

  ngOnInit(): void {
    this.breadCrumbItems = [
      { label: 'Learncore' },
      { label: 'Profile', active: true },
    ];

    this.profileForm = new FormGroup({
      firstname: new FormControl('', [Validators.required, Validators.maxLength(100)]),
      lastname: new FormControl('', [Validators.required, Validators.maxLength(100)]),
      username: new FormControl('', [Validators.maxLength(this.usernameMax)]),
      email: new FormControl({ value: '', disabled: true }, [Validators.required, Validators.email]),
      phone: new FormControl('', [
        Validators.maxLength(30),
        Validators.pattern(/^\+?[0-9\s\-()]{7,30}$/),
      ]),
    });

    this.loadProfile();
  }

  ngOnDestroy(): void {
    this.revokeObjectUrl();
    this.destroy$.next();
    this.destroy$.complete();
  }

  get f() {
    return this.profileForm.controls;
  }

  get displayName(): string {
    const first = this.profileForm.get('firstname')?.value?.trim() || this.profile?.firstname || '';
    const last = this.profileForm.get('lastname')?.value?.trim() || this.profile?.lastname || '';
    const name = `${first} ${last}`.trim();
    return name || this.profile?.email || 'My Profile';
  }

  get avatarSrc(): string {
    return (
      this.avatarPreviewUrl ||
      this.profile?.avatarUrl ||
      'assets/images/users/user-dummy-img.jpg'
    );
  }

  get usernameLength(): number {
    return String(this.profileForm.get('username')?.value ?? '').length;
  }

  loadProfile(): void {
    this.isLoading = true;
    this.loadError = '';
    this.profileApi
      .getMyProfile()
      .pipe(
        takeUntil(this.destroy$),
        finalize(() => (this.isLoading = false))
      )
      .subscribe({
        next: (profile) => {
          this.profile = profile;
          this.patchForm(profile);
        },
        error: (err) => {
          this.loadError = getApiErrorMessage(err, 'Failed to load profile.');
        },
      });
  }

  openAvatarPicker(): void {
    this.avatarInput?.nativeElement?.click();
  }

  onAvatarSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    input.value = '';
    this.avatarError = '';

    if (!file) {
      return;
    }

    if (!file.type.startsWith('image/')) {
      this.avatarError = 'Please select an image file (JPEG, PNG, WebP, or GIF).';
      return;
    }

    if (file.size > 5 * 1024 * 1024) {
      this.avatarError = 'Image must be 5 MB or smaller.';
      return;
    }

    this.avatarFile = file;
    this.revokeObjectUrl();
    this.objectUrl = URL.createObjectURL(file);
    this.avatarPreviewUrl = this.objectUrl;
  }

  submitProfile(): void {
    this.formSubmitted = true;
    this.saveError = '';
    this.saveSuccess = '';

    if (this.profileForm.invalid) {
      return;
    }

    const raw = this.profileForm.getRawValue();
    this.isSaving = true;
    this.profileApi
      .updateMyProfile(
        {
          firstname: String(raw.firstname).trim(),
          lastname: String(raw.lastname).trim(),
          username: String(raw.username ?? '').trim() || undefined,
          email: String(raw.email ?? '').trim() || undefined,
          phone: String(raw.phone ?? '').trim() || undefined,
        },
        this.avatarFile
      )
      .pipe(finalize(() => (this.isSaving = false)))
      .subscribe({
        next: (updated) => {
          this.profile = updated;
          this.avatarFile = null;
          this.revokeObjectUrl();
          this.avatarPreviewUrl = null;
          this.patchForm(updated);
          this.saveSuccess = 'Profile updated successfully.';
          this.syncSession(updated);
        },
        error: (err) => {
          this.saveError = getApiErrorMessage(err, 'Failed to update profile.');
        },
      });
  }

  private patchForm(profile: UserProfileResponse): void {
    this.profileForm.patchValue({
      firstname: profile.firstname ?? '',
      lastname: profile.lastname ?? '',
      username: profile.username ?? '',
      email: profile.email ?? '',
      phone: '',
    });
  }

  private syncSession(profile: UserProfileResponse): void {
    const session = this.session.getSession();
    if (!session) {
      return;
    }
    this.session.saveSession({
      ...session,
      firstname: profile.firstname ?? session.firstname,
      lastname: profile.lastname ?? session.lastname,
      email: profile.email ?? session.email,
    });
  }

  private revokeObjectUrl(): void {
    if (this.objectUrl) {
      URL.revokeObjectURL(this.objectUrl);
      this.objectUrl = null;
    }
  }
}
