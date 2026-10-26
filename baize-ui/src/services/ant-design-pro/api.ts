// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';
import { API_VERSION } from '@/constants/api';

/** 用户登录 POST /auth/login */
export async function login(body: API.LoginDTO, options?: { [key: string]: any }) {
  return request<API.ApiResult<API.LoginVO>>(`${API_VERSION}/auth/login`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 用户登出 POST /auth/logout */
export async function logout(options?: { [key: string]: any }) {
  return request<API.ApiResult>(`${API_VERSION}/auth/logout`, {
    method: 'POST',
    ...(options || {}),
  });
}

/** 刷新令牌 POST /auth/refresh */
export async function refresh(body: API.TokenRefreshDTO, options?: { [key: string]: any }) {
  return request<API.ApiResult<API.LoginVO>>(`${API_VERSION}/auth/refresh`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 获取当前登录用户信息 GET /system/user/current */
export async function currentUser(options?: { [key: string]: any }) {
  return request<API.ApiResult<API.UserVO>>(`${API_VERSION}/system/user/current`, {
    method: 'GET',
    ...(options || {}),
  });
}
