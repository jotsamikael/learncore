import { Injectable } from '@angular/core';
import { SessionService } from './session.service';

@Injectable({ providedIn: 'root' })
export class PermissionService {

  constructor(private readonly session: SessionService) {}

  can(code: string): boolean {
    return this.getPermissions().includes(code);
  }

  canAny(codes: string[]): boolean {
    if (!codes?.length) {
      return true;
    }
    const permissions = this.getPermissions();
    return codes.some(code => permissions.includes(code));
  }

  canAll(codes: string[]): boolean {
    if (!codes?.length) {
      return true;
    }
    const permissions = this.getPermissions();
    return codes.every(code => permissions.includes(code));
  }

  isPlatformUser(): boolean {
    return this.session.isPlatformUser();
  }

  isTenantUser(): boolean {
    return this.session.isTenantUser();
  }

  hasRole(role: string): boolean {
    return this.session.getSession()?.roles?.includes(role) ?? false;
  }

  private getPermissions(): string[] {
    return this.session.getPermissions();
  }
}
