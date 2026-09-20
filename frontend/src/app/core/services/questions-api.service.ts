import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiConfiguration } from '../../learncoreservices/api-configuration';
import { CreateQuestionForm } from '../../learncoreservices/models/create-question-form';
import { FindQuestionsQuery } from '../../learncoreservices/models/find-questions-query';
import { GetQuestionResponse } from '../../learncoreservices/models/get-question-response';
import { PageGetQuestionResponse } from '../../learncoreservices/models/page-get-question-response';
import { UpdateQuestionForm } from '../../learncoreservices/models/update-question-form';

@Injectable({ providedIn: 'root' })
export class QuestionsApiService {

  constructor(
    private readonly http: HttpClient,
    private readonly apiConfig: ApiConfiguration
  ) {}

  getQuestions(query: FindQuestionsQuery): Observable<PageGetQuestionResponse> {
    return this.http.get<PageGetQuestionResponse>(`${this.apiConfig.rootUrl}/questions`, {
      params: this.toParams(query),
    });
  }

  getQuestion(uuid: string): Observable<GetQuestionResponse> {
    return this.http.get<GetQuestionResponse>(`${this.apiConfig.rootUrl}/questions/${uuid}`);
  }

  createQuestion(body: CreateQuestionForm, image?: File | null): Observable<GetQuestionResponse> {
    return this.http.post<GetQuestionResponse>(
      `${this.apiConfig.rootUrl}/questions`,
      this.toQuestionFormData(body, image)
    );
  }

  updateQuestion(
    uuid: string,
    body: UpdateQuestionForm,
    image?: File | null
  ): Observable<GetQuestionResponse> {
    return this.http.patch<GetQuestionResponse>(
      `${this.apiConfig.rootUrl}/questions/${uuid}`,
      this.toQuestionFormData(body, image)
    );
  }

  deleteQuestion(uuid: string): Observable<void> {
    return this.http.delete<void>(`${this.apiConfig.rootUrl}/questions/${uuid}`);
  }

  private toQuestionFormData(
    body: CreateQuestionForm | UpdateQuestionForm,
    image?: File | null
  ): FormData {
    const formData = new FormData();
    formData.append('categoryUuid', body.categoryUuid);
    formData.append('difficultyLevel', body.difficultyLevel);
    formData.append('questionType', body.questionType);
    formData.append('questionText', body.questionText);
    if (body.explanation) {
      formData.append('explanation', body.explanation);
    }
    if (body.writtenAnswerConfig) {
      formData.append('writtenAnswerConfig', body.writtenAnswerConfig);
    }
    if (image) {
      formData.append('image', image, image.name);
    }
    return formData;
  }

  private toParams(query: FindQuestionsQuery): HttpParams {
    let params = new HttpParams();
    const entries: [keyof FindQuestionsQuery, string | number | undefined][] = [
      ['page', query.page],
      ['size', query.size],
      ['sortBy', query.sortBy],
      ['sortDirection', query.sortDirection],
      ['questionText', query.questionText],
      ['difficultyLevel', query.difficultyLevel],
      ['questionType', query.questionType],
      ['categoryUuid', query.categoryUuid],
    ];
    for (const [key, value] of entries) {
      if (value !== undefined && value !== null && value !== '') {
        params = params.set(String(key), String(value));
      }
    }
    return params;
  }
}
