// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';
import { message } from 'antd';
import { API_VERSION } from '@/constants/api';
import { downloadExportFile } from '@/utils/exportExcel';

/** 分页查询操作日志 POST /system/operlog/page */
export async function getOperLogPage(
  params: {
    current?: number;
    size?: number;
  },
  body: Partial<API.SysOperLogDto>,
  options?: { [key: string]: any },
) {
  return request<API.ApiResult<API.PageResult<API.SysOperLogDto>>>(`${API_VERSION}/system/operlog/page`, {
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
  return request<API.ApiResult<API.SysOperLogDto>>(`${API_VERSION}/system/operlog/${id}`, {
    method: 'GET',
    ...(options || {}),
  });
}

/** 删除操作日志 DELETE /system/operlog/{id} */
export async function deleteOperLog(id: string, options?: { [key: string]: any }) {
  return request<API.ApiResult>(`${API_VERSION}/system/operlog/${id}`, {
    method: 'DELETE',
    ...(options || {}),
  });
}

/** 清空操作日志 DELETE /system/operlog/clean */
export async function cleanOperLog(options?: { [key: string]: any }) {
  return request<API.ApiResult>(`${API_VERSION}/system/operlog/clean`, {
    method: 'DELETE',
    ...(options || {}),
  });
}

/**
 * 导出操作日志 POST /system/operlog/export
 *
 * 响应为 xlsx 二进制流，不能用 request()（会被当成 JSON 解析），
 * 这里走原生 fetch + Blob 下载，并带上登录态 Authorization 头。
 * 传空对象 {} 表示按当前条件为空的全量导出。
 * 成功弹「导出成功」，失败（非 2xx / 网络异常）弹「导出失败」，不静默。
 */
export async function exportOperLogs(
  params: Partial<API.SysOperLogDto>,
): Promise<void> {
  const ok = await downloadExportFile<API.SysOperLogDto>(
    `${API_VERSION}/system/operlog/export`,
    params,
    '操作日志.xlsx',
  );
  if (ok) {
    message.success('导出成功');
  } else {
    message.error('导出失败');
  }
}
