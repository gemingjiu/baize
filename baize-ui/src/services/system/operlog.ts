// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** 分页查询操作日志 POST /system/operlog/page */
export async function getOperLogPage(
  params: {
    current?: number;
    size?: number;
  },
  body: Partial<API.SysOperLogDto>,
  options?: { [key: string]: any },
) {
  return request<API.ApiResult<API.MybatisPage<API.SysOperLogDto>>>('/system/operlog/page', {
    method: 'POST',
    params: {
      current: params.current,
      size: params.size,
    },
    data: body,
    ...(options || {}),
  });
}

/** 根据ID获取操作日志详情 GET /system/operlog/{id} */
export async function getOperLogById(id: string, options?: { [key: string]: any }) {
  return request<API.ApiResult<API.SysOperLogDto>>(`/system/operlog/${id}`, {
    method: 'GET',
    ...(options || {}),
  });
}

/** 删除操作日志 DELETE /system/operlog/{id} */
export async function deleteOperLog(id: string, options?: { [key: string]: any }) {
  return request<API.ApiResult>(`/system/operlog/${id}`, {
    method: 'DELETE',
    ...(options || {}),
  });
}

/** 清空操作日志 DELETE /system/operlog/clean */
export async function cleanOperLog(options?: { [key: string]: any }) {
  return request<API.ApiResult>('/system/operlog/clean', {
    method: 'DELETE',
    ...(options || {}),
  });
}
