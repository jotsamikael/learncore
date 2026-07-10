import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiConfiguration } from '../../learncoreservices/api-configuration';
import { CreatePlatformStaffRequest } from '../../learncoreservices/models/create-platform-staff-request';
import { CreateTenantStaffRequest } from '../../learncoreservices/models/create-tenant-staff-request';
import { FindStaffQuery } from '../../learncoreservices/models/find-staff-query';
import { PageStaffResponse } from '../../learncoreservices/models/page-staff-response';
import { PlatformStaffDetailsResponse } from '../../learncoreservices/models/platform-staff-details-response';
import { StaffResponse } from '../../learncoreservices/models/staff-response';
import { UpdatePlatformStaffRequest } from '../../learncoreservices/models/update-platform-staff-request';

@Injectable({ providedIn: 'root' })
export class PlatformStaffApiService {
  constructor(
    private readonly http: HttpClient,
    private readonly apiConfig: ApiConfiguration
  ) {}

  getPlatformStaff(query: FindStaffQuery): Observable<PageStaffResponse> {
    return this.http.get<PageStaffResponse>(`${this.apiConfig.rootUrl}/platform/staff`, {
      params: this.toParams(query),
    });
  }

  getPlatformStaffDetails(uuid: string): Observable<PlatformStaffDetailsResponse> {
    return this.http.get<PlatformStaffDetailsResponse>(
      `${this.apiConfig.rootUrl}/platform/staff/${uuid}`
    );
  }

  createPlatformStaff(body: CreatePlatformStaffRequest): Observable<StaffResponse> {
    return this.http.post<StaffResponse>(`${this.apiConfig.rootUrl}/platform/staff`, body);
  }

  createTenantAdmin(body: CreateTenantStaffRequest): Observable<StaffResponse> {
    return this.http.post<StaffResponse>(
      `${this.apiConfig.rootUrl}/platform/staff/tenant-admin`,
      body
    );
  }

  updatePlatformStaff(uuid: string, body: UpdatePlatformStaffRequest): Observable<StaffResponse> {
    return this.http.patch<StaffResponse>(
      `${this.apiConfig.rootUrl}/platform/staff/${uuid}`,
      body
    );
  }

  changeStaffStatus(uuid: string): Observable<StaffResponse> {
    return this.http.patch<StaffResponse>(
      `${this.apiConfig.rootUrl}/platform/staff/${uuid}/status`,
      null
    );
  }

  private toParams(query: FindStaffQuery): HttpParams {
    let params = new HttpParams();
    const entries: [keyof FindStaffQuery, string | number | undefined][] = [
      ['page', query.page],
      ['size', query.size],
      ['sortBy', query.sortBy],
      ['sortDirection', query.sortDirection],
      ['firstname', query.firstname],
      ['lastname', query.lastname],
      ['email', query.email],
      ['positionName', query.positionName],
    ];
    for (const [key, value] of entries) {
      if (value !== undefined && value !== null && value !== '') {
        params = params.set(String(key), String(value));
      }
    }
    return params;
  }
}
