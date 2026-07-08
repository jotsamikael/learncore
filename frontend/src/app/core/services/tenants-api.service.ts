import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiConfiguration } from '../../learncoreservices/api-configuration';
import { CreateTenantRequest } from '../../learncoreservices/models/create-tenant-request';
import { FindTenantsQuery } from '../../learncoreservices/models/find-tenants-query';
import { PageTenantResponse } from '../../learncoreservices/models/page-tenant-response';
import { TenantResponse } from '../../learncoreservices/models/tenant-response';
import { UpdateTenantRequest } from '../../learncoreservices/models/update-tenant-request';

@Injectable({ providedIn: 'root' })
export class TenantsApiService {
  constructor(
    private readonly http: HttpClient,
    private readonly apiConfig: ApiConfiguration
  ) {}

  getTenants(query: FindTenantsQuery): Observable<PageTenantResponse> {
    return this.http.get<PageTenantResponse>(`${this.apiConfig.rootUrl}/platform/tenants`, {
      params: this.toParams(query),
    });
  }

  createTenant(body: CreateTenantRequest, logo?: File | null): Observable<TenantResponse> {
    return this.http.post<TenantResponse>(
      `${this.apiConfig.rootUrl}/platform/tenants`,
      this.toCreateTenantFormData(body, logo)
    );
  }

  private toCreateTenantFormData(body: CreateTenantRequest, logo?: File | null): FormData {
    const formData = new FormData();
    formData.append('name', body.name);
    formData.append('slug', body.slug);
    if (body.examFocus) {
      formData.append('examFocus', body.examFocus);
    }
    if (body.email) {
      formData.append('email', body.email);
    }
    if (body.phone) {
      formData.append('phone', body.phone);
    }
    if (body.country) {
      formData.append('country', body.country);
    }
    if (logo) {
      formData.append('logo', logo, logo.name);
    }
    return formData;
  }

  updateTenant(uuid: string, body: UpdateTenantRequest, logo?: File | null): Observable<TenantResponse> {
    return this.http.patch<TenantResponse>(
      `${this.apiConfig.rootUrl}/platform/tenants/${uuid}`,
      this.toUpdateTenantFormData(body, logo)
    );
  }

  private toUpdateTenantFormData(body: UpdateTenantRequest, logo?: File | null): FormData {
    const formData = new FormData();
    formData.append('name', body.name);
    if (body.examFocus) {
      formData.append('examFocus', body.examFocus);
    }
    if (body.email) {
      formData.append('email', body.email);
    }
    if (body.phone) {
      formData.append('phone', body.phone);
    }
    if (body.country) {
      formData.append('country', body.country);
    }
    if (body.description) {
      formData.append('description', body.description);
    }
    if (body.logoUrl) {
      formData.append('logoUrl', body.logoUrl);
    }
    if (logo) {
      formData.append('logo', logo, logo.name);
    }
    return formData;
  }

  deleteTenant(uuid: string): Observable<void> {
    return this.http.delete<void>(`${this.apiConfig.rootUrl}/platform/tenants/${uuid}`);
  }

  private toParams(query: FindTenantsQuery): HttpParams {
    let params = new HttpParams();
    const entries: [keyof FindTenantsQuery, string | number | undefined][] = [
      ['page', query.page],
      ['size', query.size],
      ['sortBy', query.sortBy],
      ['sortDirection', query.sortDirection],
      ['name', query.name],
      ['examFocus', query.examFocus],
      ['country', query.country],
    ];
    for (const [key, value] of entries) {
      if (value !== undefined && value !== null && value !== '') {
        params = params.set(String(key), String(value));
      }
    }
    return params;
  }
}