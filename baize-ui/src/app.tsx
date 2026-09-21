import { LinkOutlined } from '@ant-design/icons';
import type {
  Settings as LayoutSettings,
  MenuDataItem,
} from '@ant-design/pro-components';
import { SettingDrawer } from '@ant-design/pro-components';
import type { RequestConfig, RunTimeLayoutConfig } from '@umijs/max';
import { history, Link } from '@umijs/max';
import React from 'react';
import {
  AvatarDropdown,
  AvatarName,
  Footer,
  Question,
  SelectLang,
} from '@/components';
import { currentUser as queryCurrentUser } from '@/services/ant-design-pro/api';
import { getUserMenuTree } from '@/services/system/menu';
import defaultSettings from '../config/defaultSettings';
import { errorConfig } from './requestErrorConfig';
import { getAllButtonPermissions, transformMenu } from './utils/menu';
import '@ant-design/v5-patch-for-react-19';

const isDev = process.env.NODE_ENV === 'development' || process.env.CI;
const loginPath = '/system/login';

// 从localStorage获取用户信息
const getUserFromStorage = (): API.CurrentUser | undefined => {
  const token = localStorage.getItem('accessToken');
  if (!token) {
    return undefined;
  }
  const userStr = localStorage.getItem('userInfo');
  if (userStr) {
    try {
      return JSON.parse(userStr);
    } catch {
      return undefined;
    }
  }
  return undefined;
};

// 从localStorage获取菜单
const getMenuFromStorage = (): MenuDataItem[] | undefined => {
  const menuStr = localStorage.getItem('menuTree');
  if (menuStr) {
    try {
      return JSON.parse(menuStr);
    } catch {
      return undefined;
    }
  }
  return undefined;
};

/**
 * 获取菜单树
 */
const fetchMenuTree = async (): Promise<MenuDataItem[]> => {
  try {
    const user = getUserFromStorage();
    const userId = user?.id?.toString() || '';
    const res = await getUserMenuTree(userId, { skipErrorHandler: true });
    if (res.success && res.data) {
      // 转换菜单格式
      const menuData = transformMenu(res.data);
      // 保存到localStorage
      localStorage.setItem('menuTree', JSON.stringify(menuData));
      // 提取并保存按钮权限
      const permissions = getAllButtonPermissions(res.data);
      localStorage.setItem('permissions', JSON.stringify(permissions));
      return menuData;
    }
  } catch (error) {
    console.error('获取菜单失败', error);
  }
  return [];
};

/**
 * @see https://umijs.org/docs/api/runtime-config#getinitialstate
 * */
export async function getInitialState(): Promise<{
  settings?: Partial<LayoutSettings>;
  currentUser?: API.CurrentUser;
  menuData?: MenuDataItem[];
  loading?: boolean;
  fetchUserInfo?: () => Promise<API.CurrentUser | undefined>;
  fetchMenuTree?: () => Promise<MenuDataItem[]>;
}> {
  const fetchUserInfo = async () => {
    const token = localStorage.getItem('accessToken');
    if (!token) {
      history.push(loginPath);
      return undefined;
    }
    try {
      const res = await queryCurrentUser({
        skipErrorHandler: true,
      });
      // 保存用户信息到localStorage
      localStorage.setItem('userInfo', JSON.stringify(res.data));
      return res.data;
    } catch (_error) {
      history.push(loginPath);
    }
    return undefined;
  };

  // 如果不是登录页面，执行
  const { location } = history;
  if (![loginPath].includes(location.pathname)) {
    // 先从localStorage获取用户信息，避免每次刷新都请求
    const token = localStorage.getItem('accessToken');
    const storedUser = getUserFromStorage();
    const storedMenu = getMenuFromStorage();

    if (storedUser && token) {
      return {
        fetchUserInfo,
        fetchMenuTree,
        currentUser: storedUser,
        menuData: storedMenu || [],
        settings: defaultSettings as Partial<LayoutSettings>,
      };
    }

    // 没有缓存的用户信息，重新请求
    // 先验证token有效性
    const currentUser = await fetchUserInfo();
    if (!currentUser) {
      return {
        fetchUserInfo,
        fetchMenuTree,
        settings: defaultSettings as Partial<LayoutSettings>,
      };
    }

    // 获取菜单
    const menuData = await fetchMenuTree();

    return {
      fetchUserInfo,
      fetchMenuTree,
      currentUser,
      menuData,
      settings: defaultSettings as Partial<LayoutSettings>,
    };
  }
  return {
    fetchUserInfo,
    fetchMenuTree,
    settings: defaultSettings as Partial<LayoutSettings>,
  };
}

// ProLayout 支持的api https://procomponents.ant.design/components/layout
export const layout: RunTimeLayoutConfig = ({
  initialState,
  setInitialState,
}) => {
  return {
    actionsRender: () => [
      <Question key="doc" />,
      <SelectLang key="SelectLang" />,
    ],
    avatarProps: {
      src: initialState?.currentUser?.avatar,
      title: <AvatarName />,
      render: (_, avatarChildren) => {
        return <AvatarDropdown>{avatarChildren}</AvatarDropdown>;
      },
    },
    waterMarkProps: {
      content: initialState?.currentUser?.username,
    },
    footerRender: () => <Footer />,
    onPageChange: () => {
      const { location } = history;
      if (!initialState?.currentUser && location.pathname !== loginPath) {
        history.push(loginPath);
      }
    },
    bgLayoutImgList: [
      {
        src: 'https://mdn.alipayobjects.com/yuyan_qk0oxh/afts/img/D2LWSqNny4sAAAAAAAAAAAAAFl94AQBr',
        left: 85,
        bottom: 100,
        height: '303px',
      },
      {
        src: 'https://mdn.alipayobjects.com/yuyan_qk0oxh/afts/img/C2TWRpJpiC0AAAAAAAAAAAAAFl94AQBr',
        bottom: -68,
        right: -45,
        height: '303px',
      },
      {
        src: 'https://mdn.alipayobjects.com/yuyan_qk0oxh/afts/img/F6vSTbj8KpYAAAAAAAAAAAAAFl94AQBr',
        bottom: 0,
        left: 0,
        width: '331px',
      },
    ],
    links: [],
    menuHeaderRender: undefined,
    menu: {
      request: async () => {
        return initialState?.menuData || [];
      },
      type: 'group',
    },
    childrenRender: (children) => {
      if (!initialState?.currentUser && history.location.pathname !== loginPath) {
        return <div style={{ height: '100vh' }} />;
      }
      return (
        <>
          {children}
          {isDev && (
            <SettingDrawer
              disableUrlParams
              enableDarkTheme
              settings={initialState?.settings}
              onSettingChange={(settings) => {
                setInitialState((preInitialState) => ({
                  ...preInitialState,
                  settings,
                }));
              }}
            />
          )}
        </>
      );
    },
    ...initialState?.settings,
  };
};

/**
 * @name request 配置，可以配置错误处理
 * 它基于 axios 和 ahooks 的 useRequest 提供了一套统一的网络请求和错误处理方案。
 * @doc https://umijs.org/docs/max/request#配置
 */
export const request: RequestConfig = {
  // 跨域请求是否携带cookie
  withCredentials: false,
  // 请求拦截器：添加 Authorization header
  requestInterceptors: [
    (config: any) => {
      const token = localStorage.getItem('accessToken');
      if (token) {
        config.headers = {
          ...config.headers,
          Authorization: `Bearer ${token}`,
        };
      }
      return config;
    },
  ],
  // 响应拦截器：处理 401 未授权
  responseInterceptors: [
    (response: any) => {
      return response;
    },
    async (error: any) => {
      if (error.response?.status === 401) {
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        localStorage.removeItem('userInfo');
        localStorage.removeItem('menuTree');
        localStorage.removeItem('permissions');
        history.push(loginPath);
      }
      return Promise.reject(error);
    },
  ],
  ...errorConfig,
};
