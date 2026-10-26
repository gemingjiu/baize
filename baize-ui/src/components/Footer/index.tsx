import { GithubOutlined } from '@ant-design/icons';
import { DefaultFooter } from '@ant-design/pro-components';
import React from 'react';
import pkg from '../../../package.json';

/**
 * 页脚。
 *
 * 原来挂的是 antd pro 模板的假链接（baize.com / ant-design-pro 仓库），
 * 现在改为：真实版本号 + 本项目仓库地址 + 构建年份。
 */
const REPO_URL = 'https://github.com/gemingjiu/baize';

const Footer: React.FC = () => {
  const year = new Date().getFullYear();
  return (
    <DefaultFooter
      style={{ background: 'none', paddingBlockStart: 8 }}
      copyright={`白泽微服务框架 v${pkg.version} · ${year} · 基于 React 19 + UmiJS Max + Ant Design 5`}
      links={[
        {
          key: 'repo',
          title: <GithubOutlined />,
          href: REPO_URL,
          blankTarget: true,
        },
        {
          key: 'antd',
          title: 'Ant Design',
          href: 'https://ant.design',
          blankTarget: true,
        },
        {
          key: 'umi',
          title: 'UmiJS',
          href: 'https://umijs.org',
          blankTarget: true,
        },
      ]}
    />
  );
};

export default Footer;
