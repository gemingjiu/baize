import { layoutSettings } from './theme';

/**
 * 全局布局/主题默认值。
 *
 * 配色与导航骨架定义在 `config/theme.ts`（极简灰白：白底侧栏 + 无阴影 + 浅分隔线），
 * 本文件只负责把「标题、Logo、PWA」这类与皮肤无关的固定项拼上去。
 */
const Settings = {
  ...layoutSettings,
  title: '白泽微服务框架',
  pwa: true,
  logo: '/logo.svg',
  iconfontUrl: '',
};

export default Settings;
