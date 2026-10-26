// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';
import { API_VERSION } from '@/constants/api';

/** 分页查询岗位 POST /system/post/page */
export async function getPostPage(
  params: {
    current?: number;
    size?: number;
  },
  body: Partial<API.SysPostDto>,
  options?: { [key: string]: any },
) {
  return request<API.ApiResult<API.PageResult<API.SysPostDto>>>(`${API_VERSION}/system/post/page`, {
    method: 'POST',
    params: {
      current: params.current,
      size: params.size,
    },
    data: body,
    ...(options || {}),
  });
}

/** 根据ID获取岗位 GET /system/post/{id} */
export async function getPostById(id: string, options?: { [key: string]: any }) {
  return request<API.ApiResult<API.SysPostDto>>(`${API_VERSION}/system/post/${id}`, {
    method: 'GET',
    ...(options || {}),
  });
}

/** 创建岗位 POST /system/post */
export async function createPost(data: Partial<API.SysPostDto>, options?: { [key: string]: any }) {
  return request<API.ApiResult<number>>(`${API_VERSION}/system/post`, {
    method: 'POST',
    data,
    ...(options || {}),
  });
}

/** 更新岗位 PUT /system/post/{id} */
export async function updatePost(id: string, data: Partial<API.SysPostDto>, options?: { [key: string]: any }) {
  return request<API.ApiResult>(`${API_VERSION}/system/post/${id}`, {
    method: 'PUT',
    data: { ...data, id },
    ...(options || {}),
  });
}

/** 删除岗位 DELETE /system/post/{id} */
export async function deletePost(id: string, options?: { [key: string]: any }) {
  return request<API.ApiResult>(`${API_VERSION}/system/post/${id}`, {
    method: 'DELETE',
    ...(options || {}),
  });
}
