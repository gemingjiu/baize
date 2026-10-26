<#-- 生成物是 TypeScript 模板字符串，$ 需要原样输出：这里用变量代替字面 ${} 以免被 FreeMarker 解析 -->
<#assign DOLLAR = "$">
// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';
import { API_VERSION } from '@/constants/api';

/** 分页查询${dto.functionName!dto.tableComment!dto.tableName} POST /${dto.moduleName}/${dto.businessName}/page */
export async function get${dto.className}Page(
  params: {
    current?: number;
    size?: number;
  },
  body: Partial<API.${dto.className}Dto>,
  options?: { [key: string]: any },
) {
  return request<API.ApiResult<API.PageResult<API.${dto.className}Dto>>>(
    `${DOLLAR}{API_VERSION}/${dto.moduleName}/${dto.businessName}/page`,
    {
      method: 'POST',
      params: {
        current: params.current,
        size: params.size,
      },
      data: body,
      ...(options || {}),
    },
  );
}

/** 根据ID获取${dto.functionName!dto.tableComment!dto.tableName} GET /${dto.moduleName}/${dto.businessName}/{id} */
export async function get${dto.className}ById(
  id: string,
  options?: { [key: string]: any },
) {
  return request<API.ApiResult<API.${dto.className}Dto>>(
    `${DOLLAR}{API_VERSION}/${dto.moduleName}/${dto.businessName}/${DOLLAR}{id}`,
    {
      method: 'GET',
      ...(options || {}),
    },
  );
}

/** 创建${dto.functionName!dto.tableComment!dto.tableName} POST /${dto.moduleName}/${dto.businessName} */
export async function create${dto.className}(
  data: Partial<API.${dto.className}Dto>,
  options?: { [key: string]: any },
) {
  return request<API.ApiResult<number>>(
    `${DOLLAR}{API_VERSION}/${dto.moduleName}/${dto.businessName}`,
    {
      method: 'POST',
      data,
      ...(options || {}),
    },
  );
}

/** 更新${dto.functionName!dto.tableComment!dto.tableName} PUT /${dto.moduleName}/${dto.businessName}/{id} */
export async function update${dto.className}(
  id: string,
  data: Partial<API.${dto.className}Dto>,
  options?: { [key: string]: any },
) {
  return request<API.ApiResult>(
    `${DOLLAR}{API_VERSION}/${dto.moduleName}/${dto.businessName}/${DOLLAR}{id}`,
    {
      method: 'PUT',
      data: { ...data, id },
      ...(options || {}),
    },
  );
}

/** 删除${dto.functionName!dto.tableComment!dto.tableName} DELETE /${dto.moduleName}/${dto.businessName}/{id} */
export async function delete${dto.className}(
  id: string,
  options?: { [key: string]: any },
) {
  return request<API.ApiResult>(
    `${DOLLAR}{API_VERSION}/${dto.moduleName}/${dto.businessName}/${DOLLAR}{id}`,
    {
      method: 'DELETE',
      ...(options || {}),
    },
  );
}
