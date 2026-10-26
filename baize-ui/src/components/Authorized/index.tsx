import { useAccess } from '@umijs/max';
import React from 'react';
import { normalizePermissionCodes } from '@/utils/permission';

interface AuthorizedProps {
  /**
   * 权限标识，支持单个或多个
   */
  permission?: string | string[];
  /**
   * 是否需要所有权限（多个权限时）
   */
  requireAll?: boolean;
  /**
   * 无权限时显示的内容
   */
  noMatch?: React.ReactNode;
  /**
   * 子组件
   */
  children: React.ReactNode;
}

/**
 * 权限控制组件
 * 根据用户权限决定是否渲染子组件
 *
 * 数据源统一为 umi access 插件（src/access.ts 的 canPerm），
 * 组件对外 API（permission / requireAll / noMatch）保持不变。
 */
const Authorized: React.FC<AuthorizedProps> = ({
  permission,
  requireAll = false,
  noMatch = null,
  children,
}) => {
  const access = useAccess();

  /**
   * 权限判定入口。
   *
   * `useAccess()` 读的是 umi AccessContext，默认值是 null；只要 <AccessProvider>
   * 没有包住当前子树，这里拿到的就是 null，直接调用 `access.canPerm(code)` 会抛
   * "Cannot read properties of null"。与 wrappers/authGuard.tsx 保持一致：
   * 权限数据不可用时一律放行，前端显隐只影响体验，真正的鉴权由后端接口负责。
   */
  const canPerm = (code?: string): boolean => {
    const checker = access?.canPerm;
    if (typeof checker !== 'function') {
      return true;
    }
    return checker(code);
  };

  // 未指定权限，直接放行
  if (!permission) {
    return <>{children}</>;
  }

  // 兼容逗号分隔的权限码串（与后端 perms 下发格式一致）
  const permissionList = normalizePermissionCodes(permission);
  if (permissionList.length === 0) {
    return <>{children}</>;
  }

  const hasAccess = requireAll
    ? permissionList.every((code) => canPerm(code))
    : permissionList.some((code) => canPerm(code));

  if (hasAccess) {
    return <>{children}</>;
  }

  return <>{noMatch}</>;
};

/**
 * 权限控制的高阶组件
 */
export function withPermission<P extends object>(
  Component: React.ComponentType<P>,
  permission: string | string[],
  requireAll = false,
) {
  return function WithPermission(props: P) {
    return (
      <Authorized permission={permission} requireAll={requireAll}>
        <Component {...props} />
      </Authorized>
    );
  };
}

export default Authorized;
