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
    path: '/example',
    name: 'example',
    icon: 'experiment',
    routes: [
      {
        path: '/example/user',
        layout: false,
        routes: [
          {
            path: '/example/user/login',
            layout: false,
            name: 'login',
            component: './example/user/login',
          },
          {
            path: '/example/user',
            redirect: '/example/user/login',
          },
          {
            name: 'register-result',
            icon: 'smile',
            path: '/example/user/register-result',
            component: './example/user/register-result',
          },
          {
            name: 'register',
            icon: 'smile',
            path: '/example/user/register',
            component: './example/user/register',
          },
          {
            component: '404',
            path: '/example/user/*',
          },
        ],
      },
      {
        path: '/example/dashboard',
        name: 'dashboard',
        icon: 'dashboard',
        routes: [
          {
            path: '/example/dashboard',
            redirect: '/example/dashboard/analysis',
          },
          {
            name: 'analysis',
            icon: 'smile',
            path: '/example/dashboard/analysis',
            component: './example/dashboard/analysis',
          },
          {
            name: 'monitor',
            icon: 'smile',
            path: '/example/dashboard/monitor',
            component: './example/dashboard/monitor',
          },
          {
            name: 'workplace',
            icon: 'smile',
            path: '/example/dashboard/workplace',
            component: './example/dashboard/workplace',
          },
        ],
      },
      {
        path: '/example/form',
        icon: 'form',
        name: 'form',
        routes: [
          {
            path: '/example/form',
            redirect: '/example/form/basic-form',
          },
          {
            name: 'basic-form',
            icon: 'smile',
            path: '/example/form/basic-form',
            component: './example/form/basic-form',
          },
          {
            name: 'step-form',
            icon: 'smile',
            path: '/example/form/step-form',
            component: './example/form/step-form',
          },
          {
            name: 'advanced-form',
            icon: 'smile',
            path: '/example/form/advanced-form',
            component: './example/form/advanced-form',
          },
        ],
      },
      {
        path: '/example/list',
        icon: 'table',
        name: 'list',
        routes: [
          {
            path: '/example/list/search',
            name: 'search-list',
            component: './example/list/search',
            routes: [
              {
                path: '/example/list/search',
                redirect: '/example/list/search/articles',
              },
              {
                name: 'articles',
                icon: 'smile',
                path: '/example/list/search/articles',
                component: './example/list/search/articles',
              },
              {
                name: 'projects',
                icon: 'smile',
                path: '/example/list/search/projects',
                component: './example/list/search/projects',
              },
              {
                name: 'applications',
                icon: 'smile',
                path: '/example/list/search/applications',
                component: './example/list/search/applications',
              },
            ],
          },
          {
            path: '/example/list',
            redirect: '/example/list/table-list',
          },
          {
            name: 'table-list',
            icon: 'smile',
            path: '/example/list/table-list',
            component: './example/list/table-list',
          },
          {
            name: 'basic-list',
            icon: 'smile',
            path: '/example/list/basic-list',
            component: './example/list/basic-list',
          },
          {
            name: 'card-list',
            icon: 'smile',
            path: '/example/list/card-list',
            component: './example/list/card-list',
          },
        ],
      },
      {
        path: '/example/profile',
        name: 'profile',
        icon: 'profile',
        routes: [
          {
            path: '/example/profile',
            redirect: '/example/profile/basic',
          },
          {
            name: 'basic',
            icon: 'smile',
            path: '/example/profile/basic',
            component: './example/profile/basic',
          },
          {
            name: 'advanced',
            icon: 'smile',
            path: '/example/profile/advanced',
            component: './example/profile/advanced',
          },
        ],
      },
      {
        name: 'result',
        icon: 'CheckCircleOutlined',
        path: '/example/result',
        routes: [
          {
            path: '/example/result',
            redirect: '/example/result/success',
          },
          {
            name: 'success',
            icon: 'smile',
            path: '/example/result/success',
            component: './example/result/success',
          },
          {
            name: 'fail',
            icon: 'smile',
            path: '/example/result/fail',
            component: './example/result/fail',
          },
        ],
      },
      {
        name: 'exception',
        icon: 'warning',
        path: '/example/exception',
        routes: [
          {
            path: '/example/exception',
            redirect: '/example/exception/403',
          },
          {
            name: '403',
            icon: 'smile',
            path: '/example/exception/403',
            component: './example/exception/403',
          },
          {
            name: '404',
            icon: 'smile',
            path: '/example/exception/404',
            component: './example/exception/404',
          },
          {
            name: '500',
            icon: 'smile',
            path: '/example/exception/500',
            component: './example/exception/500',
          },
        ],
      },
      {
        name: 'account',
        icon: 'user',
        path: '/example/account',
        routes: [
          {
            path: '/example/account',
            redirect: '/example/account/center',
          },
          {
            name: 'center',
            icon: 'smile',
            path: '/example/account/center',
            component: './example/account/center',
          },
          {
            name: 'settings',
            icon: 'smile',
            path: '/example/account/settings',
            component: './example/account/settings',
          },
        ],
      },
    ],
  },
  {
    path: '/',
    redirect: '/system',
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
      },
      {
        name: 'role',
        icon: 'team',
        path: '/system/role',
        component: './system/role',
      },
      {
        name: 'menu',
        icon: 'menu',
        path: '/system/menu',
        component: './system/menu',
      },
      {
        name: 'dept',
        icon: 'cluster',
        path: '/system/dept',
        component: './system/dept',
      },
      {
        name: 'tenant',
        icon: 'apartment',
        path: '/system/tenant',
        component: './system/tenant',
      },
      {
        name: 'post',
        icon: 'idcard',
        path: '/system/post',
        component: './system/post',
      },
      {
        name: 'dict',
        icon: 'book',
        path: '/system/dict',
        component: './system/dict',
      },
      {
        name: 'config',
        icon: 'setting',
        path: '/system/config',
        component: './system/config',
      },
      {
        name: 'perm',
        icon: 'safety',
        path: '/system/perm',
        component: './system/perm',
      },
      {
        name: 'operlog',
        icon: 'file-text',
        path: '/system/operlog',
        component: './system/operlog',
      },
    ],
  },
];
