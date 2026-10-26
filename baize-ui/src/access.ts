/**
 * @see https://umijs.org/docs/max/access#access
 *
 * 权限模型说明（与后端契约对齐）：
 * - JWT 的 `role` claim 与菜单接口下发的 `perms` 都可能是逗号分隔的权限码串，
 *   统一由 @/utils/permission 拆分后做**精确匹配**；
 * - 超级管理员使用通配码 `*:*:*`，持有该码即放行全部权限，不再依据 username === 'admin'；
 * - permissions 只是展示缓存（按钮/菜单显隐），真正的鉴权由后端接口负责；
 * - currentUser 用于展示（头像/用户名/水印），不是可信身份来源。
 */
import { getPermissions } from '@/utils/auth';
import { hasPermissionCode, isSuperAdmin } from '@/utils/permission';

export default function access(
  initialState: { currentUser?: API.CurrentUser } | undefined,
) {
  // 超级管理员判定：以后端下发的 `*:*:*` 权限码为准
  const canAdmin = isSuperAdmin(getPermissions());

  return {
    canAdmin,
    /**
     * 按钮权限判断（精确匹配 + `*:*:*` 通配）
     * @param code 后端下发的权限标识，如 system:user:add
     * @returns 有权限返回 true；code 为空视为不限制（true）
     */
    canPerm: (code?: string): boolean => {
      if (!code) {
        return true;
      }
      return hasPermissionCode(getPermissions(), code);
    },
    /** 当前登录用户（展示用） */
    currentUser: initialState?.currentUser,
  };
}
