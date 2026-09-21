// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** 分页查询用户 POST /system/user/page */
export async function getUserPage(
  params: {
    current?: number;
    size?: number;
  },
  body: Partial<API.SysUserDto>,
  options?: { [key: string]: any },
) {
  return request<API.ApiResult<API.MybatisPage<API.SysUserDto>>>('/system/user/page', {
    method: 'POST',
    params: {
      current: params.current,
      size: params.size,
    },
    data: body,
    ...(options || {}),
  });
}

/** 根据ID获取用户 GET /system/user/{id} */
export async function getUserById(id: string, options?: { [key: string]: any }) {
  return request<API.ApiResult<API.SysUserDto>>(`/system/user/${id}`, {
    method: 'GET',
    ...(options || {}),
  });
}

/** 根据用户名查询用户 GET /system/user/getByUsername */
export async function getUserByUsername(username: string, options?: { [key: string]: any }) {
  return request<API.ApiResult<API.SysUserDto>>('/system/user/getByUsername', {
    method: 'GET',
    params: { username },
    ...(options || {}),
  });
}

/** 创建用户 POST /system/user */
export async function createUser(data: Partial<API.SysUserDto>, options?: { [key: string]: any }) {
  return request<API.ApiResult<number>>('/system/user', {
    method: 'POST',
    data,
    ...(options || {}),
  });
}

/** 更新用户 PUT /system/user/{id} */
export async function updateUser(id: string, data: Partial<API.SysUserDto>, options?: { [key: string]: any }) {
  return request<API.ApiResult>(`/system/user/${id}`, {
    method: 'PUT',
    data,
    ...(options || {}),
  });
}

/** 删除用户 DELETE /system/user/{id} */
export async function deleteUser(id: string, options?: { [key: string]: any }) {
  return request<API.ApiResult>(`/system/user/${id}`, {
    method: 'DELETE',
    ...(options || {}),
  });
}

/** 修改密码 PUT /system/user/updatePassword */
export async function updatePassword(
  userId: string,
  oldPassword: string,
  newPassword: string,
  options?: { [key: string]: any },
) {
  return request<API.ApiResult>('/system/user/updatePassword', {
    method: 'PUT',
    params: { userId, oldPassword, newPassword },
    ...(options || {}),
  });
}

/** 重置密码 PUT /system/user/resetPassword */
export async function resetPassword(userId: string, newPassword: string, options?: { [key: string]: any }) {
  return request<API.ApiResult>('/system/user/resetPassword', {
    method: 'PUT',
    params: { userId, newPassword },
    ...(options || {}),
  });
}

/** 修改用户状态 PUT /system/user/changeStatus */
export async function changeUserStatus(userId: string, status: string, options?: { [key: string]: any }) {
  return request<API.ApiResult>('/system/user/changeStatus', {
    method: 'PUT',
    params: { userId, status },
    ...(options || {}),
  });
}

/** 获取用户角色ID列表 GET /system/user/role/{userId}/roles */
export async function getUserRoles(userId: string, options?: { [key: string]: any }) {
  return request<API.ApiResult<string[]>>(`/system/user/role/${userId}/roles`, {
    method: 'GET',
    ...(options || {}),
  });
}

/** 为用户分配角色 POST /system/user/role/{userId}/assign */
export async function assignUserRoles(userId: string, roleIds: string[], options?: { [key: string]: any }) {
  return request<API.ApiResult>(`/system/user/role/${userId}/assign`, {
    method: 'POST',
    data: roleIds,
    ...(options || {}),
  });
}

/** 删除用户所有角色关联 DELETE /system/user/role/{userId} */
export async function removeUserRoles(userId: string, options?: { [key: string]: any }) {
  return request<API.ApiResult>(`/system/user/role/${userId}`, {
    method: 'DELETE',
    ...(options || {}),
  });
}
