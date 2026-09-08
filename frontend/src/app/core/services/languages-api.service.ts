import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiConfiguration } from '../../learncoreservices/api-configuration';
import { CreateLanguageRequest } from '../../learncoreservices/models/create-language-request';
import { FindLanguagesQuery } from '../../learncoreservices/models/find-languages-query';
import { LanguageResponse } from '../../learncoreservices/models/language-response';
import { PageLanguageResponse } from '../../learncoreservices/models/page-language-response';
import { UpdateLanguageRequest } from '../../learncoreservices/models/update-language-request';

@Injectable({ providedIn: 'root' })
export class LanguagesApiService {

  constructor(
    private readonly http: HttpClient,
    private readonly apiConfig: ApiConfiguration
  ) {}

  getLanguages(query: FindLanguagesQuery): Observable<PageLanguageResponse> {
    return this.http.get<PageLanguageResponse>(`${this.apiConfig.rootUrl}/languages`, {
      params: this.toParams(query),
    });
  }

  getLanguage(uuid: string): Observable<LanguageResponse> {
    return this.http.get<LanguageResponse>(`${this.apiConfig.rootUrl}/languages/${uuid}`);
  }

  createLanguage(body: CreateLanguageRequest): Observable<LanguageResponse> {
    return this.http.post<LanguageResponse>(`${this.apiConfig.rootUrl}/languages`, body);
  }

  updateLanguage(uuid: string, body: UpdateLanguageRequest): Observable<LanguageResponse> {
    return this.http.patch<LanguageResponse>(`${this.apiConfig.rootUrl}/languages/${uuid}`, body);
  }

  deleteLanguage(uuid: string): Observable<void> {
    return this.http.delete<void>(`${this.apiConfig.rootUrl}/languages/${uuid}`);
  }

  private toParams(query: FindLanguagesQuery): HttpParams {
    let params = new HttpParams();
    const entries: [keyof FindLanguagesQuery, string | number | undefined][] = [
      ['page', query.page],
      ['size', query.size],
      ['sortBy', query.sortBy],
      ['sortDirection', query.sortDirection],
      ['name', query.name],
      ['code', query.code],
    ];
    for (const [key, value] of entries) {
      if (value !== undefined && value !== null && value !== '') {
        params = params.set(String(key), String(value));
      }
    }
    return params;
  }
}
