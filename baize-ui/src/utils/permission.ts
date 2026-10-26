/**
 * 权限码匹配工具（前端展示层统一入口）
 *
 * 与后端约定保持一致：
 * - JWT 的 `role` claim 以及菜单/按钮下发的权限码都可能是**逗号分隔的权限码串**，
 *   例如 `system:user:list,system:role:list`，因此必须先 split 再比较；
 * - 权限匹配是**精确匹配**，不做前缀 / 通配的分段展开；
 * - 超级管理员使用 `*:*:*` 通配，拥有该码即拥有全部权限；
 * - 所有判断仅用于按钮/菜单显隐与路由拦截，真正的鉴权由后端接口负责，
 *   前端不把 localStorage 中的身份/权限当作安全边界。
 */

/** 超级管理员通配权限码 */
export const ALL_PERMISSION = '*:*:*';

/**
 * 归一化权限码：
 * - 支持单个字符串、逗号分隔字符串、字符串数组，以及它们的嵌套；
 * - 逐项 trim、丢弃空值与重复项；
 * - 逗号是唯一分隔符（权限码本身不含空白，但容忍 `a, b` 写法）。
 */
export function normalizePermissionCodes(input: unknown): string[] {
  const codes: string[] = [];
  const push = (raw: unknown) => {
    if (typeof raw !== 'string') {
      return;
    }
    raw.split(',').forEach((part) => {
      const code = part.trim();
      if (code && !codes.includes(code)) {
        codes.push(code);
      }
    });
  };

  if (Array.isArray(input)) {
    input.forEach((item) => {
      if (Array.isArray(item)) {
        normalizePermissionCodes(item).forEach((code) => {
          if (!codes.includes(code)) {
            codes.push(code);
          }
        });
        return;
      }
      push(item);
    });
    return codes;
  }

  push(input);
  return codes;
}

/** 是否为超级管理员（持有 `*:*:*`） */
export function isSuperAdmin(granted: readonly string[]): boolean {
  return granted.includes(ALL_PERMISSION);
}

/**
 * 单个权限码校验。
 * - code 为空：视为不做限制（true）；
 * - 持有 `*:*:*`：直接放行；
 * - 其余按**精确匹配**。
 */
export function hasPermissionCode(
  granted: readonly string[],
  code?: string | null,
): boolean {
  if (typeof code !== 'string') {
    return true;
  }
  const required = code.trim();
  if (!required) {
    return true;
  }
  if (isSuperAdmin(granted)) {
    return true;
  }
  return granted.includes(required);
}

/**
 * 多权限码校验。
 * @param requireAll true 表示需要全部命中，false 表示命中任意一个即可
 */
export function hasAnyPermission(
  granted: readonly string[],
  codes?: string | string[] | null,
  requireAll = false,
): boolean {
  if (codes === undefined || codes === null) {
    return true;
  }
  const list = normalizePermissionCodes(codes);
  if (list.length === 0) {
    return true;
  }
  return requireAll
    ? list.every((code) => hasPermissionCode(granted, code))
    : list.some((code) => hasPermissionCode(granted, code));
}
