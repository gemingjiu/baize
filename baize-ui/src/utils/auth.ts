/**
 * 认证信息本地存储工具
 *
 * 说明：
 * - 本文件只负责「本地登录态缓存」的读写，以及 JWT payload 的本地解析（不验签）；
 * - 真正的身份与权限以后端接口返回为准（GET /system/user/current、菜单权限接口），
 *   本地 userInfo / permissions 仅用于首屏展示与按钮显隐，不能作为安全边界。
 */
import type { MenuDataItem } from '@ant-design/pro-components';
import { normalizePermissionCodes } from './permission';

/** localStorage 中与认证相关的 key，清理时统一使用 */
const TOKEN_KEY = 'accessToken';
const REFRESH_TOKEN_KEY = 'refreshToken';
const USER_KEY = 'userInfo';
const MENU_KEY = 'menuTree';
const PERMISSION_KEY = 'permissions';

const AUTH_STORAGE_KEYS = [
  TOKEN_KEY,
  REFRESH_TOKEN_KEY,
  USER_KEY,
  MENU_KEY,
  PERMISSION_KEY,
];

/** 把 base64url 编码的 JWT 片段解码为 UTF-8 字符串 */
const decodeBase64Url = (input: string): string => {
  const base64 = input.replace(/-/g, '+').replace(/_/g, '/');
  const padded = base64.padEnd(Math.ceil(base64.length / 4) * 4, '=');
  // atob 返回 latin1 字符串，payload 中可能含中文，需要按 UTF-8 还原
  const binary = atob(padded);
  const bytes = Uint8Array.from(binary, (char) => char.charCodeAt(0));
  return new TextDecoder('utf-8').decode(bytes);
};

/**
 * 解析 JWT payload。
 * 只做 base64 解码，不校验签名；token 缺失或格式非法时返回 undefined。
 */
export const parseJwtPayload = (
  token?: string | null,
): Record<string, any> | undefined => {
  if (!token) {
    return undefined;
  }
  try {
    const payloadPart = token.split('.')[1];
    if (!payloadPart) {
      return undefined;
    }
    const payload = JSON.parse(decodeBase64Url(payloadPart));
    return payload && typeof payload === 'object' ? payload : undefined;
  } catch {
    // token 格式非法（非 JWT / base64 解码失败等），交由调用方按“不可用”处理
    return undefined;
  }
};

/**
 * 本地判断 token 是否过期。
 * 只读取 payload.exp（秒级时间戳），不验签；
 * token 缺失、格式非法或没有数字型 exp 时，一律视为已过期。
 */
export const isTokenExpired = (token?: string | null): boolean => {
  const payload = parseJwtPayload(token);
  const exp = payload?.exp;
  if (typeof exp !== 'number') {
    return true;
  }
  return Date.now() >= exp * 1000;
};

/** 读取 accessToken */
export const getToken = (): string | null => {
  try {
    return localStorage.getItem(TOKEN_KEY);
  } catch {
    return null;
  }
};

/** 读取 refreshToken */
export const getRefreshToken = (): string | null => {
  try {
    return localStorage.getItem(REFRESH_TOKEN_KEY);
  } catch {
    return null;
  }
};

/** 读取本地缓存的用户信息（仅用于展示，不可作为可信身份来源） */
export const getUserInfo = (): API.CurrentUser | undefined => {
  try {
    const raw = localStorage.getItem(USER_KEY);
    if (!raw) {
      return undefined;
    }
    const user = JSON.parse(raw);
    return user && typeof user === 'object' ? user : undefined;
  } catch {
    return undefined;
  }
};

/** 读取本地缓存的菜单树（展示用） */
export const getMenuTree = (): MenuDataItem[] | undefined => {
  try {
    const raw = localStorage.getItem(MENU_KEY);
    if (!raw) {
      return undefined;
    }
    const menuTree = JSON.parse(raw);
    return Array.isArray(menuTree) ? menuTree : undefined;
  } catch {
    return undefined;
  }
};

/**
 * 读取 JWT 中的权限码（`role` claim）。
 *
 * 后端已把 `role` claim 改为**逗号分隔的权限码**（如 `system:user:list,system:role:list`），
 * 超级管理员为 `*:*:*`，权限匹配是精确匹配。这里只做 base64 解码，不验签；
 * 结果仅用于首屏按钮/菜单显隐，真正的鉴权由后端接口负责。
 */
export const getTokenPermissions = (): string[] => {
  const payload = parseJwtPayload(getToken());
  if (!payload) {
    return [];
  }
  return normalizePermissionCodes(payload.role ?? payload.permissions);
};

/**
 * 读取当前可用权限码：后端菜单接口下发的按钮权限码（本地缓存）与 JWT `role` claim 的并集。
 *
 * - 两个来源都由后端签发，本地仅做展示缓存；
 * - 兼容历史缓存：即使是单个逗号分隔字符串也会被拆开（normalizePermissionCodes）；
 * - 不把 localStorage 当作唯一判据 —— 页面鉴权始终以后端接口返回为准。
 */
export const getPermissions = (): string[] => {
  let stored: unknown = [];
  try {
    const raw = localStorage.getItem(PERMISSION_KEY);
    stored = raw ? JSON.parse(raw) : [];
  } catch {
    stored = [];
  }
  return normalizePermissionCodes([stored, getTokenPermissions()]);
};

/** 保存按钮权限码（写入前统一按逗号拆分、去重，保持与后端一致） */
export const savePermissions = (permissions: string[]): void => {
  try {
    localStorage.setItem(
      PERMISSION_KEY,
      JSON.stringify(normalizePermissionCodes(permissions)),
    );
  } catch (error) {
    console.error('保存权限列表失败', error);
  }
};

/** 保存菜单树 */
export const saveMenuTree = (menuTree: MenuDataItem[]): void => {
  try {
    localStorage.setItem(MENU_KEY, JSON.stringify(menuTree));
  } catch (error) {
    console.error('保存菜单树失败', error);
  }
};

/**
 * 登录成功后落地认证信息（POST /auth/login 的 data）。
 * user 仅作为展示缓存，权限仍由后端接口决定。
 */
export const saveAuth = (data: API.LoginVO): void => {
  if (!data?.accessToken) {
    return;
  }
  try {
    localStorage.setItem(TOKEN_KEY, data.accessToken);
    if (data.refreshToken) {
      localStorage.setItem(REFRESH_TOKEN_KEY, data.refreshToken);
    }
    if (data.user) {
      localStorage.setItem(USER_KEY, JSON.stringify(data.user));
    }
  } catch (error) {
    console.error('保存登录信息失败', error);
  }
};

/** 重新拉取当前用户后更新展示缓存 */
export const saveUserInfo = (user: API.CurrentUser): void => {
  try {
    localStorage.setItem(USER_KEY, JSON.stringify(user));
  } catch (error) {
    console.error('保存用户信息失败', error);
  }
};

/** 清空全部本地登录态缓存 */
export const clearAuth = (): void => {
  AUTH_STORAGE_KEYS.forEach((key) => {
    try {
      localStorage.removeItem(key);
    } catch {
      // 忽略 storage 不可用的场景
    }
  });
};

/** token 存在且本地判断未过期 */
export const isAuthenticated = (): boolean => {
  const token = getToken();
  return Boolean(token) && !isTokenExpired(token);
};
