import { Injectable } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  CanActivate,
  CanActivateChild,
  Router,
  RouterStateSnapshot,
  UrlTree,
} from '@angular/router';
import { Observable, catchError, map, of } from 'rxjs';
import { SessionService } from '../services/session.service';
import { TokenRefreshService } from '../services/token-refresh.service';

@Injectable({ providedIn: 'root' })
export class AuthGuard implements CanActivate, CanActivateChild {

  constructor(
    private readonly router: Router,
    private readonly session: SessionService,
    private readonly tokenRefresh: TokenRefreshService
  ) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    return this.checkAuth(state.url);
  }

  canActivateChild(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    return this.checkAuth(state.url);
  }

  private checkAuth(returnUrl: string): Observable<boolean | UrlTree> {
    if (!this.session.isLoggedIn()) {
      return of(this.router.createUrlTree(['/auth/login'], {
        queryParams: { returnUrl },
      }));
    }

    if (!this.session.isAccessTokenExpired()) {
      return of(true);
    }

    return this.tokenRefresh.refreshAccessToken().pipe(
      map(() => true),
      catchError(() => of(this.router.createUrlTree(['/auth/login'], {
        queryParams: { returnUrl },
      })))
    );
  }
}
