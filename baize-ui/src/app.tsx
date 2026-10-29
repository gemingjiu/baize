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
  ErrorBoundary,
  Footer,
  Question,
} from '@/components';
import { currentUser as queryCurrentUser } from '@/services/ant-design-pro/api';
import { getUserMenuTree } from '@/services/system/menu';
import TenantSwitcher from '@/components/RightContent/TenantSwitcher';
import defaultSettings from '../config/defaultSettings';
import { errorConfig } from './requestErrorConfig';

import {
  clearAuth,
  getMenuTree,
  getToken,
  getUserInfo,
  isTokenExpired,
  saveMenuTree,
  savePermissions,
  saveUserInfo,
} from './utils/auth';
import { getAllPermissions, renderMenuTree, transformMenu } from './utils/menu';

const isDev = process.env.NODE_ENV === 'development' || process.env.CI;
const loginPath = '/system/login';

/**
 * 获取菜单树
 */
const fetchMenuTree = async (): Promise<MenuDataItem[]> => {
  try {
    // userInfo 仅用于拼接口参数，不是可信身份来源
    const user = getUserInfo();
    const userId = user?.id?.toString() || '';
    const res = await getUserMenuTree(userId, { skipErrorHandler: true });
    if (res.success && res.data) {
      // 转换菜单格式并缓存（展示用）
      const menuData = transformMenu(res.data);
      saveMenuTree(menuData);
      // 提取并保存权限码（目录/菜单/按钮全覆盖，逗号分隔的 perms 会被拆分）
      const permissions = getAllPermissions(res.data);
      savePermissions(permissions);
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
    const token = getToken();
    // 没有 token 或本地已判断过期：清空缓存并回登录页
    if (!token || isTokenExpired(token)) {
      clearAuth();
      history.push(loginPath);
      return undefined;
    }
    try {
      const res = await queryCurrentUser({
        skipErrorHandler: true,
      });
      // 后端返回的当前用户才是可信身份来源，这里仅缓存用于展示
      saveUserInfo(res.data);
      return res.data;
    } catch (_error) {
      clearAuth();
      history.push(loginPath);
    }
    return undefined;
  };

  // 如果不是登录页面，执行
  const { location } = history;
  if (![loginPath].includes(location.pathname)) {
    const token = getToken();

    // 本地只做「token 是否存在、是否明显过期」的粗判断，真正的身份与权限由后端接口决定。
    // 缺失或已过期：清空本地缓存（含 userInfo/menuTree/permissions）并跳转登录页。
    if (!token || isTokenExpired(token)) {
      clearAuth();
      history.push(loginPath);
      return {
        fetchUserInfo,
        fetchMenuTree,
        settings: defaultSettings as Partial<LayoutSettings>,
      };
    }

    // token 本地判断未过期：先用缓存做首屏展示，避免每次刷新都请求接口。
    // 注意 userInfo 只是展示缓存（头像 / 用户名 / 水印），
    // 身份与按钮权限始终以后端返回结果为准（access.ts 只消费后端下发的权限码）。
    const storedUser = getUserInfo();
    const storedMenu = getMenuTree();

    if (storedUser) {
      // 菜单缓存缺失时必须补拉一次：本地有 userInfo 但没有 menuTree 的情况真实存在
      // （旧版本只缓存了 userInfo、用户手动清理过缓存、升级后首次进入等）。
      // 此时如果直接给 ProLayout 一个空数组，侧边栏会整块渲染为空——
      // 用户看到的就是「左边什么都没有」，比缺少图标严重得多。
      const menuData = storedMenu && storedMenu.length > 0 ? storedMenu : await fetchMenuTree();
      return {
        fetchUserInfo,
        fetchMenuTree,
        currentUser: storedUser,
        menuData,
        settings: defaultSettings as Partial<LayoutSettings>,
      };
    }

    // 没有缓存的用户信息，重新请求
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
    // 不渲染语言切换器：项目仅提供中文一种有效语言（见 config.ts 的 locale 说明），
    // 保留入口会让用户切到 "英文框架 + 中文菜单" 的混排状态。
    actionsRender: () => [<TenantSwitcher key="tenant" />, <Question key="doc" />],
    avatarProps: {
      src: initialState?.currentUser?.avatar,
      title: <AvatarName />,
      render: (_, avatarChildren) => {
        return <AvatarDropdown>{avatarChildren}</AvatarDropdown>;
      },
    },
    waterMarkProps: {
      content: initialState?.currentUser?.username,
      // 默认水印在浅色内容上偏重（像一层灰字压着表格），这里调淡到"防截屏"够用即可
      fontColor: 'rgba(0, 0, 0, 0.04)',
      fontSize: 14,
    },
    footerRender: () => <Footer />,
    onPageChange: () => {
      const { location } = history;
      if (!initialState?.currentUser && location.pathname !== loginPath) {
        history.push(loginPath);
      }
    },
    links: [],
    menuHeaderRender: undefined,
    menu: {
      // 菜单图标在后端存的是图标名（user / tree-table ...），
      // 必须在这里换成图标节点，否则会被当作文本渲染进菜单项（表现为中英文混排）。
      // 菜单条目全部来自后端 sys_menu（含「首页」，path=/welcome），
      // 前端不再做任何合并/插入，权限、排序、显隐都以数据库为准。
      request: async () => renderMenuTree(initialState?.menuData || []),
      // 侧边栏布局：菜单分组由 SiderMenu 自己处理，无需顶部 group
      // 菜单文案来自后端 sys_menu.menu_name（已是中文），不需要再做 i18n 查找。
      // 置 false 可避免 ProLayout 拿中文名拼出 `menu.系统管理.用户管理` 这类键，
      // 进而在控制台刷 "Missing message" 告警。
      locale: false,
    },
    // 注意：这里**不能**在 currentUser 缺失时返回空白 div。
    // 历史实现是 `if (!currentUser) return <div style={{height:'100vh'}} />`，
    // 一旦 currentUser 因任何原因（缓存丢失 / 接口抖动 / 首屏竞态）为 falsy，
    // 左侧导航照常渲染（来自 routes 或 menuData），右侧内容区却是空白，
    // 表现为"只有左边菜单、右边什么都没有"，且没有任何错误提示，极难排查。
    // 现在改为始终渲染 children：未登录的跳转由 onPageChange 统一处理。
    childrenRender: (children) => {
      return (
        <>
          <ErrorBoundary>{children}</ErrorBoundary>
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
  // Authorization 注入、401 跳转、响应体 success 归一化统一由 ./requestErrorConfig 提供。
  // 注意：此处不要再单独声明 requestInterceptors / responseInterceptors，
  // 因为展开 errorConfig 时会整体覆盖同名数组，容易造成"看似配置了实则失效"。
  ...errorConfig,
};
