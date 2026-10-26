import { useCallback, useMemo } from 'react';
import { getPermissions } from '@/utils/auth';
import {
  hasAnyPermission,
  hasPermissionCode,
  isSuperAdmin,
} from '@/utils/permission';

/**
 * 权限 Hook（仅用于展示层）
 *
 * 权限数据源统一为 @/utils/auth（后端菜单接口 + JWT `role` claim），
 * 匹配规则与 @/utils/permission 一致：逗号拆分 + 精确匹配 + `*:*:*` 通配。
 * 页面按钮显隐推荐直接使用 <Authorized /> / useAccess()（见 src/access.ts 的 canPerm）。
 */
export function usePermission() {
  // 每次渲染都从统一数据源读取，避免弹窗/登录后状态不同步
  const permissions = useMemo(() => getPermissions(), []);

  const has = useCallback(
    (permission?: string | null) => hasPermissionCode(permissions, permission),
    [permissions],
  );

  /**
   * 检查是否有指定权限（任一命中即可）
   */
  const hasPermission = useCallback(
    (permission: string | string[]) =>
      hasAnyPermission(permissions, permission, false),
    [permissions],
  );

  /**
   * 检查是否有所有指定权限
   */
  const hasAllPermissions = useCallback(
    (permissionList: string[]) =>
      hasAnyPermission(permissions, permissionList, true),
    [permissions],
  );

  return {
    permissions,
    isSuperAdmin: isSuperAdmin(permissions),
    has,
    hasPermission,
    hasAllPermissions,
    /** 权限数据来自同步缓存，不存在异步加载态（保留字段兼容旧用法） */
    loading: false,
  };
}

export default usePermission;
