import { Component, OnInit } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { environment } from '../../../environments/environment';
import { RoleNames } from '../../core/constants/role-names';
import { AuthenticationService } from '../../core/services/auth.service';
import { GlobalFormBuilderService } from '../../core/services/globalFormBuilder.service';
import { LearncoreAuthService } from '../../core/services/learncore-auth.service';
import { PermissionService } from '../../core/services/permission.service';
import { SessionService } from '../../core/services/session.service';
import { getApiErrorMessage } from '../../core/utils/api-error.utils';
import { ToastService } from '../login/toast-service';

@Component({
  selector: 'app-superadminlogin',
  templateUrl: './superadminlogin.component.html',
  styleUrls: ['./superadminlogin.component.scss'],
  standalone: false,
})
export class SuperadminloginComponent implements OnInit {

  private static readonly DEFAULT_RETURN_URL = '/learncore/platform/tenants';

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
    private session: SessionService,
    private permissionService: PermissionService
  ) {
    this.redirectIfAlreadyAuthenticated();
  }

  ngOnInit(): void {
    if (this.isLearncoreAuth && this.session.isLoggedIn()) {
      this.redirectIfAlreadyAuthenticated();
      return;
    }

    if (!this.isLearncoreAuth && sessionStorage.getItem('currentUser')) {
      this.router.navigate(['/']);
      return;
    }

    this.loginForm = this.formBuilder.loginForm();
    this.returnUrl = this.route.snapshot.queryParams['returnUrl']
      || SuperadminloginComponent.DEFAULT_RETURN_URL;
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
      this.router.navigate([SuperadminloginComponent.DEFAULT_RETURN_URL]);
      return;
    }

    this.loading = true;
    this.learncoreAuth.superAdminLogin({
      email: this.f['email'].value,
      password: this.f['password'].value,
    }).subscribe({
      next: () => {
        this.loading = false;
        this.router.navigateByUrl(this.returnUrl);
      },
      error: (err) => {
        this.loading = false;
        this.error = getApiErrorMessage(err, 'Invalid email or password');
      },
    });
  }

  toggleFieldTextType(): void {
    this.fieldTextType = !this.fieldTextType;
  }

  private redirectIfAlreadyAuthenticated(): void {
    if (!this.isLearncoreAuth) {
      if (this.authenticationService.currentUserValue) {
        this.router.navigate(['/']);
      }
      return;
    }

    if (!this.session.isLoggedIn()) {
      return;
    }

    if (this.permissionService.hasRole(RoleNames.SUPER_ADMIN)) {
      this.router.navigate([SuperadminloginComponent.DEFAULT_RETURN_URL]);
      return;
    }

    this.router.navigate(['/learncore/dashboard']);
  }
}
