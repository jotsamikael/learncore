import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiConfiguration } from '../../learncoreservices/api-configuration';
import { CreateTenantStaffRequest } from '../../learncoreservices/models/create-tenant-staff-request';
import { FindStaffQuery } from '../../learncoreservices/models/find-staff-query';
import { PageStaffResponse } from '../../learncoreservices/models/page-staff-response';
import { StaffResponse } from '../../learncoreservices/models/staff-response';
import { TenantStaffDetailsResponse } from '../../learncoreservices/models/tenant-staff-details-response';
import { UpdateTenantStaffRequest } from '../../learncoreservices/models/update-tenant-staff-request';

@Injectable({ providedIn: 'root' })
export class TenantStaffApiService {
  constructor(
    private readonly http: HttpClient,
    private readonly apiConfig: ApiConfiguration
  ) {}

  getTenantStaff(query: FindStaffQuery, tenantUuid?: string): Observable<PageStaffResponse> {
    return this.http.get<PageStaffResponse>(`${this.apiConfig.rootUrl}/admin/staff`, {
      params: this.toParams(query, tenantUuid),
    });
  }

  getTenantStaffDetails(uuid: string): Observable<TenantStaffDetailsResponse> {
    return this.http.get<TenantStaffDetailsResponse>(`${this.apiConfig.rootUrl}/admin/staff/${uuid}`);
  }

  createTenantStaff(body: CreateTenantStaffRequest): Observable<StaffResponse> {
    return this.http.post<StaffResponse>(`${this.apiConfig.rootUrl}/admin/staff`, body);
  }

  updateTenantStaff(uuid: string, body: UpdateTenantStaffRequest): Observable<StaffResponse> {
    return this.http.patch<StaffResponse>(`${this.apiConfig.rootUrl}/admin/staff/${uuid}`, body);
  }

  changeStaffStatus(uuid: string): Observable<StaffResponse> {
    return this.http.patch<StaffResponse>(
      `${this.apiConfig.rootUrl}/admin/staff/${uuid}/status`,
      null
    );
  }

  private toParams(query: FindStaffQuery, tenantUuid?: string): HttpParams {
    let params = new HttpParams();
    if (tenantUuid) {
      params = params.set('tenantUuid', tenantUuid);
    }
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
