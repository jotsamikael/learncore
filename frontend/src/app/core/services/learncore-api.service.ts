import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ApiConfiguration } from '../../learncoreservices/api-configuration';
import { logout } from '../../learncoreservices/fn/authentication/logout';
import { refresh } from '../../learncoreservices/fn/authentication/refresh';
import { staffLogin } from '../../learncoreservices/fn/authentication/staff-login';
import { superAdminLogin } from '../../learncoreservices/fn/authentication/super-admin-login';
import { getOwnTenant } from '../../learncoreservices/fn/tenant-admin-settings/get-own-tenant';
import { viewPermissions } from '../../learncoreservices/fn/user-profile/view-permissions';
import { AuthenticationResponse } from '../../learncoreservices/models/authentication-response';
import { PermissionResponse } from '../../learncoreservices/models/permission-response';
import { TenantResponse } from '../../learncoreservices/models/tenant-response';

export interface StaffLoginParams {
  email: string;
  password: string;
}

@Injectable({ providedIn: 'root' })
export class LearncoreApiService {

  constructor(
    private readonly http: HttpClient,
    private readonly apiConfig: ApiConfiguration
  ) {}

  staffLogin(params: StaffLoginParams): Observable<AuthenticationResponse> {
    return staffLogin(this.http, this.apiConfig.rootUrl, {
      body: {
        email: params.email,
        password: params.password,
      },
    }).pipe(map(response => response.body));
  }

  superAdminLogin(params: StaffLoginParams): Observable<AuthenticationResponse> {
    return superAdminLogin(this.http, this.apiConfig.rootUrl, {
      body: {
        email: params.email,
        password: params.password,
      },
    }).pipe(map(response => response.body));
  }

  refreshAccessToken(refreshToken: string): Observable<AuthenticationResponse> {
    return refresh(this.http, this.apiConfig.rootUrl, {
      body: { refreshToken },
    }).pipe(map(response => response.body));
  }

  logout(refreshToken: string): Observable<void> {
    return logout(this.http, this.apiConfig.rootUrl, {
      body: { refreshToken },
    }).pipe(map(() => undefined));
  }

  getMyPermissions(): Observable<PermissionResponse[]> {
    return viewPermissions(this.http, this.apiConfig.rootUrl).pipe(
      map(response => response.body)
    );
  }

  getOwnTenant(): Observable<TenantResponse> {
    return getOwnTenant(this.http, this.apiConfig.rootUrl).pipe(
      map(response => response.body)
    );
  }
}
