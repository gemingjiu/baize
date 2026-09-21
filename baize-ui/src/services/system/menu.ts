// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** 查询菜单树 POST /system/menu/tree */
export async function getMenuTree(body?: Partial<API.SysMenuDto>, options?: { [key: string]: any }) {
  return request<API.ApiResult<API.SysMenuDto[]>>('/system/menu/tree', {
    method: 'POST',
    data: body,
    ...(options || {}),
  });
}

/** 获取菜单树选择框数据 GET /system/menu/treeselect */
export async function getMenuTreeSelect(tenantId?: string, options?: { [key: string]: any }) {
  return request<API.ApiResult<API.SysMenuDto[]>>('/system/menu/treeselect', {
    method: 'GET',
    params: { tenantId },
    ...(options || {}),
  });
}

/** 获取角色菜单树选择框数据 GET /system/menu/roleMenuTreeselect/{roleId} */
export async function getRoleMenuTreeSelect(roleId: string, tenantId?: string, options?: { [key: string]: any }) {
  return request<API.ApiResult<API.SysMenuDto[]>>(`/system/menu/roleMenuTreeselect/${roleId}`, {
    method: 'GET',
    params: { tenantId },
    ...(options || {}),
  });
}

/** 根据用户ID查询菜单树 GET /system/menu/user/tree */
export async function getUserMenuTree(userId: string, options?: { [key: string]: any }): Promise<API.ApiResult<API.SysMenuDto[]>>;
export async function getUserMenuTree(options?: { [key: string]: any }): Promise<API.ApiResult<API.SysMenuDto[]>>;
export async function getUserMenuTree(userIdOrOptions?: string | { [key: string]: any }, options?: { [key: string]: any }): Promise<API.ApiResult<API.SysMenuDto[]>> {
  if (typeof userIdOrOptions === 'string') {
    return request<API.ApiResult<API.SysMenuDto[]>>('/system/menu/user/tree', {
      method: 'GET',
      headers: {
        'X-User-Id': userIdOrOptions,
      },
      ...(options || {}),
    });
  }
  return request<API.ApiResult<API.SysMenuDto[]>>('/system/menu/user/tree', {
    method: 'GET',
    ...(userIdOrOptions || {}),
  });
}

/** 分页查询菜单 POST /system/menu/page */
export async function getMenuPage(
  params: {
    current?: number;
    size?: number;
  },
  body: Partial<API.SysMenuDto>,
  options?: { [key: string]: any },
) {
  return request<API.ApiResult<API.MybatisPage<API.SysMenuDto>>>('/system/menu/page', {
    method: 'POST',
    params: {
      current: params.current,
      size: params.size,
    },
    data: body,
    ...(options || {}),
  });
}

/** 根据ID获取菜单 GET /system/menu/{id} */
export async function getMenuById(id: string, options?: { [key: string]: any }) {
  return request<API.ApiResult<API.SysMenuDto>>(`/system/menu/${id}`, {
    method: 'GET',
    ...(options || {}),
  });
}

/** 创建菜单 POST /system/menu */
export async function createMenu(data: Partial<API.SysMenuDto>, options?: { [key: string]: any }) {
  return request<API.ApiResult<number>>('/system/menu', {
    method: 'POST',
    data,
    ...(options || {}),
  });
}

/** 更新菜单 PUT /system/menu/{id} */
export async function updateMenu(id: string, data: Partial<API.SysMenuDto>, options?: { [key: string]: any }) {
  return request<API.ApiResult>(`/system/menu/${id}`, {
    method: 'PUT',
    data,
    ...(options || {}),
  });
}

/** 删除菜单 DELETE /system/menu/{id} */
export async function deleteMenu(id: string, options?: { [key: string]: any }) {
  return request<API.ApiResult>(`/system/menu/${id}`, {
    method: 'DELETE',
    ...(options || {}),
  });
}
