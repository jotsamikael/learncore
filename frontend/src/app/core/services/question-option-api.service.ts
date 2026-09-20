import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiConfiguration } from '../../learncoreservices/api-configuration';
import { CreateQuestionOptionForm } from '../../learncoreservices/models/create-question-option-form';
import { FindQuestionOptionsQuery } from '../../learncoreservices/models/find-question-options-query';
import { PageQuestionOptionResponseDto } from '../../learncoreservices/models/page-question-option-response-dto';
import { QuestionOptionResponseDto } from '../../learncoreservices/models/question-option-response-dto';
import { UpdateQuestionOptionForm } from '../../learncoreservices/models/update-question-option-form';

@Injectable({ providedIn: 'root' })
export class QuestionOptionApiService {

  constructor(
    private readonly http: HttpClient,
    private readonly apiConfig: ApiConfiguration
  ) {}

  getQuestionOptions(query: FindQuestionOptionsQuery): Observable<PageQuestionOptionResponseDto> {
    return this.http.get<PageQuestionOptionResponseDto>(`${this.apiConfig.rootUrl}/question-options`, {
      params: this.toParams(query),
    });
  }

  getQuestionOption(uuid: string): Observable<QuestionOptionResponseDto> {
    return this.http.get<QuestionOptionResponseDto>(
      `${this.apiConfig.rootUrl}/question-options/${uuid}`
    );
  }

  createQuestionOption(
    body: CreateQuestionOptionForm,
    image?: File | null
  ): Observable<QuestionOptionResponseDto> {
    return this.http.post<QuestionOptionResponseDto>(
      `${this.apiConfig.rootUrl}/question-options`,
      this.toCreateFormData(body, image)
    );
  }

  updateQuestionOption(
    uuid: string,
    body: UpdateQuestionOptionForm,
    image?: File | null
  ): Observable<QuestionOptionResponseDto> {
    return this.http.patch<QuestionOptionResponseDto>(
      `${this.apiConfig.rootUrl}/question-options/${uuid}`,
      this.toUpdateFormData(body, image)
    );
  }

  deleteQuestionOption(uuid: string): Observable<void> {
    return this.http.delete<void>(`${this.apiConfig.rootUrl}/question-options/${uuid}`);
  }

  private toCreateFormData(body: CreateQuestionOptionForm, image?: File | null): FormData {
    const formData = new FormData();
    formData.append('questionUuid', body.questionUuid);
    formData.append('optionText', body.optionText);
    formData.append('correct', String(body.correct ?? false));
    if (image) {
      formData.append('image', image, image.name);
    }
    return formData;
  }

  private toUpdateFormData(body: UpdateQuestionOptionForm, image?: File | null): FormData {
    const formData = new FormData();
    formData.append('optionText', body.optionText);
    formData.append('correct', String(body.correct ?? false));
    if (image) {
      formData.append('image', image, image.name);
    }
    return formData;
  }

  private toParams(query: FindQuestionOptionsQuery): HttpParams {
    let params = new HttpParams();
    const entries: [keyof FindQuestionOptionsQuery, string | number | undefined][] = [
      ['page', query.page],
      ['size', query.size],
      ['sortBy', query.sortBy],
      ['sortDirection', query.sortDirection],
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
