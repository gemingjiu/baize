// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** 分页查询参数配置 POST /system/config/page */
export async function getConfigPage(
  params: {
    current?: number;
    size?: number;
  },
  body: Partial<API.SysConfigDto>,
  options?: { [key: string]: any },
) {
  return request<API.ApiResult<API.MybatisPage<API.SysConfigDto>>>('/system/config/page', {
    method: 'POST',
    params: {
      current: params.current,
      size: params.size,
    },
    data: body,
    ...(options || {}),
  });
}

/** 根据ID获取参数配置 GET /system/config/{id} */
export async function getConfigById(id: string, options?: { [key: string]: any }) {
  return request<API.ApiResult<API.SysConfigDto>>(`/system/config/${id}`, {
    method: 'GET',
    ...(options || {}),
  });
}

/** 根据Key获取参数配置 GET /system/config/key/{configKey} */
export async function getConfigByKey(configKey: string, options?: { [key: string]: any }) {
  return request<API.ApiResult<API.SysConfigDto>>(`/system/config/key/${configKey}`, {
    method: 'GET',
    ...(options || {}),
  });
}

/** 创建参数配置 POST /system/config */
export async function createConfig(data: Partial<API.SysConfigDto>, options?: { [key: string]: any }) {
  return request<API.ApiResult<number>>('/system/config', {
    method: 'POST',
    data,
    ...(options || {}),
  });
}

/** 更新参数配置 PUT /system/config/{id} */
export async function updateConfig(id: string, data: Partial<API.SysConfigDto>, options?: { [key: string]: any }) {
  return request<API.ApiResult>(`/system/config/${id}`, {
    method: 'PUT',
    data,
    ...(options || {}),
  });
}

/** 删除参数配置 DELETE /system/config/{id} */
export async function deleteConfig(id: string, options?: { [key: string]: any }) {
  return request<API.ApiResult>(`/system/config/${id}`, {
    method: 'DELETE',
    ...(options || {}),
  });
}
