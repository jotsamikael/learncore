import { Component, OnInit } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { environment } from '../../../environments/environment';
import { AuthenticationService } from '../../core/services/auth.service';
import { LearncoreAuthService } from '../../core/services/learncore-auth.service';
import { SessionService } from '../../core/services/session.service';
import { GlobalFormBuilderService } from '../../core/services/globalFormBuilder.service';
import { ToastService } from './toast-service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
  standalone: false,
})
export class LoginComponent implements OnInit {

  loginForm!: UntypedFormGroup;
  submitted = false;
  fieldTextType = false;
  error = '';
  loading = false;
  returnUrl!: string;
  isLearncoreAuth = environment.defaultauth === 'learncore';
  year = new Date().getFullYear();

  constructor(
    private formBuilder: GlobalFormBuilderService,
    private authenticationService: AuthenticationService,
    private router: Router,
    private route: ActivatedRoute,
    public toastService: ToastService,
    private learncoreAuth: LearncoreAuthService,
    private session: SessionService
  ) {
    if (this.isLearncoreAuth && this.session.isLoggedIn()) {
      this.router.navigate(['/learncore/dashboard']);
    } else if (this.authenticationService.currentUserValue) {
      this.router.navigate(['/']);
    }
  }

  ngOnInit(): void {
    if (this.isLearncoreAuth && this.session.isLoggedIn()) {
      this.router.navigate(['/learncore/dashboard']);
      return;
    }

    if (!this.isLearncoreAuth && sessionStorage.getItem('currentUser')) {
      this.router.navigate(['/']);
    }

    this.loginForm = this.formBuilder.loginForm();
    this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/learncore/dashboard';
  }

  get f() {
    return this.loginForm.controls;
  }

  onSubmit(): void {
    this.submitted = true;
    this.error = '';

    if (this.loginForm.invalid) {
      return;
    }

    if (!this.isLearncoreAuth) {
      this.router.navigate(['/learncore/dashboard']);
      return;
    }

    this.loading = true;
    this.learncoreAuth.staffLogin({
      email: this.f['email'].value,
      password: this.f['password'].value,
    }).subscribe({
      next: () => {
        this.loading = false;
        this.router.navigateByUrl(this.returnUrl);
      },
      error: (err) => {
        this.loading = false;
        this.error = err?.error?.message ?? 'Invalid email or password';
      },
    });
  }

  toggleFieldTextType(): void {
    this.fieldTextType = !this.fieldTextType;
  }
}
