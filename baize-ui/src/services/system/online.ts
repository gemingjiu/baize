// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';
import { API_VERSION } from '@/constants/api';

/**
 * 在线用户相关接口。
 *
 * 后端：baize-system 的 OnlineUserController（位于 user/controller 目录下），
 * 数据源是 Redis ZSet（key = baize:online:{tenantId}，30 分钟无活动视为离线）。
 *
 * 注意：GET /list 只返回**用户 ID 列表**（List<String>），不返回用户名/昵称/IP。
 * 页面需要用 userId 回查用户信息（GET /system/user/{id}）才能展示可读列。
 */

/** 获取在线用户数量 GET /system/online/count */
export async function getOnlineCount(
  tenantId?: string,
  options?: { [key: string]: any },
) {
  return request<API.ApiResult<number>>(`${API_VERSION}/system/online/count`, {
    method: 'GET',
    params: { tenantId },
    ...(options || {}),
  });
}

/** 获取在线用户ID列表 GET /system/online/list */
export async function getOnlineUsers(
  tenantId?: string,
  options?: { [key: string]: any },
) {
  return request<API.ApiResult<string[]>>(`${API_VERSION}/system/online/list`, {
    method: 'GET',
    params: { tenantId },
    ...(options || {}),
  });
}

/** 检查用户是否在线 GET /system/online/isOnline */
export async function isOnline(
  userId: string,
  tenantId?: string,
  options?: { [key: string]: any },
) {
  return request<API.ApiResult<boolean>>(`${API_VERSION}/system/online/isOnline`, {
    method: 'GET',
    params: { userId, tenantId },
    ...(options || {}),
  });
}

/** 强制用户下线 POST /system/online/kickout */
export async function kickoutUser(
  userId: string,
  tenantId?: string,
  options?: { [key: string]: any },
) {
  return request<API.ApiResult>(`${API_VERSION}/system/online/kickout`, {
    method: 'POST',
    params: { userId, tenantId },
    ...(options || {}),
  });
}
