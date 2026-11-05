/**
 * @name umi 的路由配置
 * @description 只支持 path,component,routes,redirect,wrappers,name,icon 的配置
 * @param path  path 只支持两种占位符配置，第一种是动态参数 :id 的形式，第二种是 * 通配符，通配符只能出现路由字符串的最后。
 * @param component 配置 location 和 path 匹配后用于渲染的 React 组件路径。可以是绝对路径，也可以是相对路径，如果是相对路径，会从 src/pages 开始找起。
 * @param routes 配置子路由，通常在需要为多个路径增加 layout 组件时使用。
 * @param redirect 配置路由跳转
 * @param wrappers 配置路由组件的包装组件，通过包装组件可以为当前的路由组件组合进更多的功能。 比如，可以用于路由级别的权限校验
 * @param name 配置路由的标题，默认读取国际化文件 menu.ts 中 menu.xxxx 的值，如配置 name 为 login，则读取 menu.ts 中 menu.login 的取值作为标题
 * @param icon 配置路由的图标，取值参考 https://ant.design/components/icon-cn， 注意去除风格后缀和大小写，如想要配置图标为 <StepBackwardOutlined /> 则取值应为 stepBackward 或 StepBackward，如想要配置图标为 <UserOutlined /> 则取值应为 user 或者 User
 * @doc https://umijs.org/docs/guides/routes
 */
export default [
  {
    path: '/',
    redirect: '/welcome',
  },
  {
    // 仪表盘首页。菜单项不在后端 sys_menu 里，由 src/app.tsx 的 HOME_MENU 补在导航最前面
    path: '/welcome',
    name: 'welcome',
    icon: 'dashboard',
    component: './Welcome',
    wrappers: ['@/wrappers/authGuard'],
  },
  {
    component: '404',
    path: '/*',
  },
  {
    path: '/system/login',
    layout: false,
    name: 'login',
    component: './system/login',
  },
  {
    path: '/system',
    name: 'system',
    icon: 'setting',
    routes: [
      {
        path: '/system',
        redirect: '/system/user',
      },
      {
        name: 'user',
        icon: 'user',
        path: '/system/user',
        component: './system/user',
        wrappers: ['@/wrappers/authGuard'],
      },
      {
        name: 'role',
        icon: 'team',
        path: '/system/role',
        component: './system/role',
        wrappers: ['@/wrappers/authGuard'],
      },
      {
        name: 'menu',
        icon: 'menu',
        path: '/system/menu',
        component: './system/menu',
        wrappers: ['@/wrappers/authGuard'],
      },
      {
        name: 'dept',
        icon: 'cluster',
        path: '/system/dept',
        component: './system/dept',
        wrappers: ['@/wrappers/authGuard'],
      },
      {
        name: 'tenant',
        icon: 'apartment',
        path: '/system/tenant',
        component: './system/tenant',
        wrappers: ['@/wrappers/authGuard'],
      },
      {
        name: 'post',
        icon: 'idcard',
        path: '/system/post',
        component: './system/post',
        wrappers: ['@/wrappers/authGuard'],
      },
      {
        name: 'dict',
        icon: 'book',
        path: '/system/dict',
        component: './system/dict',
        wrappers: ['@/wrappers/authGuard'],
      },
      {
        name: 'config',
        icon: 'setting',
        path: '/system/config',
        component: './system/config',
        wrappers: ['@/wrappers/authGuard'],
      },
      {
        name: 'perm',
        icon: 'safety',
        path: '/system/perm',
        component: './system/perm',
        wrappers: ['@/wrappers/authGuard'],
      },
      // 以下三个为历史路径，菜单已改用 /monitor/** 子树（见文件末尾 /monitor 组）。
      // 保留为 redirect 而非删除，避免用户收藏/直接输入的旧链接变成 404。
      // 它们不再需要 component 与 authGuard（重定向本身不渲染页面）。
      {
        path: '/system/operlog',
        redirect: '/monitor/operlog',
      },
      {
        path: '/system/loginlog',
        redirect: '/monitor/logininfor',
      },
      {
        name: 'job',
        icon: 'schedule',
        path: '/system/job',
        component: './system/job',
        wrappers: ['@/wrappers/authGuard'],
      },
      {
        name: 'notice',
        icon: 'notification',
        path: '/system/notice',
        component: './system/notice',
        wrappers: ['@/wrappers/authGuard'],
      },
      // 历史路径，菜单已改用 /monitor/server，保留 redirect 避免旧链接 404
      {
        path: '/system/monitor',
        redirect: '/monitor/server',
      },
    ],
  },
  {
    path: '/monitor',
    name: 'monitor',
    icon: 'monitor',
    routes: [
      {
        path: '/monitor',
        redirect: '/monitor/operlog',
      },
      {
        name: 'operlog',
        icon: 'file-text',
        path: '/monitor/operlog',
        component: './system/operlog',
        wrappers: ['@/wrappers/authGuard'],
      },
      {
        name: 'logininfor',
        icon: 'login',
        path: '/monitor/logininfor',
        component: './system/loginlog',
        wrappers: ['@/wrappers/authGuard'],
      },
      {
        name: 'online',
        icon: 'user',
        path: '/monitor/online',
        component: './system/monitor/online',
        wrappers: ['@/wrappers/authGuard'],
      },
      {
        name: 'server',
        icon: 'cloud-server',
        path: '/monitor/server',
        component: './system/monitor',
        wrappers: ['@/wrappers/authGuard'],
      },
      {
        name: 'cache',
        icon: 'database',
        path: '/monitor/cache',
        component: './system/monitor/cache',
        wrappers: ['@/wrappers/authGuard'],
      },
    ],
  },
  {
    path: '/tool',
    name: 'tool',
    icon: 'tool',
    routes: [
      {
        path: '/tool',
        redirect: '/tool/gen',
      },
      {
        name: 'gen',
        icon: 'code',
        path: '/tool/gen',
        component: './tool/gen',
        wrappers: ['@/wrappers/authGuard'],
      },
      {
        name: 'swagger',
        icon: 'api',
        path: '/tool/swagger',
        component: './tool/swagger',
        wrappers: ['@/wrappers/authGuard'],
      },
    ],
  },
];
