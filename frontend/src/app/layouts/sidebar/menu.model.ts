export type MenuScope = 'tenant' | 'platform';

export interface MenuItem {
  id?: number;
  label?: any;
  icon?: string;
  isCollapsed?: any;
  link?: string;
  subItems?: MenuItem[];
  isTitle?: boolean;
  badge?: any;
  parentId?: number;
  isLayout?: boolean;
  permissions?: string[];
  permissionMode?: 'any' | 'all';
  scope?: MenuScope;
}