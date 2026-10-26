// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';
import { message } from 'antd';
import { API_VERSION } from '@/constants/api';
import { downloadExportFile } from '@/utils/exportExcel';

/** 分页查询登录日志 POST /system/loginLog/page */
export async function getLoginLogPage(
  params: {
    current?: number;
    size?: number;
  },
  body: Partial<API.SysLoginLogDto>,
  options?: { [key: string]: any },
) {
  return request<API.ApiResult<API.PageResult<API.SysLoginLogDto>>>(`${API_VERSION}/system/loginLog/page`, {
    method: 'POST',
    params: {
      current: params.current,
      size: params.size,
    },
    data: body,
    ...(options || {}),
  });
}

/** 删除登录日志 DELETE /system/loginLog/{id} */
export async function deleteLoginLog(id: string, options?: { [key: string]: any }) {
  return request<API.ApiResult>(`${API_VERSION}/system/loginLog/${id}`, {
    method: 'DELETE',
    ...(options || {}),
  });
}

/** 清空登录日志 DELETE /system/loginLog/clean */
export async function cleanLoginLog(options?: { [key: string]: any }) {
  return request<API.ApiResult>(`${API_VERSION}/system/loginLog/clean`, {
    method: 'DELETE',
    ...(options || {}),
  });
}

/**
 * 导出登录日志 POST /system/loginLog/export
 *
 * 注意：后端 @RequestMapping 是 /system/loginLog（大写 L），
 * 与前端目录名 loginlog 不同，这里以服务文件既有写法为准。
 *
 * 响应为 xlsx 二进制流，不能用 request()（会被当成 JSON 解析），
 * 这里走原生 fetch + Blob 下载，并带上登录态 Authorization 头。
 * 传空对象 {} 表示按当前条件为空的全量导出。
 * 成功弹「导出成功」，失败（非 2xx / 网络异常）弹「导出失败」，不静默。
 */
export async function exportLoginLogs(
  params: Partial<API.SysLoginLogDto>,
): Promise<void> {
  const ok = await downloadExportFile<API.SysLoginLogDto>(
    `${API_VERSION}/system/loginLog/export`,
    params,
    '登录日志.xlsx',
  );
  if (ok) {
    message.success('导出成功');
  } else {
    message.error('导出失败');
  }
}
