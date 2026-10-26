// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';
import { API_VERSION } from '@/constants/api';

/** 获取定时任务列表 GET /system/job/list（只返回当前页记录，无 total，保留向后兼容） */
export async function getJobList(
  params: {
    status?: string;
    pageNum?: number;
    pageSize?: number;
  },
  options?: { [key: string]: any },
) {
  return request<API.ApiResult<API.SysJobDto[]>>(`${API_VERSION}/system/job/list`, {
    method: 'GET',
    params,
    ...(options || {}),
  });
}

/**
 * 分页查询定时任务 POST /system/job/page
 * 返回 PageResult（含 total/size/current），翻页条依赖 total 才能算对总页数。
 */
export async function getJobPage(
  params: {
    current?: number;
    size?: number;
  },
  body?: Partial<API.SysJobDto>,
  options?: { [key: string]: any },
) {
  return request<API.ApiResult<API.PageResult<API.SysJobDto>>>(
    `${API_VERSION}/system/job/page`,
    {
      method: 'POST',
      params: {
        current: params.current,
        size: params.size,
      },
      data: body ?? {},
      ...(options || {}),
    },
  );
}

/** 根据ID获取定时任务 GET /system/job/{id} */
export async function getJobById(id: string, options?: { [key: string]: any }) {
  return request<API.ApiResult<API.SysJobDto>>(`${API_VERSION}/system/job/${id}`, {
    method: 'GET',
    ...(options || {}),
  });
}

/** 新增定时任务 POST /system/job */
export async function createJob(data: Partial<API.SysJobDto>, options?: { [key: string]: any }) {
  return request<API.ApiResult>(`${API_VERSION}/system/job`, {
    method: 'POST',
    data,
    ...(options || {}),
  });
}

/** 修改定时任务 PUT /system/job */
export async function updateJob(data: Partial<API.SysJobDto>, options?: { [key: string]: any }) {
  return request<API.ApiResult>(`${API_VERSION}/system/job`, {
    method: 'PUT',
    data,
    ...(options || {}),
  });
}

/** 删除定时任务 DELETE /system/job/{id} */
export async function deleteJob(id: string, options?: { [key: string]: any }) {
  return request<API.ApiResult>(`${API_VERSION}/system/job/${id}`, {
    method: 'DELETE',
    ...(options || {}),
  });
}

/** 立即执行任务 PUT /system/job/run/{id} */
export async function runJob(id: string, options?: { [key: string]: any }) {
  return request<API.ApiResult>(`${API_VERSION}/system/job/run/${id}`, {
    method: 'PUT',
    ...(options || {}),
  });
}

/** 暂停任务 PUT /system/job/pause/{id} */
export async function pauseJob(id: string, options?: { [key: string]: any }) {
  return request<API.ApiResult>(`${API_VERSION}/system/job/pause/${id}`, {
    method: 'PUT',
    ...(options || {}),
  });
}

/** 恢复任务 PUT /system/job/resume/{id} */
export async function resumeJob(id: string, options?: { [key: string]: any }) {
  return request<API.ApiResult>(`${API_VERSION}/system/job/resume/${id}`, {
    method: 'PUT',
    ...(options || {}),
  });
}
