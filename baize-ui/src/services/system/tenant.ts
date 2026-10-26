// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';
import { API_VERSION } from '@/constants/api';

/** 分页查询租户 POST /system/tenant/page */
export async function getTenantPage(
  params: {
    current?: number;
    size?: number;
  },
  body: Partial<API.SysTenantDto>,
  options?: { [key: string]: any },
) {
  return request<API.ApiResult<API.PageResult<API.SysTenantDto>>>(`${API_VERSION}/system/tenant/page`, {
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
  return request<API.ApiResult<API.SysTenantDto>>(`${API_VERSION}/system/tenant/getByTenant`, {
    method: 'GET',
    params: { tenant },
    ...(options || {}),
  });
}

/** 根据ID获取租户 GET /system/tenant/{id} */
export async function getTenantById(id: string, options?: { [key: string]: any }) {
  return request<API.ApiResult<API.SysTenantDto>>(`${API_VERSION}/system/tenant/${id}`, {
    method: 'GET',
    ...(options || {}),
  });
}

/** 创建租户 POST /system/tenant */
export async function createTenant(data: Partial<API.SysTenantDto>, options?: { [key: string]: any }) {
  return request<API.ApiResult<number>>(`${API_VERSION}/system/tenant`, {
    method: 'POST',
    data,
    ...(options || {}),
  });
}

/** 更新租户 PUT /system/tenant/{id} */
export async function updateTenant(id: string, data: Partial<API.SysTenantDto>, options?: { [key: string]: any }) {
  return request<API.ApiResult>(`${API_VERSION}/system/tenant/${id}`, {
    method: 'PUT',
    data: { ...data, id },
    ...(options || {}),
  });
}

/** 删除租户 DELETE /system/tenant/{id} */
export async function deleteTenant(id: string, options?: { [key: string]: any }) {
  return request<API.ApiResult>(`${API_VERSION}/system/tenant/${id}`, {
    method: 'DELETE',
    ...(options || {}),
  });
}

/**
 * 可用租户列表 GET /system/tenant/list
 *
 * 免鉴权接口（网关白名单放行 /api/v1/system/tenant/list），登录页在拿到令牌前就要用它渲染
 * 租户下拉框，因此这里不能依赖 requestErrorConfig 注入的 Authorization 头 —— 没有令牌时
 * 拦截器不会注入，有令牌时多带一个头也不影响。
 */
export async function getTenantList(options?: { [key: string]: any }) {
  return request<API.ApiResult<API.SysTenantDto[]>>(`${API_VERSION}/system/tenant/list`, {
    method: 'GET',
    ...(options || {}),
  });
}
