import {
  ApiOutlined,
  IdcardOutlined,
  MenuOutlined,
  NotificationOutlined,
  SafetyOutlined,
} from '@ant-design/icons';
import { PageContainer } from '@ant-design/pro-components';
import { useModel } from '@umijs/max';
import { Badge, Card, Col, Drawer, List, Modal, Row, Tag, theme } from 'antd';
import React, { useEffect, useMemo, useState } from 'react';
import { getLatestNotices } from '@/services/system/notice';
import { useDict } from '@/hooks/useDict';
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
  const { dictLabel } = useDict();
  const currentUser = initialState?.currentUser;

  // 最新公告（P3 公告展示闭环）：首页公告栏 + 登录后弹窗
  const [notices, setNotices] = useState<API.SysNoticeDto[]>([]);
  const [noticeDetail, setNoticeDetail] = useState<API.SysNoticeDto>();
  const [popupNotice, setPopupNotice] = useState<API.SysNoticeDto>();

  useEffect(() => {
    let mounted = true;
    getLatestNotices(5, { skipErrorHandler: true })
      .then((res) => {
        if (mounted && res?.success && Array.isArray(res.data)) {
          const list = res.data;
          setNotices(list);
          // 登录弹窗：仅弹最新一条，同一公告「我已知晓」后不再弹（localStorage 记忆）
          const latest = list[0];
          if (latest?.id) {
            const dismissed = window.localStorage.getItem(
              `notice_dismissed_${latest.id}`,
            );
            if (!dismissed) {
              setPopupNotice(latest);
            }
          }
        }
      })
      .catch(() => {});
    return () => {
      mounted = false;
    };
  }, []);

  const dismissPopup = () => {
    if (popupNotice?.id) {
      window.localStorage.setItem(`notice_dismissed_${popupNotice.id}`, '1');
    }
    setPopupNotice(undefined);
  };

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

      <Card
        variant="outlined"
        style={{
          marginTop: 16,
          borderColor: token.colorBorderSecondary,
        }}
        styles={{ body: { padding: '12px 20px' } }}
        title={
          <span>
            <NotificationOutlined style={{ marginRight: 8, color: token.colorPrimary }} />
            通知公告
          </span>
        }
        extra={<Badge count={notices.length} overflowCount={99} />}
      >
        <List
          dataSource={notices}
          locale={{ emptyText: '暂无公告' }}
          renderItem={(item) => (
            <List.Item
              style={{ cursor: 'pointer', padding: '10px 0' }}
              onClick={() => setNoticeDetail(item)}
            >
              <List.Item.Meta
                avatar={
                  <Tag color={item.noticeType === '1' ? 'blue' : 'green'}>
                    {dictLabel('sys_notice_type', item.noticeType)}
                  </Tag>
                }
                title={item.noticeTitle}
                description={
                  item.createdTime
                    ? String(item.createdTime).replace('T', ' ').slice(0, 16)
                    : ''
                }
              />
            </List.Item>
          )}
        />
      </Card>

      <Drawer
        title={noticeDetail?.noticeTitle}
        width={520}
        open={!!noticeDetail}
        onClose={() => setNoticeDetail(undefined)}
      >
        <Tag color={noticeDetail?.noticeType === '1' ? 'blue' : 'green'} style={{ marginBottom: 16 }}>
          {dictLabel('sys_notice_type', noticeDetail?.noticeType)}
        </Tag>
        <div style={{ whiteSpace: 'pre-wrap', lineHeight: 1.8 }}>
          {noticeDetail?.noticeContent || '（无内容）'}
        </div>
      </Drawer>

      <Modal
        title={
          <span>
            <NotificationOutlined style={{ marginRight: 8, color: token.colorPrimary }} />
            {popupNotice?.noticeTitle}
          </span>
        }
        open={!!popupNotice}
        onOk={dismissPopup}
        onCancel={dismissPopup}
        okText="我已知晓"
        cancelButtonProps={{ style: { display: 'none' } }}
        width={520}
      >
        <div style={{ whiteSpace: 'pre-wrap', lineHeight: 1.8, marginTop: 8 }}>
          {popupNotice?.noticeContent || '（无内容）'}
        </div>
      </Modal>
    </PageContainer>
  );
};

export default Welcome;
