// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** 分页查询角色 POST /system/role/page */
export async function getRolePage(
  params: {
    current?: number;
    size?: number;
  },
  body: Partial<API.SysRoleDto>,
  options?: { [key: string]: any },
) {
  return request<API.ApiResult<API.MybatisPage<API.SysRoleDto>>>('/system/role/page', {
    method: 'POST',
    params: {
      current: params.current,
      size: params.size,
    },
    data: body,
    ...(options || {}),
  });
}

/** 根据ID获取角色 GET /system/role/{id} */
export async function getRoleById(id: string, options?: { [key: string]: any }) {
  return request<API.ApiResult<API.SysRoleDto>>(`/system/role/${id}`, {
    method: 'GET',
    ...(options || {}),
  });
}

/** 创建角色 POST /system/role */
export async function createRole(data: Partial<API.SysRoleDto>, options?: { [key: string]: any }) {
  return request<API.ApiResult<number>>('/system/role', {
    method: 'POST',
    data,
    ...(options || {}),
  });
}

/** 更新角色 PUT /system/role/{id} */
export async function updateRole(id: string, data: Partial<API.SysRoleDto>, options?: { [key: string]: any }) {
  return request<API.ApiResult>(`/system/role/${id}`, {
    method: 'PUT',
    data,
    ...(options || {}),
  });
}

/** 删除角色 DELETE /system/role/{id} */
export async function deleteRole(id: string, options?: { [key: string]: any }) {
  return request<API.ApiResult>(`/system/role/${id}`, {
    method: 'DELETE',
    ...(options || {}),
  });
}

/** 获取角色菜单ID列表 GET /system/role/menu/{roleId}/menus */
export async function getRoleMenus(roleId: string, options?: { [key: string]: any }) {
  return request<API.ApiResult<string[]>>(`/system/role/menu/${roleId}/menus`, {
    method: 'GET',
    ...(options || {}),
  });
}

/** 为角色分配菜单 POST /system/role/menu/{roleId}/assign */
export async function assignRoleMenus(roleId: string, menuIds: string[], options?: { [key: string]: any }) {
  return request<API.ApiResult>(`/system/role/menu/${roleId}/assign`, {
    method: 'POST',
    data: menuIds,
    ...(options || {}),
  });
}

/** 删除角色所有菜单关联 DELETE /system/role/menu/{roleId} */
export async function removeRoleMenus(roleId: string, options?: { [key: string]: any }) {
  return request<API.ApiResult>(`/system/role/menu/${roleId}`, {
    method: 'DELETE',
    ...(options || {}),
  });
}

/** 获取角色权限ID列表 GET /system/role/perm/{roleId}/perms */
export async function getRolePerms(roleId: string, options?: { [key: string]: any }) {
  return request<API.ApiResult<string[]>>(`/system/role/perm/${roleId}/perms`, {
    method: 'GET',
    ...(options || {}),
  });
}

/** 为角色分配权限 POST /system/role/perm/{roleId}/assign */
export async function assignRolePerms(roleId: string, permIds: string[], options?: { [key: string]: any }) {
  return request<API.ApiResult>(`/system/role/perm/${roleId}/assign`, {
    method: 'POST',
    data: permIds,
    ...(options || {}),
  });
}

/** 删除角色所有权限关联 DELETE /system/role/perm/{roleId} */
export async function removeRolePerms(roleId: string, options?: { [key: string]: any }) {
  return request<API.ApiResult>(`/system/role/perm/${roleId}`, {
    method: 'DELETE',
    ...(options || {}),
  });
}

/** 根据角色ID列表获取权限编码列表 GET /system/role/perm/roles/codes */
export async function getPermCodesByRoleIds(roleIds: string[], options?: { [key: string]: any }) {
  return request<API.ApiResult<string[]>>('/system/role/perm/roles/codes', {
    method: 'GET',
    params: { roleIds },
    ...(options || {}),
  });
}

/** 根据角色ID列表获取菜单树 GET /system/role/menu/roles/tree */
export async function getMenuTreeByRoleIds(roleIds: string[], options?: { [key: string]: any }) {
  return request<API.ApiResult<API.SysMenuDto[]>>('/system/role/menu/roles/tree', {
    method: 'GET',
    params: { roleIds },
    ...(options || {}),
  });
}
