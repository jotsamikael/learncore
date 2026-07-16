import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiConfiguration } from '../../learncoreservices/api-configuration';
import { FindStudentsQuery } from '../../learncoreservices/models/find-students-query';
import { PageStudentResponse } from '../../learncoreservices/models/page-student-response';
import { StudentDetailResponse } from '../../learncoreservices/models/student-detail-response';

@Injectable({ providedIn: 'root' })
export class StudentsApiService {
  constructor(
    private readonly http: HttpClient,
    private readonly apiConfig: ApiConfiguration
  ) {}

  getStudents(query: FindStudentsQuery): Observable<PageStudentResponse> {
    return this.http.get<PageStudentResponse>(`${this.apiConfig.rootUrl}/admin/students`, {
      params: this.toParams(query),
    });
  }

  getStudentDetails(uuid: string): Observable<StudentDetailResponse> {
    return this.http.get<StudentDetailResponse>(`${this.apiConfig.rootUrl}/admin/students/${uuid}`);
  }

  changeStudentStatus(uuid: string): Observable<void> {
    return this.http.patch<void>(`${this.apiConfig.rootUrl}/admin/students/${uuid}/status`, null);
  }

  private toParams(query: FindStudentsQuery): HttpParams {
    let params = new HttpParams();
    const entries: [keyof FindStudentsQuery, string | number | undefined][] = [
      ['page', query.page],
      ['size', query.size],
      ['sortBy', query.sortBy],
      ['sortDirection', query.sortDirection],
      ['firstname', query.firstname],
      ['lastname', query.lastname],
      ['email', query.email],
      ['username', query.username],
      ['tenantUuid', query.tenantUuid],
      ['level', query.level],
      ['xp', query.xp],
      ['streakDays', query.streakDays],
    ];
    for (const [key, value] of entries) {
      if (value !== undefined && value !== null && value !== '') {
        params = params.set(String(key), String(value));
      }
    }
    return params;
  }
}
