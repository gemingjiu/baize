/**
 * 菜单工具类
 */
import type { MenuDataItem } from '@umijs/route-utils';

const iconMap: Record<string, React.ReactNode> = {
  setting: 'setting',
  user: 'user',
  team: 'team',
  bars: 'menu',
  apartment: 'apartment',
  shop: 'shop',
  'file-text': 'file-text',
  history: 'history',
  dashboard: 'dashboard',
  cluster: 'cluster',
  idcard: 'idcard',
  safety: 'safety',
  experiment: 'experiment',
  table: 'table',
  form: 'form',
  profile: 'profile',
  warning: 'warning',
};

export function transformMenu(backendMenus: API.SysMenuDto[]): MenuDataItem[] {
  return backendMenus
    .filter((menu) => menu.menuType !== '2')
    .map((menu) => {
      const result: MenuDataItem = {
        path: menu.path || `/${menu.id}`,
        name: menu.menuName,
        key: menu.id,
        icon: iconMap[menu.icon || ''] || menu.icon,
        hideInMenu: menu.visible === '1',
        children: menu.children ? transformMenu(menu.children) : undefined,
      };

      if (menu.component && menu.component !== 'Layout') {
        result.component = menu.component;
      }

      return result;
    });
}

export function getMenuPermissions(menus: API.SysMenuDto[]): string[] {
  const permissions: string[] = [];

  const traverse = (menuList: API.SysMenuDto[]) => {
    menuList.forEach((menu) => {
      if (menu.perms) {
        permissions.push(menu.perms);
      }
      if (menu.children && menu.children.length > 0) {
        traverse(menu.children);
      }
    });
  };

  traverse(menus);
  return permissions;
}

export function getAllButtonPermissions(menus: API.SysMenuDto[]): string[] {
  const buttons: string[] = [];

  const traverse = (menuList: API.SysMenuDto[]) => {
    menuList.forEach((menu) => {
      if (menu.menuType === '2' && menu.perms) {
        buttons.push(menu.perms);
      }
      if (menu.children && menu.children.length > 0) {
        traverse(menu.children);
      }
    });
  };

  traverse(menus);
  return buttons;
}
