import type { RequestOptions } from '@@/plugin-request/request';
import type { RequestConfig } from '@umijs/max';
import { history } from '@umijs/max';
import { message } from 'antd';
import { clearAuth, getToken } from '@/utils/auth';

interface ResponseStructure {
  success?: boolean;
  /** 后端统一响应码，200 表示成功 */
  code?: number;
  errorCode: number;
  errorMessage: string;
  data: any;
  showType: number;
  traceId: string;
  timeStamp: number;
}

const loginPath = '/system/login';

/** 兼容两种成功标记：success === true 或 code === 200（部分接口只回 code） */
const isApiSuccess = (body?: ResponseStructure): boolean => {
  if (!body || typeof body !== 'object') {
    return false;
  }
  if (typeof body.success === 'boolean') {
    return body.success;
  }
  return body.code === 200 || body.code === 0;
};

export const errorConfig: RequestConfig = {
  errorConfig: {
    errorThrower: (res) => {
      const body = res as unknown as ResponseStructure;
      const { errorCode, errorMessage, data } = body;
      if (!isApiSuccess(body)) {
        const error: any = new Error(errorMessage);
        error.name = 'BizError';
        error.info = { errorCode, errorMessage, data };
        throw error;
      }
    },
    errorHandler: (error: any, opts: any) => {
      if (opts?.skipErrorHandler) throw error;
      if (error.name === 'BizError') {
        const errorInfo = error.info;
        if (errorInfo) {
          if (errorInfo.errorCode === 401) {
            clearAuth();
            history.push(loginPath);
            message.error('登录已过期，请重新登录');
            return;
          }
          message.error(errorInfo.errorMessage || '请求失败');
        }
      } else if (error.response) {
        const { status } = error.response;
        if (status === 401) {
          clearAuth();
          history.push(loginPath);
          message.error('登录已过期，请重新登录');
          return;
        }
        message.error(`请求失败: ${status}`);
      } else if (error.request) {
        message.error('服务器无响应，请稍后重试');
      } else {
        message.error('请求配置错误');
      }
    },
  },

  requestInterceptors: [
    (config: RequestOptions) => {
      const token = getToken();
      if (token) {
        config.headers = {
          ...config.headers,
          Authorization: `Bearer ${token}`,
        };
      }
      return config;
    },
  ],

  responseInterceptors: [
    (response) => {
      // 统一响应体：{ code: 200, data } 与 { success: true, data } 两种形态都归一化出 success，
      // 避免页面里 `if (res.success)` 在只回 code 的接口上被判为失败。
      const body = response?.data as ResponseStructure | undefined;
      if (
        body &&
        typeof body === 'object' &&
        typeof body.success !== 'boolean' &&
        typeof body.code === 'number'
      ) {
        body.success = body.code === 200 || body.code === 0;
      }
      return response;
    },
  ],
};
