import { history, Outlet, useAccess, useModel } from '@umijs/max';
import { Button, Result } from 'antd';
import type React from 'react';
import { getPermissions } from '@/utils/auth';
import {
  hasMenuPermission,
  normalizePath,
  ROUTE_PERMISSIONS,
} from '@/utils/routePermission';

/**
 * 路由级权限守卫（配置在 config/routes.ts 的 wrappers 中）
 *
 * 判定顺序：
 * 1. 未配置权限码的路由：直接放行；
 * 2. 精确权限码命中，或持有 `*:*:*` 通配：放行；
 * 3. 后端菜单树包含该路由：放行（菜单即授权）；
 * 4. 菜单树不可用（首屏加载中 / 拉取失败 / 无菜单数据）：放行，避免把用户锁在 403；
 * 5. 其余情况（有菜单数据但没有该路由、也没有对应权限码）：403。
 *
 * 说明：前端守卫只影响体验，不构成安全边界 —— localStorage 中的身份/权限不可信，
 * 越权数据一律由后端接口拒绝。
 */
export default function AuthGuard({
  children,
}: {
  children?: React.ReactNode;
}) {
  const access = useAccess();
  const { initialState } = useModel('@@initialState');
  const { pathname } = history.location;

  /**
   * 被守护页面的实际内容。
   *
   * umi 4 的 wrappers 并不是普通的高阶组件：它把路由包装成
   * `{ isWrapper: true, component: AuthGuard }` 的父路由，真正的业务页面注册为
   * 子路由（path: ''）。默认配置下（reactRouter5Compat 为 false），
   * renderer-react 渲染 wrapper 时只做 `<Component />`，不会把子路由内容作为
   * children 传进来 —— 也就是说这里 `children` 恒为 undefined。因此必须显式渲染
   * `<Outlet />`，否则所有被 wrappers 包裹的页面（各类列表页、详情页）都会
   * 渲染成空白，界面上只剩布局外壳。
   */
  const content = children ?? <Outlet />;

  const required = ROUTE_PERMISSIONS[normalizePath(pathname)];
  if (!required) {
    return content;
  }

  // 1) 精确权限码 / `*:*:*` 通配（见 src/access.ts#canPerm）
  if (access?.canPerm?.(required)) {
    return content;
  }

  // 2) 后端菜单树授权
  const menuData = initialState?.menuData;
  if (hasMenuPermission(menuData, pathname)) {
    return content;
  }

  // 3) 缺少权威数据时放行：菜单为空说明授权结果还没拿到，不能据此判定越权
  if (!menuData || menuData.length === 0) {
    return content;
  }

  // 4) 权限码与菜单都没有加载出来时同样放行，避免首屏误拦截
  if (getPermissions().length === 0) {
    return content;
  }

  return (
    <Result
      status="403"
      title="403"
      subTitle="抱歉，您没有访问该页面的权限。"
      extra={
        <Button type="primary" onClick={() => history.push('/')}>
          返回首页
        </Button>
      }
    />
  );
}
