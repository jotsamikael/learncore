import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { UserSession } from '../models/user-session.model';
import { getJwtExpirationMs } from '../utils/jwt.utils';

const SESSION_KEY = 'learncoreSession';

@Injectable({ providedIn: 'root' })
export class SessionService {

  private readonly sessionChanged$ = new BehaviorSubject<UserSession | null>(this.readSession());

  get session$() {
    return this.sessionChanged$.asObservable();
  }

  get permissionsLoaded$() {
    return this.sessionChanged$.asObservable();
  }

  saveSession(session: UserSession): void {
    const normalized = this.withTokenExpiry(session);
    sessionStorage.setItem(SESSION_KEY, JSON.stringify(normalized));
    this.sessionChanged$.next(normalized);
  }

  getSession(): UserSession | null {
    return this.sessionChanged$.value ?? this.readSession();
  }

  clearSession(): void {
    sessionStorage.removeItem(SESSION_KEY);
    sessionStorage.removeItem('currentUser');
    sessionStorage.removeItem('token');
    this.sessionChanged$.next(null);
  }

  isLoggedIn(): boolean {
    const session = this.getSession();
    return !!session?.accessToken && !!session?.refreshToken;
  }

  updateTokens(accessToken: string, refreshToken?: string): void {
    const session = this.getSession();
    if (!session) {
      return;
    }
    this.saveSession({
      ...session,
      accessToken,
      refreshToken: refreshToken ?? session.refreshToken,
    });
  }

  updatePermissions(permissions: string[]): void {
    const session = this.getSession();
    if (!session) {
      return;
    }
    this.saveSession({ ...session, permissions });
  }

  updateTenantSlug(tenantSlug: string): void {
    const session = this.getSession();
    if (!session) {
      return;
    }
    this.saveSession({ ...session, tenantSlug });
  }

  isAccessTokenExpired(bufferMs = 30_000): boolean {
    const session = this.getSession();
    if (!session?.accessToken) {
      return true;
    }
    const expiresAt = session.accessTokenExpiresAt ?? getJwtExpirationMs(session.accessToken);
    if (!expiresAt) {
      return false;
    }
    return Date.now() >= expiresAt - bufferMs;
  }

  isPlatformUser(): boolean {
    return this.getSession()?.tenantUuid == null;
  }

  isTenantUser(): boolean {
    return !this.isPlatformUser() && this.isLoggedIn();
  }

  getPermissions(): string[] {
    return this.getSession()?.permissions ?? [];
  }

  getTenantSlug(): string | null {
    return this.getSession()?.tenantSlug ?? null;
  }

  getAccessToken(): string | null {
    return this.getSession()?.accessToken ?? null;
  }

  getRefreshToken(): string | null {
    return this.getSession()?.refreshToken ?? null;
  }

  private readSession(): UserSession | null {
    try {
      const raw = sessionStorage.getItem(SESSION_KEY);
      if (!raw) {
        return null;
      }
      return this.withTokenExpiry(JSON.parse(raw) as UserSession);
    } catch {
      return null;
    }
  }

  private withTokenExpiry(session: UserSession): UserSession {
    return {
      ...session,
      accessTokenExpiresAt: getJwtExpirationMs(session.accessToken),
    };
  }
}
