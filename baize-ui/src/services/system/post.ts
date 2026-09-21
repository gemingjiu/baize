// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** 分页查询岗位 POST /system/post/page */
export async function getPostPage(
  params: {
    current?: number;
    size?: number;
  },
  body: Partial<API.SysPostDto>,
  options?: { [key: string]: any },
) {
  return request<API.ApiResult<API.MybatisPage<API.SysPostDto>>>('/system/post/page', {
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
  return request<API.ApiResult<API.SysPostDto>>(`/system/post/${id}`, {
    method: 'GET',
    ...(options || {}),
  });
}

/** 创建岗位 POST /system/post */
export async function createPost(data: Partial<API.SysPostDto>, options?: { [key: string]: any }) {
  return request<API.ApiResult<number>>('/system/post', {
    method: 'POST',
    data,
    ...(options || {}),
  });
}

/** 更新岗位 PUT /system/post/{id} */
export async function updatePost(id: string, data: Partial<API.SysPostDto>, options?: { [key: string]: any }) {
  return request<API.ApiResult>(`/system/post/${id}`, {
    method: 'PUT',
    data,
    ...(options || {}),
  });
}

/** 删除岗位 DELETE /system/post/{id} */
export async function deletePost(id: string, options?: { [key: string]: any }) {
  return request<API.ApiResult>(`/system/post/${id}`, {
    method: 'DELETE',
    ...(options || {}),
  });
}
