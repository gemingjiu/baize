/**
 * 路由 → 权限码映射（前端路由守卫用）
 *
 * 约定：
 * - 右侧取后端菜单/按钮下发的权限码，采用**精确匹配**，`*:*:*` 表示超级管理员；
 * - 前端守卫只做体验层拦截（避免用户进入空白/报错页），真正的鉴权由后端接口负责；
 * - 菜单树是后端下发的授权结果，命中菜单同样视为有权限（见 hasMenuPermission）。
 */
export const ROUTE_PERMISSIONS: Record<string, string> = {
  '/system/user': 'system:user:list',
  '/system/role': 'system:role:list',
  '/system/menu': 'system:menu:list',
  '/system/dept': 'system:dept:list',
  '/system/tenant': 'system:tenant:list',
  '/system/post': 'system:post:list',
  '/system/dict': 'system:dict:list',
  '/system/config': 'system:config:list',
  '/system/perm': 'system:perm:list',
  '/system/notice': 'system:notice:list',
  // 注意：/system/operlog、/system/loginlog、/system/monitor 已改为
  // redirect 到 /monitor/** 子树（见 config/routes.ts），重定向本身不渲染页面、
  // 不经过 authGuard，因此这里不再需要它们的映射。
  '/monitor/operlog': 'monitor:operlog:list',
  '/monitor/logininfor': 'monitor:logininfor:list',
  '/monitor/online': 'monitor:online:list',
  '/monitor/server': 'monitor:server:list',
  '/monitor/cache': 'monitor:cache:list',
  // /tool/build（表单构建器，模板残留）已移除——2026-11-05 Frontend CI 根因修复：
  // 该页面被 .gitignore 裸 build 规则遮蔽从未入库，CI 冷环境 max setup 解析
  // 该组件路径失败致 postinstall 崩溃（与 2026-10-28「表单构建器不做」决策对齐）。
  '/tool/gen': 'tool:gen:list',
  '/tool/swagger': 'tool:swagger:list',
};

/** 归一化路径：去掉 query/hash、重复斜杠、末尾斜杠，统一小写 */
export function normalizePath(path?: string | null): string {
  if (!path) {
    return '';
  }
  const clean = path.split('?')[0].split('#')[0].trim();
  if (!clean) {
    return '';
  }
  const withLeading = clean.startsWith('/') ? clean : `/${clean}`;
  const trimmed = withLeading.replace(/\/+$/, '');
  return (trimmed || '/').toLowerCase();
}

/** 去掉末尾 /index（后端菜单路径常带 index 后缀） */
const stripIndex = (path: string): string => path.replace(/\/index$/, '');

/**
 * 菜单路径与当前路由是否匹配。
 * 后端菜单 path 可能是 `/system/user`、`user`、`/system/user/index` 等形态，
 * 因此做归一化 + 去 index + 父子前缀的宽松匹配，降低误拦截概率。
 */
export function matchMenuPath(menuPath: string, target: string): boolean {
  const menu = normalizePath(menuPath);
  const route = normalizePath(target);
  if (!menu || !route) {
    return false;
  }
  if (menu === route) {
    return true;
  }
  const menuStripped = stripIndex(menu);
  const routeStripped = stripIndex(route);
  if (menuStripped === routeStripped) {
    return true;
  }
  return (
    menuStripped.startsWith(`${routeStripped}/`) ||
    routeStripped.startsWith(`${menuStripped}/`)
  );
}

type MenuNode = {
  path?: string;
  children?: MenuNode[];
};

/** 菜单树中是否包含目标路由 */
export function hasMenuPermission(
  menuData: MenuNode[] | undefined,
  pathname: string,
): boolean {
  if (!menuData || menuData.length === 0) {
    return false;
  }
  return menuData.some((item) => {
    if (item.path && matchMenuPath(item.path, pathname)) {
      return true;
    }
    return item.children ? hasMenuPermission(item.children, pathname) : false;
  });
}
