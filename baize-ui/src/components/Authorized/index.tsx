import React from 'react';
import usePermission from '@/hooks/usePermission';

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
 */
const Authorized: React.FC<AuthorizedProps> = ({
  permission,
  requireAll = false,
  noMatch = null,
  children,
}) => {
  const { hasPermission, hasAllPermissions, loading } = usePermission();

  if (loading) {
    return null;
  }

  // 未指定权限，直接放行
  if (!permission) {
    return <>{children}</>;
  }

  const permissionList = Array.isArray(permission) ? permission : [permission];

  // 检查权限
  let hasAccess;
  if (requireAll) {
    hasAccess = hasAllPermissions(permissionList);
  } else {
    hasAccess = hasPermission(permission);
  }

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
