import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiConfiguration } from '../../learncoreservices/api-configuration';
import { UpdateProfileRequest } from '../../learncoreservices/models/update-profile-request';
import { UserProfileResponse } from '../../learncoreservices/models/user-profile-response';

export interface UpdateProfilePayload extends UpdateProfileRequest {
  email?: string;
  phone?: string;
}

@Injectable({ providedIn: 'root' })
export class ProfileApiService {
  constructor(
    private readonly http: HttpClient,
    private readonly apiConfig: ApiConfiguration
  ) {}

  getMyProfile(): Observable<UserProfileResponse> {
    return this.http.get<UserProfileResponse>(`${this.apiConfig.rootUrl}/users/me`);
  }

  updateMyProfile(body: UpdateProfilePayload, avatar?: File | null): Observable<UserProfileResponse> {
    return this.http.patch<UserProfileResponse>(
      `${this.apiConfig.rootUrl}/users/me`,
      this.toFormData(body, avatar)
    );
  }

  private toFormData(body: UpdateProfilePayload, avatar?: File | null): FormData {
    const formData = new FormData();
    if (body.firstname) {
      formData.append('firstname', body.firstname);
    }
    if (body.lastname) {
      formData.append('lastname', body.lastname);
    }
    if (body.username) {
      formData.append('username', body.username);
    }
    if (body.email) {
      formData.append('email', body.email);
    }
    if (body.phone) {
      formData.append('phone', body.phone);
    }
    if (body.dateOfBirth) {
      formData.append('dateOfBirth', body.dateOfBirth);
    }
    if (avatar) {
      formData.append('avatar', avatar, avatar.name);
    }
    return formData;
  }
}
