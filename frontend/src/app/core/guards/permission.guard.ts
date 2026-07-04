import { Injectable } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  CanActivate,
  CanActivateChild,
  Router,
  UrlTree,
} from '@angular/router';
import { MenuScope } from '../../layouts/sidebar/menu.model';
import { PermissionService } from '../services/permission.service';

@Injectable({ providedIn: 'root' })
export class PermissionGuard implements CanActivate, CanActivateChild {

  constructor(
    private readonly permissionService: PermissionService,
    private readonly router: Router
  ) {}

  canActivate(route: ActivatedRouteSnapshot): boolean | UrlTree {
    return this.checkPermission(route);
  }

  canActivateChild(route: ActivatedRouteSnapshot): boolean | UrlTree {
    return this.checkPermission(route);
  }

  private checkPermission(route: ActivatedRouteSnapshot): boolean | UrlTree {
    const permissions = route.data['permissions'] as string[] | undefined;
    const permissionMode = (route.data['permissionMode'] as 'any' | 'all' | undefined) ?? 'any';
    const scope = route.data['scope'] as MenuScope | undefined;

    if (scope === 'tenant' && this.permissionService.isPlatformUser()) {
      return this.router.createUrlTree(['/auth/errors/403']);
    }

    if (scope === 'platform' && !this.permissionService.isPlatformUser()) {
      return this.router.createUrlTree(['/auth/errors/403']);
    }

    if (!permissions?.length) {
      return true;
    }

    const allowed = permissionMode === 'all'
      ? this.permissionService.canAll(permissions)
      : this.permissionService.canAny(permissions);

    return allowed ? true : this.router.createUrlTree(['/auth/errors/403']);
  }
}
