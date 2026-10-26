/**
 * 菜单工具类
 *
 * 权限码约定（与后端一致）：
 * - 单个 `perms` 字段可能承载**逗号分隔的多个权限码**（如 `system:user:list,system:user:add`），
 *   收集时必须拆分后再入库，匹配时做精确匹配；
 * - 这里收集到的权限码只用于前端按钮/菜单显隐与路由拦截，鉴权由后端接口负责。
 */
import {
  ApartmentOutlined,
  ApiOutlined,
  BellOutlined,
  BookOutlined,
  CloudServerOutlined,
  CodeOutlined,
  DashboardOutlined,
  DatabaseOutlined,
  EditOutlined,
  FieldTimeOutlined,
  FileTextOutlined,
  FormOutlined,
  GlobalOutlined,
  IdcardOutlined,
  LockOutlined,
  LoginOutlined,
  MenuOutlined,
  SafetyCertificateOutlined,
  SettingOutlined,
  TableOutlined,
  TeamOutlined,
  ToolOutlined,
  UserOutlined,
} from '@ant-design/icons';
import type { MenuDataItem } from '@umijs/route-utils';
import type React from 'react';
import { normalizePermissionCodes } from './permission';

/**
 * 后端 `sys_menu.icon`（字符串）-> 图标节点。
 *
 * 为什么必须在前端做这层映射：ProLayout 的 `MenuDataItem.icon` 是 ReactNode，
 * 直接塞字符串会把 "user"、"tree-table" 这类图标名当**文字**渲染到菜单项里，
 * 界面上就是「user 用户管理」——中文菜单名旁边多出一串英文。
 * 后端沿用若依的图标命名（peoples / tree-table / logininfor ...），
 * 与 antd 图标组件名并非一一对应，所以这里显式列一张表。
 */
const iconNodeMap: Record<string, React.ReactNode> = {
  // 后端 sys_menu 现有的图标名
  system: <SettingOutlined />,
  monitor: <DashboardOutlined />,
  tool: <ToolOutlined />,
  user: <UserOutlined />,
  peoples: <TeamOutlined />,
  'tree-table': <ApartmentOutlined />,
  tree: <ApartmentOutlined />,
  international: <GlobalOutlined />,
  dict: <BookOutlined />,
  lock: <LockOutlined />,
  post: <IdcardOutlined />,
  edit: <EditOutlined />,
  message: <BellOutlined />,
  job: <FieldTimeOutlined />,
  form: <FileTextOutlined />,
  logininfor: <LoginOutlined />,
  online: <UserOutlined />,
  server: <CloudServerOutlined />,
  redis: <DatabaseOutlined />,
  build: <FormOutlined />,
  code: <CodeOutlined />,
  swagger: <ApiOutlined />,
  list: <TableOutlined />,
  // routes.ts 里出现过、后端也可能下发的通用图标名
  setting: <SettingOutlined />,
  team: <TeamOutlined />,
  menu: <MenuOutlined />,
  bars: <MenuOutlined />,
  apartment: <ApartmentOutlined />,
  cluster: <ApartmentOutlined />,
  'file-text': <FileTextOutlined />,
  history: <FieldTimeOutlined />,
  dashboard: <DashboardOutlined />,
  idcard: <IdcardOutlined />,
  safety: <SafetyCertificateOutlined />,
  schedule: <FieldTimeOutlined />,
  notification: <BellOutlined />,
  login: <LoginOutlined />,
  'cloud-server': <CloudServerOutlined />,
  database: <DatabaseOutlined />,
  api: <ApiOutlined />,
  book: <BookOutlined />,
};

/**
 * 归一化图标名，屏蔽大小写、驼峰、以及 `Outlined`/`Filled` 风格后缀的差异。
 * `TreeTable` / `treeTable` / `tree-table` 都会收敛成 `tree-table`。
 */
export function normalizeIconName(icon: string): string {
  return icon
    .trim()
    .replace(/[A-Z]/g, (char) => `-${char.toLowerCase()}`)
    .replace(/-?(outlined|filled|twotone)$/i, '')
    .replace(/^-+/, '')
    .toLowerCase();
}

/**
 * 后端菜单 path -> 前端路由 path。
 *
 * 后端 `sys_menu.path` 存的是**相对路径**，实际见过两种形态：
 *   a) 子级是裸名字：`user`、`dict`（顶层为 `/system`）；
 *   b) 子级已带前缀：`system/user/index`、`system/dict/index`（顶层为 `system`）。
 * 而前端 umi 路由是**绝对路径**（如 `/system/user`）。
 * 若不转换，ProLayout 渲染出的菜单项 path 就是裸的 `user`，
 * 点击后跳 `/user` 匹配不到路由，直接进 404 —— 表现为"登录后没有列表功能"。
 *
 * 规则：
 * 1. 已是绝对路径（`/` 开头）：原样使用；
 * 2. 相对路径：先去掉与父路径重复的前缀段，再拼接父路径，收敛到父路径自己的顶层前缀下
 *    （`/system` 子树收敛到 `/system/**`，`/monitor` 子树收敛到 `/monitor/**`）；
 * 3. 去掉末尾的 `/index`（后端菜单常带 index 后缀，前端路由不需要）；
 * 4. 按钮节点（menuType=2）path 为空，由调用方过滤，不会走到这里。
 */
function toRoutePath(menuPath: string | undefined | null, parentPath: string): string {
  const raw = (menuPath || '').trim();
  if (!raw) {
    return parentPath || '/';
  }
  // 绝对路径：仅做 index 后缀清理
  if (raw.startsWith('/')) {
    return raw.replace(/\/index$/, '') || '/';
  }

  // 相对路径：剥离子路径中与父路径重复的前缀段，避免 /system/system/user
  let rest = raw.replace(/^\/+/, '');
  const parentSegments = (parentPath || '').split('/').filter(Boolean);
  // 从最长前缀开始尝试剥离（例如父路径 /system，子路径 system/user/index -> user/index）
  for (let take = Math.min(parentSegments.length, rest.split('/').length); take > 0; take--) {
    const head = parentSegments.slice(0, take).join('/');
    if (rest === head || rest.startsWith(`${head}/`)) {
      rest = rest.slice(head.length).replace(/^\/+/, '');
      break;
    }
  }

  const joined = rest ? `${(parentPath || '').replace(/\/$/, '')}/${rest}` : parentPath || '/';
  const normalized = joined.replace(/\/index$/, '') || '/';
  // 兜底前缀必须跟随**父路径的首段**，不能硬编码 /system：
  // 否则 /monitor/operlog、/tool/gen 会被错改成 /system/monitor/operlog、/system/tool/gen，
  // 而前端真实路由是 /system/operlog（monitor 子树）或根本不存在（tool 子树），点击即 404。
  const parentPrefix = `/${(parentPath || '').split('/').filter(Boolean)[0] || 'system'}`;
  return normalized.startsWith(parentPrefix) ? normalized : `${parentPrefix}${normalized}`;
}

export function transformMenu(
  backendMenus: API.SysMenuDto[],
  parentPath = '/system',
): MenuDataItem[] {
  return backendMenus
    .filter((menu) => menu.menuType !== '2')
    .map((menu) => {
      const routePath = toRoutePath(menu.path, parentPath);
      const result: MenuDataItem = {
        path: routePath,
        name: menu.menuName,
        key: menu.id,
        icon: menu.icon ? normalizeIconName(menu.icon) : undefined,
        hideInMenu: menu.visible === '1',
        children: menu.children ? transformMenu(menu.children, routePath) : undefined,
      };

      if (menu.component && menu.component !== 'Layout') {
        result.component = menu.component;
      }

      return result;
    });
}

/**
 * 渲染前把菜单图标名换成图标节点。
 *
 * transformMenu 产出的是**字符串**图标名（可直接 JSON.stringify 进 localStorage，
 * 见 src/utils/auth.ts 的 saveMenuTree）；真正渲染时（src/app.tsx 的 menu.request）
 * 再用这里转换，避免把 React 元素写进缓存。
 * 未在映射表里的图标名一律丢弃 —— 宁可没有图标，也不能把英文图标名当文字显示出来。
 */
export function renderMenuTree(menuData: MenuDataItem[]): MenuDataItem[] {
  return menuData.map((item) => {
    const icon = typeof item.icon === 'string' ? iconNodeMap[item.icon] : item.icon;
    return {
      ...item,
      icon,
      children: item.children?.length ? renderMenuTree(item.children) : item.children,
    };
  });
}

export function getMenuPermissions(menus: API.SysMenuDto[]): string[] {
  const permissions: string[] = [];

  const traverse = (menuList: API.SysMenuDto[]) => {
    menuList.forEach((menu) => {
      if (menu.perms) {
        permissions.push(...normalizePermissionCodes(menu.perms));
      }
      if (menu.children && menu.children.length > 0) {
        traverse(menu.children);
      }
    });
  };

  traverse(menus);
  return normalizePermissionCodes(permissions);
}

/**
 * 收集菜单树上**所有**节点的权限码（目录 / 菜单 / 按钮）。
 * 路由守卫需要 `system:user:list` 这类列表权限，因此不能只收集按钮节点。
 */
export function getAllPermissions(menus: API.SysMenuDto[]): string[] {
  return getMenuPermissions(menus);
}

/**
 * 仅收集按钮类节点（menuType === '2'）的权限码。
 * 逗号分隔的 perms 会被拆开，保证与后端「精确匹配」一致。
 */
export function getAllButtonPermissions(menus: API.SysMenuDto[]): string[] {
  const buttons: string[] = [];

  const traverse = (menuList: API.SysMenuDto[]) => {
    menuList.forEach((menu) => {
      if (menu.menuType === '2' && menu.perms) {
        buttons.push(...normalizePermissionCodes(menu.perms));
      }
      if (menu.children && menu.children.length > 0) {
        traverse(menu.children);
      }
    });
  };

  traverse(menus);
  return normalizePermissionCodes(buttons);
}
