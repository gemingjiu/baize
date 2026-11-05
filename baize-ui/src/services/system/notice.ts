// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';
import { API_VERSION } from '@/constants/api';

/**
 * 最新启用的公告列表 GET /system/notice/latest（P3 公告展示闭环）
 *
 * 登录即可见：首页公告栏 / 登录后弹窗数据源，只返回启用中的公告（默认前 5 条）。
 */
export async function getLatestNotices(
  limit: number = 5,
  options?: { [key: string]: any },
) {
  return request<API.ApiResult<API.SysNoticeDto[]>>(
    `${API_VERSION}/system/notice/latest`,
    {
      method: 'GET',
      params: { limit },
      ...(options || {}),
    },
  );
}

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
