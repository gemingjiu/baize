// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';
import { API_VERSION } from '@/constants/api';

/** 分页查询权限 POST /system/perm/page */
export async function getPermPage(
  params: {
    current?: number;
    size?: number;
  },
  body: Partial<API.SysPermDto>,
  options?: { [key: string]: any },
) {
  return request<API.ApiResult<API.PageResult<API.SysPermDto>>>(`${API_VERSION}/system/perm/page`, {
    method: 'POST',
    params: {
      current: params.current,
      size: params.size,
    },
    data: body,
    ...(options || {}),
  });
}

/** 根据ID获取权限 GET /system/perm/{id} */
export async function getPermById(id: string, options?: { [key: string]: any }) {
  return request<API.ApiResult<API.SysPermDto>>(`${API_VERSION}/system/perm/${id}`, {
    method: 'GET',
    ...(options || {}),
  });
}

/** 创建权限 POST /system/perm */
export async function createPerm(data: Partial<API.SysPermDto>, options?: { [key: string]: any }) {
  return request<API.ApiResult<number>>(`${API_VERSION}/system/perm`, {
    method: 'POST',
    data,
    ...(options || {}),
  });
}

/** 更新权限 PUT /system/perm/{id} */
export async function updatePerm(id: string, data: Partial<API.SysPermDto>, options?: { [key: string]: any }) {
  return request<API.ApiResult>(`${API_VERSION}/system/perm/${id}`, {
    method: 'PUT',
    data: { ...data, id },
    ...(options || {}),
  });
}

/** 删除权限 DELETE /system/perm/{id} */
export async function deletePerm(id: string, options?: { [key: string]: any }) {
  return request<API.ApiResult>(`${API_VERSION}/system/perm/${id}`, {
    method: 'DELETE',
    ...(options || {}),
  });
}

/** 查询权限树 GET /system/perm/tree */
export async function getPermTree(options?: { [key: string]: any }) {
  return request<API.ApiResult<API.SysPermDto[]>>(`${API_VERSION}/system/perm/tree`, {
    method: 'GET',
    ...(options || {}),
  });
}
