// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';
import { API_VERSION } from '@/constants/api';

/** 分页查询字典类型 POST /system/dict/type/page */
export async function getDictTypePage(
  params: {
    current?: number;
    size?: number;
  },
  body: Partial<API.SysDictTypeDto>,
  options?: { [key: string]: any },
) {
  return request<API.ApiResult<API.PageResult<API.SysDictTypeDto>>>(`${API_VERSION}/system/dict/type/page`, {
    method: 'POST',
    params: {
      current: params.current,
      size: params.size,
    },
    data: body,
    ...(options || {}),
  });
}

/** 根据ID获取字典类型 GET /system/dict/type/{id} */
export async function getDictTypeById(id: string, options?: { [key: string]: any }) {
  return request<API.ApiResult<API.SysDictTypeDto>>(`${API_VERSION}/system/dict/type/${id}`, {
    method: 'GET',
    ...(options || {}),
  });
}

/** 创建字典类型 POST /system/dict/type */
export async function createDictType(data: Partial<API.SysDictTypeDto>, options?: { [key: string]: any }) {
  return request<API.ApiResult<number>>(`${API_VERSION}/system/dict/type`, {
    method: 'POST',
    data,
    ...(options || {}),
  });
}

/** 更新字典类型 PUT /system/dict/type/{id} */
export async function updateDictType(id: string, data: Partial<API.SysDictTypeDto>, options?: { [key: string]: any }) {
  return request<API.ApiResult>(`${API_VERSION}/system/dict/type/${id}`, {
    method: 'PUT',
    data: { ...data, id },
    ...(options || {}),
  });
}

/** 删除字典类型 DELETE /system/dict/type/{id} */
export async function deleteDictType(id: string, options?: { [key: string]: any }) {
  return request<API.ApiResult>(`${API_VERSION}/system/dict/type/${id}`, {
    method: 'DELETE',
    ...(options || {}),
  });
}

/** 分页查询字典数据 POST /system/dict/data/page */
export async function getDictDataPage(
  params: {
    current?: number;
    size?: number;
  },
  body: Partial<API.SysDictDataDto>,
  options?: { [key: string]: any },
) {
  return request<API.ApiResult<API.PageResult<API.SysDictDataDto>>>(`${API_VERSION}/system/dict/data/page`, {
    method: 'POST',
    params: {
      current: params.current,
      size: params.size,
    },
    data: body,
    ...(options || {}),
  });
}

/** 根据ID获取字典数据 GET /system/dict/data/{id} */
export async function getDictDataById(id: string, options?: { [key: string]: any }) {
  return request<API.ApiResult<API.SysDictDataDto>>(`${API_VERSION}/system/dict/data/${id}`, {
    method: 'GET',
    ...(options || {}),
  });
}

/** 创建字典数据 POST /system/dict/data */
export async function createDictData(data: Partial<API.SysDictDataDto>, options?: { [key: string]: any }) {
  return request<API.ApiResult<number>>(`${API_VERSION}/system/dict/data`, {
    method: 'POST',
    data,
    ...(options || {}),
  });
}

/** 更新字典数据 PUT /system/dict/data/{id} */
export async function updateDictData(id: string, data: Partial<API.SysDictDataDto>, options?: { [key: string]: any }) {
  return request<API.ApiResult>(`${API_VERSION}/system/dict/data/${id}`, {
    method: 'PUT',
    data: { ...data, id },
    ...(options || {}),
  });
}

/** 删除字典数据 DELETE /system/dict/data/{id} */
export async function deleteDictData(id: string, options?: { [key: string]: any }) {
  return request<API.ApiResult>(`${API_VERSION}/system/dict/data/${id}`, {
    method: 'DELETE',
    ...(options || {}),
  });
}

/** 根据字典类型获取字典数据列表 GET /system/dict/data/type/{dictType} */
export async function getDictDataByType(dictType: string, options?: { [key: string]: any }) {
  return request<API.ApiResult<API.SysDictDataDto[]>>(`${API_VERSION}/system/dict/data/type/${dictType}`, {
    method: 'GET',
    ...(options || {}),
  });
}

/**
 * 批量获取当前租户可见的全部启用字典 GET /system/dict/data/all
 *
 * 返回 Map<dictType, SysDictDataDto[]>（合并解析：租户自定义项 → 平台内置项兜底）。
 * 登录即可见（无 DICT_QUERY 权限门槛），前端登录后调用一次并全局缓存（见 @/hooks/useDict）。
 */
export async function getAllDictData(options?: { [key: string]: any }) {
  return request<API.ApiResult<Record<string, API.SysDictDataDto[]>>>(`${API_VERSION}/system/dict/data/all`, {
    method: 'GET',
    ...(options || {}),
  });
}
