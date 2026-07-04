import { Injectable } from '@angular/core';
import { MenuItem } from '../../layouts/sidebar/menu.model';
import { MENU } from '../../layouts/sidebar/menu';
import { PermissionService } from './permission.service';
import { SessionService } from './session.service';

export interface MenuFilterContext {
  permissions: string[];
  isPlatformUser: boolean;
}

@Injectable({ providedIn: 'root' })
export class MenuService {

  constructor(
    private readonly session: SessionService,
    private readonly permissionService: PermissionService
  ) {}

  getVisibleMenu(items: MenuItem[] = MENU): MenuItem[] {
    return this.filterMenuItems(items, {
      permissions: this.session.getPermissions(),
      isPlatformUser: this.session.isPlatformUser(),
    });
  }

  getVisibleMenuFromContext(context: MenuFilterContext, items: MenuItem[] = MENU): MenuItem[] {
    return this.filterMenuItems(items, context);
  }

  filterMenuItems(items: MenuItem[], context: MenuFilterContext): MenuItem[] {
    const filtered = items
      .filter(item => this.isMenuItemVisible(item, context))
      .map(item => ({
        ...item,
        subItems: item.subItems?.length
          ? this.filterMenuItems(item.subItems, context)
          : undefined,
      }));

    return filtered.filter((item, index, list) => {
      if (!item.isTitle) {
        return true;
      }
      const next = list[index + 1];
      return next !== undefined && !next.isTitle;
    });
  }

  private isMenuItemVisible(item: MenuItem, context: MenuFilterContext): boolean {
    if (item.scope === 'tenant' && context.isPlatformUser) {
      return false;
    }
    if (item.scope === 'platform' && !context.isPlatformUser) {
      return false;
    }
    if (!item.permissions?.length) {
      return true;
    }
    const mode = item.permissionMode ?? 'any';
    return mode === 'all'
      ? this.permissionService.canAll(item.permissions)
      : this.permissionService.canAny(item.permissions);
  }
}
