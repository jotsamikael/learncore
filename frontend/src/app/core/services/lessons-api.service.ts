import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiConfiguration } from '../../learncoreservices/api-configuration';
import { CreateLessonRequest } from '../../learncoreservices/models/create-lesson-request';
import { LessonResponse } from '../../learncoreservices/models/lesson-response';
import { PageLessonResponse } from '../../learncoreservices/models/page-lesson-response';
import { UpdateLessonRequest } from '../../learncoreservices/models/update-lesson-request';

export interface FindLessonsQuery {
  page?: number;
  size?: number;
  sortBy?: string;
  sortDirection?: 'ASC' | 'DESC';
  title?: string;
  categoryUuid?: string;
  isPremium?: boolean;
}

@Injectable({ providedIn: 'root' })
export class LessonsApiService {
  constructor(
    private readonly http: HttpClient,
    private readonly apiConfig: ApiConfiguration
  ) {}

  getLessons(query: FindLessonsQuery): Observable<PageLessonResponse> {
    return this.http.get<PageLessonResponse>(`${this.apiConfig.rootUrl}/lessons`, {
      params: this.toParams(query),
    });
  }

  getLesson(uuid: string): Observable<LessonResponse> {
    return this.http.get<LessonResponse>(`${this.apiConfig.rootUrl}/lessons/${uuid}`);
  }

  createLesson(body: CreateLessonRequest): Observable<LessonResponse> {
    return this.http.post<LessonResponse>(`${this.apiConfig.rootUrl}/lessons`, body);
  }

  updateLesson(uuid: string, body: UpdateLessonRequest): Observable<LessonResponse> {
    return this.http.patch<LessonResponse>(`${this.apiConfig.rootUrl}/lessons/${uuid}`, body);
  }

  deleteLesson(uuid: string): Observable<void> {
    return this.http.delete<void>(`${this.apiConfig.rootUrl}/lessons/${uuid}`);
  }

  private toParams(query: FindLessonsQuery): HttpParams {
    let params = new HttpParams();
    const entries: [keyof FindLessonsQuery, string | number | boolean | undefined][] = [
      ['page', query.page],
      ['size', query.size],
      ['sortBy', query.sortBy],
      ['sortDirection', query.sortDirection],
      ['title', query.title],
      ['categoryUuid', query.categoryUuid],
      ['isPremium', query.isPremium],
    ];
    for (const [key, value] of entries) {
      if (value !== undefined && value !== null && value !== '') {
        params = params.set(String(key), String(value));
      }
    }
    return params;
  }
}
