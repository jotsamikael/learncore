import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiConfiguration } from '../../learncoreservices/api-configuration';
import { CreateRoleRequest } from '../../learncoreservices/models/create-role-request';
import { FindRolesQuery } from '../../learncoreservices/models/find-roles-query';
import { PageRoleResponse } from '../../learncoreservices/models/page-role-response';
import { PermissionResponse } from '../../learncoreservices/models/permission-response';
import { RoleResponse } from '../../learncoreservices/models/role-response';
import { UpdateRoleRequest } from '../../learncoreservices/models/update-role-request';

@Injectable({ providedIn: 'root' })
export class RolesApiService {

  constructor(
    private readonly http: HttpClient,
    private readonly apiConfig: ApiConfiguration
  ) {}

  getRoles(query: FindRolesQuery): Observable<PageRoleResponse> {
    return this.http.get<PageRoleResponse>(`${this.apiConfig.rootUrl}/roles`, {
      params: this.toParams(query),
    });
  }

  createRole(body: CreateRoleRequest): Observable<RoleResponse> {
    return this.http.post<RoleResponse>(`${this.apiConfig.rootUrl}/roles`, body);
  }

  updateRole(uuid: string, body: UpdateRoleRequest): Observable<RoleResponse> {
    return this.http.patch<RoleResponse>(`${this.apiConfig.rootUrl}/roles/${uuid}`, body);
  }

  deleteRole(uuid: string): Observable<void> {
    return this.http.delete<void>(`${this.apiConfig.rootUrl}/roles/${uuid}`);
  }

  listAssignablePermissions(): Observable<PermissionResponse[]> {
    return this.http.get<PermissionResponse[]>(`${this.apiConfig.rootUrl}/permissions`);
  }

  private toParams(query: FindRolesQuery): HttpParams {
    let params = new HttpParams();
    const entries: [keyof FindRolesQuery, string | number | undefined][] = [
      ['page', query.page],
      ['size', query.size],
      ['sortBy', query.sortBy],
      ['sortDirection', query.sortDirection],
      ['name', query.name],
      ['description', query.description],
      ['roleLevel', query.roleLevel],
    ];
    for (const [key, value] of entries) {
      if (value !== undefined && value !== null && value !== '') {
        params = params.set(String(key), String(value));
      }
    }
    return params;
  }
}
