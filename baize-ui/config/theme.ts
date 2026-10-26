import type { ProLayoutProps } from '@ant-design/pro-components';
import type { ThemeConfig } from 'antd';

/**
 * 全局视觉规范（单一事实来源）。
 *
 * 风格：**极简灰白**——白底侧栏常驻、无阴影、无粗描边，靠留白、字号与极浅的分隔线区分层级。
 *
 * 两个出口都从这里取色，页面里不要再写死 hex：
 * - `antdTheme` → 交给 umi 的 `antd.configProvider.theme`（组件色板 / 圆角 / 阴影）
 * - `layoutSettings` → 交给 ProLayout（导航骨架 / 侧栏与头部配色）
 *
 * 换主色只改 `palette.colorPrimary`，换导航骨架改 `layoutSettings.layout`。
 *
 * 两个踩过的坑，改之前先看：
 * 1. **不要用 ProLayout 的 `navTheme: 'realDark'` 表达「深色侧栏」**。它会让 ProConfigProvider
 *    给整个 layout 子树注入 antd 的 `darkAlgorithm`：内容区 `--ant-color-bg-container` 变成
 *    #141414、表格底色变黑，而显式写死的浅色表头不变，页面直接花掉；主色也会被算法改写
 *    （#4d7cff → #446cdc）。要深色侧栏只能保持 `navTheme: 'light'` + `menuProps: { theme: 'dark' }`。
 * 2. **`layout: 'side'` 在桌面端没有顶栏**（ProLayout 的 Header 对 side 布局直接 return null），
 *    头像/语言切换会自动挪到侧栏底部。想在顶部放东西必须用 `layout: 'mix'`。
 */

/**
 * 调色板。所有颜色语义化命名，改这里即可整体换色。
 * 中性色刻意贴近 zinc 色系（而不是 antd 默认的灰蓝），观感更「素」。
 */
const palette = {
  /** 主色：geekblue 600，比 antd v4 的 #1890ff 更沉稳 */
  colorPrimary: '#2f54eb',
  /** 侧栏 / 头部 / 卡片底色 */
  surface: '#ffffff',
  /** 页面画布：比卡片略深，靠层次而不是描边分隔 */
  canvas: '#fafafa',
  /** 表格表头：极简风格不用底色，只靠字重与极浅分隔线区分 */
  tableHeaderBg: 'transparent',
  tableHeaderColor: '#71717a',
  tableBorder: '#f0f0f2',
  textHeading: '#18181b',
  textMuted: '#71717a',
  siderText: '#52525b',
  siderSelectedText: '#18181b',
  siderSelectedBg: '#f4f4f5',
  siderHoverBg: '#fafafa',
} as const;

const fontFamily =
  '-apple-system, BlinkMacSystemFont, "Segoe UI", "PingFang SC", "HarmonyOS Sans SC", "Noto Sans SC", "Microsoft YaHei", sans-serif';

/** 控件圆角 */
const radius = 10;
/** 卡片圆角 */
const cardRadius = 12;

/** 交给 umi antd 插件的 configProvider.theme */
export const antdTheme: ThemeConfig = {
  cssVar: true,
  token: {
    colorPrimary: palette.colorPrimary,
    colorInfo: palette.colorPrimary,
    colorLink: palette.colorPrimary,
    colorBgLayout: palette.canvas,
    colorTextHeading: palette.textHeading,
    borderRadius: radius,
    fontSize: 14,
    fontFamily,
    // 极简风格：全局去掉阴影，层次只由背景与分隔线承担
    boxShadow: 'none',
    boxShadowSecondary: 'none',
  },
  components: {
    Layout: {
      headerBg: palette.surface,
      siderBg: palette.surface,
      bodyBg: palette.canvas,
      headerHeight: 56,
    },
    Menu: {
      itemSelectedBg: palette.siderSelectedBg,
      itemSelectedColor: palette.siderSelectedText,
      itemHoverBg: palette.siderHoverBg,
      itemBorderRadius: radius,
      itemMarginInline: 8,
      itemMarginBlock: 2,
      itemHeight: 40,
      subMenuItemBg: 'transparent',
    },
    Table: {
      headerBg: palette.tableHeaderBg,
      headerColor: palette.tableHeaderColor,
      headerSplitColor: 'transparent',
      borderColor: palette.tableBorder,
      rowHoverBg: '#f7f9fc',
      cellPaddingBlock: 12,
    },
    Card: {
      borderRadiusLG: cardRadius,
      paddingLG: 20,
    },
    Button: {
      controlHeight: 34,
      primaryShadow: 'none',
      defaultShadow: 'none',
    },
    Input: { controlHeight: 34 },
    Select: { controlHeight: 34 },
    Breadcrumb: { fontSize: 13 },
  },
};

/** 交给 ProLayout（`config/defaultSettings.ts` 与 `initialState.settings` 共用） */
export const layoutSettings: ProLayoutProps = {
  // 恒为 light，理由见文件头「坑 1」
  navTheme: 'light',
  colorPrimary: palette.colorPrimary,
  layout: 'side',
  splitMenus: false,
  contentWidth: 'Fluid',
  fixedHeader: true,
  fixSiderbar: true,
  colorWeak: false,
  siderWidth: 216,
  token: {
    header: {
      colorBgHeader: palette.surface,
      colorHeaderTitle: palette.textHeading,
      heightLayoutHeader: 56,
    },
    sider: {
      colorMenuBackground: palette.surface,
      colorTextMenu: palette.siderText,
      colorTextMenuSelected: palette.siderSelectedText,
      colorTextMenuActive: palette.siderSelectedText,
      colorTextMenuTitle: palette.textHeading,
      colorBgMenuItemSelected: palette.siderSelectedBg,
      colorBgMenuItemHover: palette.siderHoverBg,
    },
    pageContainer: {
      paddingInlinePageContainerContent: 24,
      paddingBlockPageContainerContent: 20,
    },
  },
};
