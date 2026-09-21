import exampleComponent from './en-US/example/component';
import exampleGlobalHeader from './en-US/example/globalHeader';
import exampleMenu from './en-US/example/menu';
import examplePages from './en-US/example/pages';
import examplePwa from './en-US/example/pwa';
import exampleSettingDrawer from './en-US/example/settingDrawer';
import exampleSettings from './en-US/example/settings';

export default {
  'navBar.lang': 'Languages',
  'layout.user.link.help': 'Help',
  'layout.user.link.privacy': 'Privacy',
  'layout.user.link.terms': 'Terms',
  'app.preview.down.block': 'Download this page to your local project',
  'app.welcome.link.fetch-blocks': 'Get all block',
  'app.welcome.link.block-list':
    'Quickly build standard, pages based on `block` development',
  ...examplePages,
  ...exampleGlobalHeader,
  ...exampleMenu,
  ...exampleSettingDrawer,
  ...exampleSettings,
  ...examplePwa,
  ...exampleComponent,
};
