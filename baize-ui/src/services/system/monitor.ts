// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';
import { API_VERSION } from '@/constants/api';

/** 获取服务器信息 GET /system/monitor/server */
export async function getServerInfo(options?: { [key: string]: any }) {
  return request<API.ApiResult<API.ServerInfoVO>>(`${API_VERSION}/system/monitor/server`, {
    method: 'GET',
    ...(options || {}),
  });
}

/**
 * 获取 Redis 缓存监控信息 GET /system/monitor/cache
 *
 * 后端：baize-system 的 CacheMonitorController。
 * 返回版本/客户端数/内存/命中率/运行时长，以及按业务前缀分组的 key 数量。
 * Redis 不可用时返回 unavailable=true + errorMessage（HTTP 仍为 200），
 * 页面据此展示 Alert，而不是把整页打崩。
 */
export async function getCacheInfo(options?: { [key: string]: any }) {
  return request<API.ApiResult<API.CacheInfoVO>>(`${API_VERSION}/system/monitor/cache`, {
    method: 'GET',
    ...(options || {}),
  });
}

/**
 * 查询缓存 key 列表 GET /system/monitor/cache/keys
 *
 * 后端用 SCAN（不是 KEYS）扫描，最多返回 200 条；达到上限时 truncated=true。
 * @param pattern 匹配表达式，默认 '*'，例如 'sys_config::*'
 */
export async function getCacheKeys(
  params?: { pattern?: string; limit?: number },
  options?: { [key: string]: any },
) {
  return request<API.ApiResult<API.CacheKeyVO>>(`${API_VERSION}/system/monitor/cache/keys`, {
    method: 'GET',
    params: params as any,
    ...(options || {}),
  });
}
