// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';
import { message } from 'antd';
import { API_VERSION } from '@/constants/api';
import { downloadExportFile } from '@/utils/exportExcel';

/** 分页查询角色 POST /system/role/page */
export async function getRolePage(
  params: {
    current?: number;
    size?: number;
  },
  body: Partial<API.SysRoleDto>,
  options?: { [key: string]: any },
) {
  return request<API.ApiResult<API.PageResult<API.SysRoleDto>>>(`${API_VERSION}/system/role/page`, {
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
  return request<API.ApiResult<API.SysRoleDto>>(`${API_VERSION}/system/role/${id}`, {
    method: 'GET',
    ...(options || {}),
  });
}

/** 创建角色 POST /system/role */
export async function createRole(data: Partial<API.SysRoleDto>, options?: { [key: string]: any }) {
  return request<API.ApiResult<number>>(`${API_VERSION}/system/role`, {
    method: 'POST',
    data,
    ...(options || {}),
  });
}

/** 更新角色 PUT /system/role/{id} */
export async function updateRole(id: string, data: Partial<API.SysRoleDto>, options?: { [key: string]: any }) {
  return request<API.ApiResult>(`${API_VERSION}/system/role/${id}`, {
    method: 'PUT',
    data: { ...data, id },
    ...(options || {}),
  });
}

/** 删除角色 DELETE /system/role/{id} */
export async function deleteRole(id: string, options?: { [key: string]: any }) {
  return request<API.ApiResult>(`${API_VERSION}/system/role/${id}`, {
    method: 'DELETE',
    ...(options || {}),
  });
}

/** 获取角色菜单ID列表 GET /system/role/menu/{roleId}/menus */
export async function getRoleMenus(roleId: string, options?: { [key: string]: any }) {
  return request<API.ApiResult<string[]>>(`${API_VERSION}/system/role/menu/${roleId}/menus`, {
    method: 'GET',
    ...(options || {}),
  });
}

/** 为角色分配菜单 POST /system/role/menu/{roleId}/assign */
export async function assignRoleMenus(roleId: string, menuIds: string[], options?: { [key: string]: any }) {
  return request<API.ApiResult>(`${API_VERSION}/system/role/menu/${roleId}/assign`, {
    method: 'POST',
    data: menuIds,
    ...(options || {}),
  });
}

/** 删除角色所有菜单关联 DELETE /system/role/menu/{roleId} */
export async function removeRoleMenus(roleId: string, options?: { [key: string]: any }) {
  return request<API.ApiResult>(`${API_VERSION}/system/role/menu/${roleId}`, {
    method: 'DELETE',
    ...(options || {}),
  });
}

/** 获取角色权限ID列表 GET /system/role/perm/{roleId}/perms */
export async function getRolePerms(roleId: string, options?: { [key: string]: any }) {
  return request<API.ApiResult<string[]>>(`${API_VERSION}/system/role/perm/${roleId}/perms`, {
    method: 'GET',
    ...(options || {}),
  });
}

/** 为角色分配权限 POST /system/role/perm/{roleId}/assign */
export async function assignRolePerms(roleId: string, permIds: string[], options?: { [key: string]: any }) {
  return request<API.ApiResult>(`${API_VERSION}/system/role/perm/${roleId}/assign`, {
    method: 'POST',
    data: permIds,
    ...(options || {}),
  });
}

/** 删除角色所有权限关联 DELETE /system/role/perm/{roleId} */
export async function removeRolePerms(roleId: string, options?: { [key: string]: any }) {
  return request<API.ApiResult>(`${API_VERSION}/system/role/perm/${roleId}`, {
    method: 'DELETE',
    ...(options || {}),
  });
}

/** 根据角色ID列表获取权限编码列表 GET /system/role/perm/roles/codes */
export async function getPermCodesByRoleIds(roleIds: string[], options?: { [key: string]: any }) {
  return request<API.ApiResult<string[]>>(`${API_VERSION}/system/role/perm/roles/codes`, {
    method: 'GET',
    params: { roleIds },
    ...(options || {}),
  });
}

/** 根据角色ID列表获取菜单树 GET /system/role/menu/roles/tree */
export async function getMenuTreeByRoleIds(roleIds: string[], options?: { [key: string]: any }) {
  return request<API.ApiResult<API.SysMenuDto[]>>(`${API_VERSION}/system/role/menu/roles/tree`, {
    method: 'GET',
    params: { roleIds },
    ...(options || {}),
  });
}

/** 获取角色已配置的部门ID列表 GET /system/role/dept/{roleId}/depts */
export async function getRoleDepts(roleId: string, options?: { [key: string]: any }) {
  return request<API.ApiResult<string[]>>(`${API_VERSION}/system/role/dept/${roleId}/depts`, {
    method: 'GET',
    ...(options || {}),
  });
}

/** 为角色分配部门（自定义数据权限）POST /system/role/dept/{roleId}/assign */
export async function assignRoleDepts(roleId: string, deptIds: string[], options?: { [key: string]: any }) {
  return request<API.ApiResult>(`${API_VERSION}/system/role/dept/${roleId}/assign`, {
    method: 'POST',
    data: deptIds,
    ...(options || {}),
  });
}

/** 删除角色所有部门关联 DELETE /system/role/dept/{roleId} */
export async function removeRoleDepts(roleId: string, options?: { [key: string]: any }) {
  return request<API.ApiResult>(`${API_VERSION}/system/role/dept/${roleId}`, {
    method: 'DELETE',
    ...(options || {}),
  });
}

/**
 * 导出角色列表 POST /system/role/export
 *
 * 响应为 xlsx 二进制流，不能用 request()（会被当成 JSON 解析），
 * 这里走原生 fetch + Blob 下载，并带上登录态 Authorization 头。
 * 传空对象 {} 表示按当前条件为空的全量导出。
 * 成功弹「导出成功」，失败（非 2xx / 网络异常）弹「导出失败」，不静默。
 */
export async function exportRoles(params: Partial<API.SysRoleDto>): Promise<void> {
  const ok = await downloadExportFile<API.SysRoleDto>(
    `${API_VERSION}/system/role/export`,
    params,
    '角色列表.xlsx',
  );
  if (ok) {
    message.success('导出成功');
  } else {
    message.error('导出失败');
  }
}
