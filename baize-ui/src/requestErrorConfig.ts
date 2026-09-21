import type { RequestOptions } from '@@/plugin-request/request';
import type { RequestConfig } from '@umijs/max';
import { history } from '@umijs/max';
import { message } from 'antd';

interface ResponseStructure {
  success: boolean;
  errorCode: number;
  errorMessage: string;
  data: any;
  showType: number;
  traceId: string;
  timeStamp: number;
}

const loginPath = '/system/login';

const getToken = () => {
  return localStorage.getItem('accessToken');
};

const clearAuth = () => {
  localStorage.removeItem('accessToken');
  localStorage.removeItem('refreshToken');
  localStorage.removeItem('userInfo');
  localStorage.removeItem('menuTree');
  localStorage.removeItem('permissions');
};

export const errorConfig: RequestConfig = {
  errorConfig: {
    errorThrower: (res) => {
      const { success, errorCode, errorMessage, data } =
        res as unknown as ResponseStructure;
      if (!success) {
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
      return response;
    },
  ],
};
