import exampleComponent from './zh-CN/example/component';
import exampleGlobalHeader from './zh-CN/example/globalHeader';
import exampleMenu from './zh-CN/example/menu';
import examplePages from './zh-CN/example/pages';
import examplePwa from './zh-CN/example/pwa';
import exampleSettingDrawer from './zh-CN/example/settingDrawer';
import exampleSettings from './zh-CN/example/settings';
import menu from './zh-CN/menu'

export default {
  'navBar.lang': '语言',
  'layout.user.link.help': '帮助',
  'layout.user.link.privacy': '隐私',
  'layout.user.link.terms': '条款',
  'app.preview.down.block': '下载此页面到本地项目',
  'app.welcome.link.fetch-blocks': '获取全部区块',
  'app.welcome.link.block-list': '基于 block 开发，快速构建标准页面',
  ...examplePages,
  ...exampleGlobalHeader,
  ...exampleMenu,
  ...exampleSettingDrawer,
  ...exampleSettings,
  ...examplePwa,
  ...exampleComponent,
  ...menu,
};
