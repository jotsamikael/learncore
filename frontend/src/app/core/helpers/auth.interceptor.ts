import { Injectable } from '@angular/core';
import {
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
} from '@angular/common/http';
import { Observable, catchError, switchMap, throwError } from 'rxjs';
import { Router } from '@angular/router';
import { environment } from '../../../environments/environment';
import { IS_RETRY_AFTER_REFRESH } from './http-context.tokens';
import { isPublicAuthUrl } from './public-auth-urls';
import { SessionService } from '../services/session.service';
import { TokenRefreshService } from '../services/token-refresh.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  constructor(
    private readonly session: SessionService,
    private readonly tokenRefresh: TokenRefreshService,
    private readonly router: Router
  ) {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    if (!this.shouldAttachAuth(request.url)) {
      return next.handle(request);
    }

    const token = this.session.getAccessToken();
    const authRequest = token
      ? request.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
      : request;

    return next.handle(authRequest).pipe(
      catchError(error => {
        if (error.status !== 401 || this.shouldSkipRefresh(request.url)) {
          return throwError(() => error);
        }

        if (request.context.get(IS_RETRY_AFTER_REFRESH)) {
          this.session.clearSession();
          this.router.navigate(['/auth/login']);
          return throwError(() => error);
        }

        if (!this.session.getRefreshToken()) {
          this.session.clearSession();
          this.router.navigate(['/auth/login']);
          return throwError(() => error);
        }

        return this.tokenRefresh.refreshAccessToken().pipe(
          switchMap(() => {
            const refreshedToken = this.session.getAccessToken();
            const retryRequest = request.clone({
              context: request.context.set(IS_RETRY_AFTER_REFRESH, true),
              setHeaders: refreshedToken
                ? { Authorization: `Bearer ${refreshedToken}` }
                : {},
            });
            return next.handle(retryRequest);
          }),
          catchError(refreshError => {
            this.session.clearSession();
            this.router.navigate(['/auth/login']);
            return throwError(() => refreshError);
          })
        );
      })
    );
  }

  private shouldAttachAuth(url: string): boolean {
    if (!url.includes(environment.apiUrl)) {
      return environment.defaultauth === 'learncore';
    }
    return !isPublicAuthUrl(url);
  }

  private shouldSkipRefresh(url: string): boolean {
    return isPublicAuthUrl(url) || url.includes('/auth/refresh');
  }
}
