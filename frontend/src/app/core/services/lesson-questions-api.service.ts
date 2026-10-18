import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiConfiguration } from '../../learncoreservices/api-configuration';
import { AssignLessonQuestionRequest } from '../../learncoreservices/models/assign-lesson-question-request';
import { LessonQuestionResponse } from '../../learncoreservices/models/lesson-question-response';
import { PageLessonQuestionResponse } from '../../learncoreservices/models/page-lesson-question-response';
import { UpdateLessonQuestionRequest } from '../../learncoreservices/models/update-lesson-question-request';

export interface FindLessonQuestionsQuery {
  page?: number;
  size?: number;
  sortBy?: string;
  sortDirection?: 'ASC' | 'DESC';
  lessonUuid?: string;
  questionUuid?: string;
}

@Injectable({ providedIn: 'root' })
export class LessonQuestionsApiService {
  constructor(
    private readonly http: HttpClient,
    private readonly apiConfig: ApiConfiguration
  ) {}

  getLessonQuestions(
    query: FindLessonQuestionsQuery
  ): Observable<PageLessonQuestionResponse> {
    return this.http.get<PageLessonQuestionResponse>(
      `${this.apiConfig.rootUrl}/lesson-questions`,
      { params: this.toParams(query) }
    );
  }

  assignQuestion(body: AssignLessonQuestionRequest): Observable<LessonQuestionResponse> {
    return this.http.post<LessonQuestionResponse>(
      `${this.apiConfig.rootUrl}/lesson-questions`,
      body
    );
  }

  updateAssignment(
    id: number,
    body: UpdateLessonQuestionRequest
  ): Observable<LessonQuestionResponse> {
    return this.http.patch<LessonQuestionResponse>(
      `${this.apiConfig.rootUrl}/lesson-questions/${id}`,
      body
    );
  }

  removeAssignment(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiConfig.rootUrl}/lesson-questions/${id}`);
  }

  private toParams(query: FindLessonQuestionsQuery): HttpParams {
    let params = new HttpParams();
    const entries: [keyof FindLessonQuestionsQuery, string | number | undefined][] = [
      ['page', query.page],
      ['size', query.size],
      ['sortBy', query.sortBy],
      ['sortDirection', query.sortDirection],
      ['lessonUuid', query.lessonUuid],
      ['questionUuid', query.questionUuid],
    ];
    for (const [key, value] of entries) {
      if (value !== undefined && value !== null && value !== '') {
        params = params.set(String(key), String(value));
      }
    }
    return params;
  }
}
