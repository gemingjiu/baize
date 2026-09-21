// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** 查询部门树 POST /system/dept/tree */
export async function getDeptTree(body?: Partial<API.SysDeptDto>, options?: { [key: string]: any }) {
  return request<API.ApiResult<API.SysDeptDto[]>>('/system/dept/tree', {
    method: 'POST',
    data: body,
    ...(options || {}),
  });
}

/** 获取部门树选择框数据 GET /system/dept/treeselect */
export async function getDeptTreeSelect(tenantId?: string, options?: { [key: string]: any }) {
  return request<API.ApiResult<API.SysDeptDto[]>>('/system/dept/treeselect', {
    method: 'GET',
    params: { tenantId },
    ...(options || {}),
  });
}

/** 获取部门列表 GET /system/dept/list */
export async function getDeptList(dto: Partial<API.SysDeptDto>, options?: { [key: string]: any }) {
  return request<API.ApiResult<API.SysDeptDto[]>>('/system/dept/list', {
    method: 'GET',
    params: { dto },
    ...(options || {}),
  });
}

/** 分页查询部门 POST /system/dept/page */
export async function getDeptPage(
  params: {
    current?: number;
    size?: number;
  },
  body: Partial<API.SysDeptDto>,
  options?: { [key: string]: any },
) {
  return request<API.ApiResult<API.MybatisPage<API.SysDeptDto>>>('/system/dept/page', {
    method: 'POST',
    params: {
      current: params.current,
      size: params.size,
    },
    data: body,
    ...(options || {}),
  });
}

/** 根据ID获取部门 GET /system/dept/{id} */
export async function getDeptById(id: string, options?: { [key: string]: any }) {
  return request<API.ApiResult<API.SysDeptDto>>(`/system/dept/${id}`, {
    method: 'GET',
    ...(options || {}),
  });
}

/** 创建部门 POST /system/dept */
export async function createDept(data: Partial<API.SysDeptDto>, options?: { [key: string]: any }) {
  return request<API.ApiResult<number>>('/system/dept', {
    method: 'POST',
    data,
    ...(options || {}),
  });
}

/** 更新部门 PUT /system/dept/{id} */
export async function updateDept(id: string, data: Partial<API.SysDeptDto>, options?: { [key: string]: any }) {
  return request<API.ApiResult>(`/system/dept/${id}`, {
    method: 'PUT',
    data,
    ...(options || {}),
  });
}

/** 删除部门 DELETE /system/dept/{id} */
export async function deleteDept(id: string, options?: { [key: string]: any }) {
  return request<API.ApiResult>(`/system/dept/${id}`, {
    method: 'DELETE',
    ...(options || {}),
  });
}
