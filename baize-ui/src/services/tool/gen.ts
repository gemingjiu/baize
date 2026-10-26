// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';
import { API_VERSION } from '@/constants/api';

/**
 * 代码生成服务
 *
 * 后端 baize-gen（网关路由 /api/v1/gen/** -> lb://baize-gen）：
 *   GET  /gen/tables            获取数据库表列表（tool:gen:query）
 *   GET  /gen/table/{tableName} 获取表字段信息（tool:gen:query）
 *   POST /gen/generate          生成代码，返回「文件路径 -> 源码」（tool:gen:code）
 *   POST /gen/download          生成代码并下载 zip（tool:gen:code）
 */

/** 数据库表信息 */
export interface GenTableItem {
  tableName?: string;
  tableComment?: string;
  tableType?: string;
}

/** 获取数据库表列表 GET /gen/tables */
export async function getGenTables(options?: { [key: string]: any }) {
  return request<API.ApiResult<GenTableItem[]>>(`${API_VERSION}/gen/tables`, {
    method: 'GET',
    ...(options || {}),
  });
}

/** 获取表字段信息 GET /gen/table/{tableName} */
export async function getGenTableInfo(
  tableName: string,
  options?: { [key: string]: any },
) {
  return request<API.ApiResult<Record<string, any>>>(
    `${API_VERSION}/gen/table/${tableName}`,
    {
      method: 'GET',
      ...(options || {}),
    },
  );
}

/**
 * 生成参数。
 * 最小闭环下只需 tableName；moduleName / businessName / packageName 留空时
 * 由后端回落默认值（system / 类名首字母小写 / com.gem.baize.system）。
 */
export interface GenCodeParams {
  tableName: string;
  tableComment?: string;
  className?: string;
  moduleName?: string;
  businessName?: string;
  functionName?: string;
  author?: string;
  packageName?: string;
}

/** 生成代码 POST /gen/generate，返回「文件路径 -> 源码内容」 */
export async function generateCode(
  data: GenCodeParams,
  options?: { [key: string]: any },
) {
  return request<API.ApiResult<Record<string, string>>>(
    `${API_VERSION}/gen/generate`,
    {
      method: 'POST',
      data,
      ...(options || {}),
    },
  );
}
