/**
 * Menu messages (English).
 *
 * Same keys as src/locales/zh-CN/menu.ts — ProLayout looks up `menu.{parent}.{child}`
 * for route names (breadcrumb, page title, and the route-derived menu fallback).
 * Missing keys make react-intl log "Missing message: menu.system.user" and render
 * the raw key or route name instead of a label.
 */
export default {
  'menu.login': 'Login',
  'menu.system': 'System',
  'menu.system.user': 'Users',
  'menu.system.role': 'Roles',
  'menu.system.menu': 'Menus',
  'menu.system.dept': 'Departments',
  'menu.system.tenant': 'Tenants',
  'menu.system.post': 'Posts',
  'menu.system.dict': 'Dictionaries',
  'menu.system.config': 'Settings',
  'menu.system.perm': 'Permissions',
  'menu.system.job': 'Scheduled Jobs',
  'menu.system.notice': 'Notices',
  'menu.monitor': 'Monitoring',
  'menu.monitor.operlog': 'Operation Logs',
  'menu.monitor.logininfor': 'Login Logs',
  'menu.monitor.online': 'Online Users',
  'menu.monitor.server': 'Server',
  'menu.monitor.cache': 'Cache',
  'menu.tool': 'Tools',
  'menu.tool.build': 'Form Builder',
  'menu.tool.gen': 'Code Generator',
  'menu.tool.swagger': 'API Docs',
  'menu.welcome': 'Home',
};
