/**
 * 菜单语言包。
 *
 * 侧边栏菜单项通常来自后端 sys_menu.menu_name（见 src/utils/menu.ts 的 transformMenu，
 * menuName 直接作为 MenuDataItem.name），不需要这里的翻译。
 *
 * 但这张表仍然必须有：ProLayout 会对 umi 路由上的 `name` 做 `menu.{父级}.{子级}`
 * 形式的 i18n 查找（面包屑、标题、以及菜单数据尚未就绪时回退到路由生成的菜单）。
 * 缺键时 react-intl 会打印 "[React Intl] Missing message: menu.system.user"，
 * 并把键名或裸路由名（user / system / monitor ...）直接渲染到界面上。
 *
 * 键名格式：`menu.` + routes.ts 中父级 name + 子级 name；一级路由只有一段。
 */
export default {
  'menu.login': '登录',
  'menu.system': '系统管理',
  'menu.system.user': '用户管理',
  'menu.system.role': '角色管理',
  'menu.system.menu': '菜单管理',
  'menu.system.dept': '部门管理',
  'menu.system.tenant': '租户管理',
  'menu.system.post': '岗位管理',
  'menu.system.dict': '字典管理',
  'menu.system.config': '参数设置',
  'menu.system.perm': '权限管理',
  'menu.system.job': '定时任务',
  'menu.system.notice': '通知公告',
  'menu.monitor': '系统监控',
  'menu.monitor.operlog': '操作日志',
  'menu.monitor.logininfor': '登录日志',
  'menu.monitor.online': '在线用户',
  'menu.monitor.server': '服务监控',
  'menu.monitor.cache': '缓存监控',
  'menu.tool': '系统工具',
  'menu.tool.build': '表单构建',
  'menu.tool.gen': '代码生成',
  'menu.tool.swagger': '系统接口',
  'menu.welcome': '首页',
};
