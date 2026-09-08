import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiConfiguration } from '../../learncoreservices/api-configuration';
import { CategoryResponse } from '../../learncoreservices/models/category-response';
import { CreateCategoryForm } from '../../learncoreservices/models/create-category-form';
import { FindCategoriesQuery } from '../../learncoreservices/models/find-categories-query';
import { PageCategoryResponse } from '../../learncoreservices/models/page-category-response';
import { UpdateCategoryForm } from '../../learncoreservices/models/update-category-form';

@Injectable({ providedIn: 'root' })
export class CategoriesApiService {

  constructor(
    private readonly http: HttpClient,
    private readonly apiConfig: ApiConfiguration
  ) {}

  getCategories(query: FindCategoriesQuery): Observable<PageCategoryResponse> {
    return this.http.get<PageCategoryResponse>(`${this.apiConfig.rootUrl}/categories`, {
      params: this.toParams(query),
    });
  }

  getCategory(uuid: string): Observable<CategoryResponse> {
    return this.http.get<CategoryResponse>(`${this.apiConfig.rootUrl}/categories/${uuid}`);
  }

  createCategory(body: CreateCategoryForm, image?: File | null): Observable<CategoryResponse> {
    return this.http.post<CategoryResponse>(
      `${this.apiConfig.rootUrl}/categories`,
      this.toCategoryFormData(body, image)
    );
  }

  updateCategory(uuid: string, body: UpdateCategoryForm, image?: File | null): Observable<CategoryResponse> {
    return this.http.patch<CategoryResponse>(
      `${this.apiConfig.rootUrl}/categories/${uuid}`,
      this.toCategoryFormData(body, image)
    );
  }

  deleteCategory(uuid: string): Observable<void> {
    return this.http.delete<void>(`${this.apiConfig.rootUrl}/categories/${uuid}`);
  }

  private toCategoryFormData(
    body: CreateCategoryForm | UpdateCategoryForm,
    image?: File | null
  ): FormData {
    const formData = new FormData();
    formData.append('name', body.name);
    formData.append('slug', body.slug);
    formData.append('description', body.description);
    if (body.parentUuid) {
      formData.append('parentUuid', body.parentUuid);
    }
    if (body.languageUuid) {
      formData.append('languageUuid', body.languageUuid);
    }
    if (image) {
      formData.append('image', image, image.name);
    }
    return formData;
  }

  private toParams(query: FindCategoriesQuery): HttpParams {
    let params = new HttpParams();
    const entries: [keyof FindCategoriesQuery, string | number | undefined][] = [
      ['page', query.page],
      ['size', query.size],
      ['sortBy', query.sortBy],
      ['sortDirection', query.sortDirection],
      ['name', query.name],
      ['description', query.description],
    ];
    for (const [key, value] of entries) {
      if (value !== undefined && value !== null && value !== '') {
        params = params.set(String(key), String(value));
      }
    }
    return params;
  }
}
