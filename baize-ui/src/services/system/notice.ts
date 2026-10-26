// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';
import { API_VERSION } from '@/constants/api';

/** 分页查询通知公告 POST /system/notice/page */
export async function getNoticePage(
  params: {
    current?: number;
    size?: number;
  },
  body: Partial<API.SysNoticeDto>,
  options?: { [key: string]: any },
) {
  return request<API.ApiResult<API.PageResult<API.SysNoticeDto>>>(`${API_VERSION}/system/notice/page`, {
    method: 'POST',
    params: {
      current: params.current,
      size: params.size,
    },
    data: body,
    ...(options || {}),
  });
}

/** 根据ID获取通知公告 GET /system/notice/{id} */
export async function getNoticeById(id: string, options?: { [key: string]: any }) {
  return request<API.ApiResult<API.SysNoticeDto>>(`${API_VERSION}/system/notice/${id}`, {
    method: 'GET',
    ...(options || {}),
  });
}

/** 创建通知公告 POST /system/notice */
export async function createNotice(data: Partial<API.SysNoticeDto>, options?: { [key: string]: any }) {
  return request<API.ApiResult>(`${API_VERSION}/system/notice`, {
    method: 'POST',
    data,
    ...(options || {}),
  });
}

/** 更新通知公告 PUT /system/notice/{id} */
export async function updateNotice(id: string, data: Partial<API.SysNoticeDto>, options?: { [key: string]: any }) {
  return request<API.ApiResult>(`${API_VERSION}/system/notice/${id}`, {
    method: 'PUT',
    data: { ...data, id },
    ...(options || {}),
  });
}

/** 删除通知公告 DELETE /system/notice/{id} */
export async function deleteNotice(id: string, options?: { [key: string]: any }) {
  return request<API.ApiResult>(`${API_VERSION}/system/notice/${id}`, {
    method: 'DELETE',
    ...(options || {}),
  });
}
