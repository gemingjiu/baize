import {
  ApiOutlined,
  IdcardOutlined,
  MenuOutlined,
  SafetyOutlined,
} from '@ant-design/icons';
import { PageContainer } from '@ant-design/pro-components';
import { useModel } from '@umijs/max';
import { Card, Col, Row, theme } from 'antd';
import React, { useMemo } from 'react';
import { PLATFORM_TENANT_ID } from '@/constants/tenant';
import { getPermissions } from '@/utils/auth';

/** 递归统计菜单树节点数（父节点 + 子节点） */
const countMenuNodes = (menus: any[] = []): number =>
  menus.reduce(
    (sum, item) => sum + 1 + countMenuNodes(item?.children ?? item?.routes ?? []),
    0,
  );

const StatCard: React.FC<{
  title: string;
  value: React.ReactNode;
  icon: React.ReactNode;
  hint?: string;
}> = ({ title, value, icon, hint }) => {
  const { token } = theme.useToken();
  return (
    <Card
      variant="outlined"
      style={{ borderColor: token.colorBorderSecondary }}
      styles={{ body: { padding: '20px 24px' } }}
    >
      <Row align="middle" gutter={16} wrap={false}>
        <Col flex="none">
          <div
            style={{
              width: 44,
              height: 44,
              borderRadius: token.borderRadius,
              background: token.colorFillTertiary,
              color: token.colorPrimary,
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              fontSize: 20,
            }}
          >
            {icon}
          </div>
        </Col>
        <Col flex="auto" style={{ minWidth: 0 }}>
          <div style={{ fontSize: 13, color: token.colorTextTertiary, marginBottom: 2 }}>
            {title}
          </div>
          <div
            style={{
              fontSize: 24,
              fontWeight: 600,
              color: token.colorTextHeading,
              lineHeight: 1.3,
              overflow: 'hidden',
              textOverflow: 'ellipsis',
              whiteSpace: 'nowrap',
            }}
          >
            {value}
          </div>
          {hint && (
            <div style={{ fontSize: 12, color: token.colorTextTertiary, marginTop: 2 }}>
              {hint}
            </div>
          )}
        </Col>
      </Row>
    </Card>
  );
};

const Welcome: React.FC = () => {
  const { token } = theme.useToken();
  const { initialState } = useModel('@@initialState');
  const currentUser = initialState?.currentUser;

  const menuCount = useMemo(
    () => countMenuNodes((initialState?.menuData ?? []) as any[]),
    [initialState?.menuData],
  );
  const permissionCount = useMemo(() => getPermissions().length, []);

  return (
    <PageContainer>
      <div
        style={{
          background: token.colorBgContainer,
          border: `1px solid ${token.colorBorderSecondary}`,
          borderRadius: token.borderRadiusLG,
          padding: '28px 24px',
          marginBottom: 20,
        }}
      >
        <h1
          style={{
            fontSize: 24,
            fontWeight: 600,
            margin: 0,
            color: token.colorTextHeading,
            letterSpacing: '-0.01em',
          }}
        >
          欢迎回来，{currentUser?.nickname || currentUser?.username || '管理员'}
        </h1>
        <p
          style={{
            margin: '8px 0 0 0',
            fontSize: 14,
            color: token.colorTextSecondary,
          }}
        >
          白泽微服务框架 · 后端服务已就绪，可从左侧菜单进入各业务模块。
        </p>
      </div>

      <Row gutter={[16, 16]}>
        <Col xs={24} sm={12} lg={6}>
          <StatCard
            title="可访问菜单"
            value={menuCount}
            icon={<MenuOutlined />}
            hint="按当前账号权限"
          />
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <StatCard
            title="权限码"
            value={permissionCount}
            icon={<SafetyOutlined />}
            hint="用于按钮级控权"
          />
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <StatCard
            title="当前账号"
            value={currentUser?.username || '-'}
            icon={<IdcardOutlined />}
            hint={currentUser?.email || '未绑定邮箱'}
          />
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <StatCard
            title="租户"
            // 后端默认租户是平台租户（nil UUID），直接显示成 UUID 也会刷屏，统一显示文案
            value={
              !currentUser?.tenantId ||
              currentUser.tenantId === PLATFORM_TENANT_ID
                ? '默认租户'
                : currentUser.tenantId
            }
            icon={<ApiOutlined />}
            hint="请求会带上该租户上下文"
          />
        </Col>
      </Row>
    </PageContainer>
  );
};

export default Welcome;
