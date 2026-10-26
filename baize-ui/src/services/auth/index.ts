// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';
import { API_VERSION } from '@/constants/api';

/** 切换租户请求体 */
export type SwitchTenantParams = {
  /** 目标租户ID（数字字符串，平台租户为 '0'） */
  tenantId: string;
};

/**
 * 切换租户 POST /auth/switchTenant
 *
 * 用当前（仍有效的）访问令牌换取目标租户下的新令牌，服务端会把旧令牌加入黑名单使其立即失效。
 * 请求体只带目标租户 ID，身份完全由 Authorization 头里的令牌决定 —— 前端无法伪造。
 */
export async function switchTenant(
  data: SwitchTenantParams,
  options?: { [key: string]: any },
) {
  return request<API.ApiResult<API.LoginVO>>(`${API_VERSION}/auth/switchTenant`, {
    method: 'POST',
    data,
    ...(options || {}),
  });
}
