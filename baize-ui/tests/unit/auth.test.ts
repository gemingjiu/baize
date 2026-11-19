import { beforeEach, describe, expect, it } from 'vitest';
import {
  clearAuth,
  getPermissions,
  isAuthenticated,
  isTokenExpired,
  parseJwtPayload,
  saveAuth,
  savePermissions,
} from '@/utils/auth';

/** 构造一个仅含 payload 的伪 JWT（本地解析不验签，这里只关心 payload）。 */
function fakeJwt(payload: Record<string, unknown>): string {
  // btoa 仅接受 latin1，payload 含中文时先编码为 UTF-8 字节再逐字节转 latin1
  const encode = (obj: unknown) => {
    const bytes = new TextEncoder().encode(JSON.stringify(obj));
    const binary = Array.from(bytes, (b) => String.fromCharCode(b)).join('');
    return btoa(binary)
      .replace(/\+/g, '-')
      .replace(/\//g, '_')
      .replace(/=+$/, '');
  };
  return `${encode({ alg: 'none' })}.${encode(payload)}.signature`;
}

beforeEach(() => {
  localStorage.clear();
});

describe('parseJwtPayload / isTokenExpired', () => {
  it('解析 payload 并正确处理中文', () => {
    const token = fakeJwt({ sub: '管理员', exp: 9999999999 });
    expect(parseJwtPayload(token)).toMatchObject({
      sub: '管理员',
      exp: 9999999999,
    });
  });

  it('缺失或非法 token 返回 undefined / 视为过期', () => {
    expect(parseJwtPayload(null)).toBeUndefined();
    expect(parseJwtPayload('not-a-jwt')).toBeUndefined();
    expect(isTokenExpired(undefined)).toBe(true);
    expect(isTokenExpired(fakeJwt({ noExp: true }))).toBe(true);
  });

  it('依据 exp 判断过期', () => {
    const future = fakeJwt({ exp: Math.floor(Date.now() / 1000) + 3600 });
    const past = fakeJwt({ exp: Math.floor(Date.now() / 1000) - 3600 });
    expect(isTokenExpired(future)).toBe(false);
    expect(isTokenExpired(past)).toBe(true);
  });
});

describe('auth 本地缓存读写', () => {
  it('saveAuth 落地 access/refresh/user，clearAuth 全清', () => {
    const token = fakeJwt({ exp: Math.floor(Date.now() / 1000) + 3600 });
    saveAuth({
      accessToken: token,
      refreshToken: 'r-token',
      user: { id: 'u1', username: 'tester' } as unknown as API.CurrentUser,
    } as API.LoginVO);
    expect(isAuthenticated()).toBe(true);
    clearAuth();
    expect(isAuthenticated()).toBe(false);
    expect(localStorage.getItem('accessToken')).toBeNull();
    expect(localStorage.getItem('refreshToken')).toBeNull();
    expect(localStorage.getItem('userInfo')).toBeNull();
  });

  it('permissions 与 token 中的权限码合并、去重', () => {
    savePermissions(['system:user:list', 'system:role:list']);
    localStorage.setItem(
      'accessToken',
      fakeJwt({ permissions: 'system:user:list,system:user:add' }),
    );
    expect(getPermissions()).toEqual([
      'system:user:list',
      'system:role:list',
      'system:user:add',
    ]);
  });

  it('无缓存时 getPermissions 返回空数组', () => {
    expect(getPermissions()).toEqual([]);
  });
});
