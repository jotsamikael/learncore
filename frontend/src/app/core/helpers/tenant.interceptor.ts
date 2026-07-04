import { Injectable } from '@angular/core';
import {
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
} from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { SessionService } from '../services/session.service';

@Injectable()
export class TenantInterceptor implements HttpInterceptor {

  constructor(private readonly session: SessionService) {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    if (environment.defaultauth !== 'learncore' || !this.shouldAttachTenantHeader(request.url)) {
      return next.handle(request);
    }

    const tenantSlug = this.session.getTenantSlug();
    if (!tenantSlug || this.session.isPlatformUser()) {
      return next.handle(request);
    }

    return next.handle(request.clone({
      setHeaders: { 'X-Tenant-Slug': tenantSlug },
    }));
  }

  private shouldAttachTenantHeader(url: string): boolean {
    if (!url.includes(environment.apiUrl)) {
      return false;
    }
    return !url.includes('/auth/refresh') && !url.includes('/auth/logout');
  }
}
