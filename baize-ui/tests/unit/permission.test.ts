import { describe, expect, it } from 'vitest';
import {
  ALL_PERMISSION,
  hasAnyPermission,
  hasPermissionCode,
  isSuperAdmin,
  normalizePermissionCodes,
} from '@/utils/permission';

describe('normalizePermissionCodes', () => {
  it('拆分逗号串、trim 并去重', () => {
    expect(
      normalizePermissionCodes(
        'system:user:list, system:role:list,system:user:list',
      ),
    ).toEqual(['system:user:list', 'system:role:list']);
  });

  it('支持字符串数组与嵌套数组', () => {
    expect(
      normalizePermissionCodes(['a,b', ['c', 'd,a'], '', null, 42]),
    ).toEqual(['a', 'b', 'c', 'd']);
  });

  it('非字符串输入返回空数组', () => {
    expect(normalizePermissionCodes(undefined)).toEqual([]);
    expect(normalizePermissionCodes(123)).toEqual([]);
  });
});

describe('isSuperAdmin / hasPermissionCode', () => {
  it('持有通配符即超管，且放行任意权限码', () => {
    const granted = [ALL_PERMISSION];
    expect(isSuperAdmin(granted)).toBe(true);
    expect(hasPermissionCode(granted, 'system:user:add')).toBe(true);
  });

  it('精确匹配：仅命中完全相同的权限码', () => {
    const granted = ['system:user:list', 'system:role:list'];
    expect(hasPermissionCode(granted, 'system:user:list')).toBe(true);
    expect(hasPermissionCode(granted, 'system:user')).toBe(false);
    expect(hasPermissionCode(granted, 'system:user:add')).toBe(false);
  });

  it('空权限码视为不限制', () => {
    expect(hasPermissionCode([], '')).toBe(true);
    expect(hasPermissionCode([], undefined)).toBe(true);
    expect(hasPermissionCode([], null)).toBe(true);
  });
});

describe('hasAnyPermission', () => {
  const granted = ['system:user:list', 'system:role:list'];

  it('默认任一命中即可', () => {
    expect(hasAnyPermission(granted, 'system:user:list,system:x:y')).toBe(true);
    expect(hasAnyPermission(granted, 'system:x:y')).toBe(false);
  });

  it('requireAll=true 需全部命中', () => {
    expect(
      hasAnyPermission(granted, ['system:user:list', 'system:role:list'], true),
    ).toBe(true);
    expect(
      hasAnyPermission(granted, ['system:user:list', 'system:x:y'], true),
    ).toBe(false);
  });

  it('空权限列表视为不限制', () => {
    expect(hasAnyPermission(granted, [])).toBe(true);
    expect(hasAnyPermission(granted, null)).toBe(true);
  });
});
