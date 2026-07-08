import { Injectable } from '@angular/core';
import { Observable, finalize, map, shareReplay, tap, throwError } from 'rxjs';
import { AuthenticationResponse } from '../../learncoreservices/models/authentication-response';
import { UserSession } from '../models/user-session.model';
import { LearncoreApiService } from './learncore-api.service';
import { SessionService } from './session.service';

@Injectable({ providedIn: 'root' })
export class TokenRefreshService {

  private refreshInFlight$: Observable<UserSession> | null = null;

  constructor(
    private readonly api: LearncoreApiService,
    private readonly session: SessionService
  ) {}

  isRefreshing(): boolean {
    return this.refreshInFlight$ != null;
  }

  refreshAccessToken(): Observable<UserSession> {
    const current = this.session.getSession();
    if (!current?.refreshToken) {
      return throwError(() => new Error('No refresh token available'));
    }

    if (!this.refreshInFlight$) {
      this.refreshInFlight$ = this.api.refreshAccessToken(current.refreshToken).pipe(
        map(response => this.mergeSession(current, response)),
        tap(session => this.session.saveSession(session)),
        finalize(() => {
          this.refreshInFlight$ = null;
        }),
        shareReplay(1)
      );
    }

    return this.refreshInFlight$;
  }

  private mergeSession(current: UserSession, response: AuthenticationResponse): UserSession {
    return {
      ...current,
      accessToken: response.accessToken ?? current.accessToken,
      refreshToken: response.refreshToken ?? current.refreshToken,
      userUuid: response.userUuid ?? current.userUuid,
      tenantUuid: response.tenantUuid ?? current.tenantUuid,
      roles: response.roles ?? current.roles,
    };
  }
}
