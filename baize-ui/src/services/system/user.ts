// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';
import { message } from 'antd';
import { API_VERSION } from '@/constants/api';
import { downloadExportFile } from '@/utils/exportExcel';

/** 分页查询用户 POST /system/user/page */
export async function getUserPage(
  params: {
    current?: number;
    size?: number;
  },
  body: Partial<API.SysUserDto>,
  options?: { [key: string]: any },
) {
  return request<API.ApiResult<API.PageResult<API.SysUserDto>>>(`${API_VERSION}/system/user/page`, {
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
  return request<API.ApiResult<API.SysUserDto>>(`${API_VERSION}/system/user/${id}`, {
    method: 'GET',
    ...(options || {}),
  });
}

/** 根据用户名查询用户 GET /system/user/getByUsername */
export async function getUserByUsername(username: string, options?: { [key: string]: any }) {
  return request<API.ApiResult<API.SysUserDto>>(`${API_VERSION}/system/user/getByUsername`, {
    method: 'GET',
    params: { username },
    ...(options || {}),
  });
}

/** 创建用户 POST /system/user */
export async function createUser(data: Partial<API.SysUserDto>, options?: { [key: string]: any }) {
  return request<API.ApiResult<number>>(`${API_VERSION}/system/user`, {
    method: 'POST',
    data,
    ...(options || {}),
  });
}

/** 更新用户 PUT /system/user/{id} */
export async function updateUser(id: string, data: Partial<API.SysUserDto>, options?: { [key: string]: any }) {
  return request<API.ApiResult>(`${API_VERSION}/system/user/${id}`, {
    method: 'PUT',
    data: { ...data, id },
    ...(options || {}),
  });
}

/** 删除用户 DELETE /system/user/{id} */
export async function deleteUser(id: string, options?: { [key: string]: any }) {
  return request<API.ApiResult>(`${API_VERSION}/system/user/${id}`, {
    method: 'DELETE',
    ...(options || {}),
  });
}

/** 修改密码 PUT /system/user/updatePassword（密码走 body，避免出现在 URL/access log） */
export async function updatePassword(
  userId: string,
  oldPassword: string,
  newPassword: string,
  options?: { [key: string]: any },
) {
  return request<API.ApiResult>(`${API_VERSION}/system/user/updatePassword`, {
    method: 'PUT',
    data: { userId, oldPassword, newPassword },
    ...(options || {}),
  });
}

/** 重置密码 PUT /system/user/resetPassword（密码走 body，避免出现在 URL/access log） */
export async function resetPassword(userId: string, newPassword: string, options?: { [key: string]: any }) {
  return request<API.ApiResult>(`${API_VERSION}/system/user/resetPassword`, {
    method: 'PUT',
    data: { userId, newPassword },
    ...(options || {}),
  });
}

/** 修改用户状态 PUT /system/user/changeStatus */
export async function changeUserStatus(userId: string, status: string, options?: { [key: string]: any }) {
  return request<API.ApiResult>(`${API_VERSION}/system/user/changeStatus`, {
    method: 'PUT',
    params: { userId, status },
    ...(options || {}),
  });
}

/** 获取用户角色ID列表 GET /system/user/role/{userId}/roles */
export async function getUserRoles(userId: string, options?: { [key: string]: any }) {
  return request<API.ApiResult<string[]>>(`${API_VERSION}/system/user/role/${userId}/roles`, {
    method: 'GET',
    ...(options || {}),
  });
}

/** 为用户分配角色 POST /system/user/role/{userId}/assign */
export async function assignUserRoles(userId: string, roleIds: string[], options?: { [key: string]: any }) {
  return request<API.ApiResult>(`${API_VERSION}/system/user/role/${userId}/assign`, {
    method: 'POST',
    data: roleIds,
    ...(options || {}),
  });
}

/** 删除用户所有角色关联 DELETE /system/user/role/{userId} */
export async function removeUserRoles(userId: string, options?: { [key: string]: any }) {
  return request<API.ApiResult>(`${API_VERSION}/system/user/role/${userId}`, {
    method: 'DELETE',
    ...(options || {}),
  });
}

/**
 * 导出用户列表 POST /system/user/export
 *
 * 响应为 xlsx 二进制流，不能用 request()（会被当成 JSON 解析），
 * 这里走原生 fetch + Blob 下载，并带上登录态 Authorization 头。
 * 传空对象 {} 表示按当前条件为空的全量导出。
 * 成功弹「导出成功」，失败（非 2xx / 网络异常）弹「导出失败」，不静默。
 */
export async function exportUsers(params: Partial<API.SysUserDto>): Promise<void> {
  const ok = await downloadExportFile<API.SysUserDto>(
    `${API_VERSION}/system/user/export`,
    params,
    '用户列表.xlsx',
  );
  if (ok) {
    message.success('导出成功');
  } else {
    message.error('导出失败');
  }
}
