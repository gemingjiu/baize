// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** 分页查询租户 POST /system/tenant/page */
export async function getTenantPage(
  params: {
    current?: number;
    size?: number;
  },
  body: Partial<API.SysTenantDto>,
  options?: { [key: string]: any },
) {
  return request<API.ApiResult<API.MybatisPage<API.SysTenantDto>>>('/system/tenant/page', {
    method: 'POST',
    params: {
      current: params.current,
      size: params.size,
    },
    data: body,
    ...(options || {}),
  });
}

/** 根据租户编码或域名获取租户 GET /system/tenant/getByTenant */
export async function getByTenantCodeOrDomain(tenant: string, options?: { [key: string]: any }) {
  return request<API.ApiResult<API.SysTenantDto>>('/system/tenant/getByTenant', {
    method: 'GET',
    params: { tenant },
    ...(options || {}),
  });
}

/** 根据ID获取租户 GET /system/tenant/{id} */
export async function getTenantById(id: string, options?: { [key: string]: any }) {
  return request<API.ApiResult<API.SysTenantDto>>(`/system/tenant/${id}`, {
    method: 'GET',
    ...(options || {}),
  });
}

/** 创建租户 POST /system/tenant */
export async function createTenant(data: Partial<API.SysTenantDto>, options?: { [key: string]: any }) {
  return request<API.ApiResult<number>>('/system/tenant', {
    method: 'POST',
    data,
    ...(options || {}),
  });
}

/** 更新租户 PUT /system/tenant/{id} */
export async function updateTenant(id: string, data: Partial<API.SysTenantDto>, options?: { [key: string]: any }) {
  return request<API.ApiResult>(`/system/tenant/${id}`, {
    method: 'PUT',
    data,
    ...(options || {}),
  });
}

/** 删除租户 DELETE /system/tenant/{id} */
export async function deleteTenant(id: string, options?: { [key: string]: any }) {
  return request<API.ApiResult>(`/system/tenant/${id}`, {
    method: 'DELETE',
    ...(options || {}),
  });
}
