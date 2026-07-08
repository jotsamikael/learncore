import { Injectable } from '@angular/core';
import { catchError, map, Observable, of, switchMap, tap, throwError } from 'rxjs';
import { AuthenticationResponse } from '../../learncoreservices/models/authentication-response';
import { RoleNames } from '../constants/role-names';
import { UserSession } from '../models/user-session.model';
import { LearncoreApiService, StaffLoginParams } from './learncore-api.service';
import { SessionService } from './session.service';
import { TokenRefreshService } from './token-refresh.service';

export type { StaffLoginParams as StaffLoginRequest };

@Injectable({ providedIn: 'root' })
export class LearncoreAuthService {

  constructor(
    private readonly api: LearncoreApiService,
    private readonly session: SessionService,
    private readonly tokenRefresh: TokenRefreshService
  ) {}

  superAdminLogin(request: StaffLoginParams): Observable<UserSession> {
    const credentials = {
      email: request.email.trim().toLowerCase(),
      password: request.password,
    };
    return this.api.superAdminLogin(credentials).pipe(
      tap(response => this.session.saveSession(this.buildSession(response))),
      switchMap(() => this.loadPermissions()),
      switchMap(() => {
        const session = this.session.getSession();
        if (!session?.roles?.includes(RoleNames.SUPER_ADMIN)) {
          this.session.clearSession();
          return throwError(() => ({
            error: { error: 'This account is not authorized for super admin access.' },
          }));
        }
        return of(session);
      })
    );
  }

  staffLogin(request: StaffLoginParams): Observable<UserSession> {
    const credentials = {
      email: request.email.trim().toLowerCase(),
      password: request.password,
    };

    return this.api.staffLogin(credentials).pipe(
      tap(response => this.session.saveSession(this.buildSession(response))),
      switchMap(() => this.resolveTenantSlug()),
      switchMap(() => this.loadPermissions()),
      switchMap(() => of(this.session.getSession()!))
    );
  }

  loadPermissions(): Observable<string[]> {
    return this.api.getMyPermissions().pipe(
      map(permissions => permissions.map(permission => permission.code ?? '').filter(Boolean)),
      tap(codes => this.session.updatePermissions(codes))
    );
  }

  refresh(): Observable<UserSession> {
    return this.tokenRefresh.refreshAccessToken();
  }

  logout(): Observable<void> {
    const refreshToken = this.session.getRefreshToken();
    if (!refreshToken) {
      this.session.clearSession();
      return of(undefined);
    }

    return this.api.logout(refreshToken).pipe(
      tap(() => this.session.clearSession())
    );
  }

  restoreSessionIfNeeded(): Observable<UserSession | null> {
    if (!this.session.isLoggedIn()) {
      return of(null);
    }

    if (!this.session.isAccessTokenExpired()) {
      return of(this.session.getSession());
    }

    return this.refresh().pipe(
      switchMap(session => of(session)),
    );
  }

  private resolveTenantSlug(): Observable<void> {
    const current = this.session.getSession();
    if (!current?.tenantUuid || current.tenantSlug) {
      return of(undefined);
    }

    return this.api.getOwnTenant().pipe(
      tap(tenant => {
        if (tenant.slug) {
          this.session.updateTenantSlug(tenant.slug);
        }
      }),
      map(() => undefined),
      catchError(() => of(undefined))
    );
  }

  private buildSession(response: AuthenticationResponse): UserSession {
    return {
      firstname: response.firstname ?? '',
      lastname: response.lastname ?? '',
      email: response.email ?? '',
      accessToken: response.accessToken ?? '',
      refreshToken: response.refreshToken ?? '',
      userUuid: response.userUuid ?? '',
      tenantUuid: response.tenantUuid ?? null,
      tenantSlug: null,
      roles: response.roles ?? [],
      permissions: [],
    };
  }
}
