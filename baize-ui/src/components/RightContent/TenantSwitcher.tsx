import { SwapOutlined } from '@ant-design/icons';
import { useModel } from '@umijs/max';
import { App, Modal, Select, Space, Tag } from 'antd';
import { createStyles } from 'antd-style';
import React, { useCallback, useEffect, useMemo, useState } from 'react';
import { PLATFORM_TENANT_ID } from '@/constants/tenant';
import { switchTenant } from '@/services/auth';
import { getTenantList } from '@/services/system/tenant';
import { getUserInfo, saveAuth } from '@/utils/auth';

type TenantOption = {
  value: string;
  label: React.ReactNode;
};

const useStyles = createStyles(({ token }) => ({
  trigger: {
    display: 'flex',
    alignItems: 'center',
    height: '48px',
    padding: '0 8px',
    cursor: 'pointer',
    borderRadius: token.borderRadius,
    '&:hover': {
      backgroundColor: token.colorBgTextHover,
    },
  },
  hint: {
    color: token.colorTextTertiary,
    fontSize: 12,
  },
}));

/**
 * 顶栏租户切换器。
 *
 * 切换 = 用当前令牌调 POST /auth/switchTenant 换取目标租户下的新令牌，
 * 成功后覆盖本地 token / userInfo 并整体刷新页面：菜单树、按钮权限都在
 * getInitialState 里重新按新租户的令牌拉取，等价于一次「带租户的重新登录」。
 */
const TenantSwitcher: React.FC = () => {
  const { styles } = useStyles();
  const { message } = App.useApp();
  const { initialState } = useModel('@@initialState');
  const [tenants, setTenants] = useState<API.SysTenantDto[]>([]);
  const [loading, setLoading] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [open, setOpen] = useState(false);
  const [target, setTarget] = useState<string>();

  // 当前租户优先取后端下发的 userInfo.tenantId；令牌里的 X-Tenant-Id 是权威值，
  // 这里只是展示，真正的数据隔离由后端 CustomTenantHandler 按令牌执行。
  const currentTenantId = initialState?.currentUser?.tenantId ?? '';

  const loadTenants = useCallback(async () => {
    setLoading(true);
    try {
      const res = await getTenantList({ skipErrorHandler: true });
      if (res?.success && Array.isArray(res.data)) {
        setTenants(res.data);
      }
    } catch (_e) {
      // 列表拉取失败不阻塞页面，下拉框为空时用户可看到「暂无可选租户」
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadTenants();
  }, [loadTenants]);

  const currentTenantName = useMemo(() => {
    if (!currentTenantId) {
      return '未选择租户';
    }
    if (currentTenantId === PLATFORM_TENANT_ID) {
      return '平台租户';
    }
    const hit = tenants.find((item) => item.id === currentTenantId);
    return hit?.tenantName || `租户 ${currentTenantId}`;
  }, [currentTenantId, tenants]);

  const options: TenantOption[] = useMemo(
    () =>
      tenants.map((item) => ({
        value: item.id || '',
        label:
          item.id === PLATFORM_TENANT_ID ? (
            <Space size={4}>
              <span>平台租户</span>
              <Tag color="blue">平台</Tag>
            </Space>
          ) : (
            <span>{item.tenantName}</span>
          ),
      })),
    [tenants],
  );

  const doSwitch = useCallback(
    async (tenantId: string) => {
      setSubmitting(true);
      try {
        const res = await switchTenant(
          { tenantId },
          { skipErrorHandler: true },
        );
        if (!res?.success || !res.data?.accessToken) {
          message.error(res?.errorMessage || '切换租户失败，请重试');
          return;
        }
        // 覆盖本地令牌与用户信息（旧的 userInfo.tenantId 已失效）
        saveAuth(res.data);
        message.success('切换租户成功');
        setOpen(false);
        // 菜单树与按钮权限都在 getInitialState 里按新令牌重新拉取，
        // 直接刷新页面比在前端逐项重置更可靠（权限码、菜单缓存、路由态一并重建）。
        window.location.reload();
      } catch (error: any) {
        message.error(error?.message || '切换租户失败，请重试');
      } finally {
        setSubmitting(false);
      }
    },
    [message],
  );

  const onConfirm = useCallback(async () => {
    if (!target) {
      message.warning('请选择要切换的租户');
      return;
    }
    if (target === currentTenantId) {
      message.info('已在该租户下');
      return;
    }
    await doSwitch(target);
  }, [currentTenantId, doSwitch, message, target]);

  const openSwitcher = useCallback(() => {
    // 用户信息缺失（缓存被清 / 首屏竞态）时拿不到当前租户，切换没有意义
    const user = getUserInfo();
    if (!user?.tenantId) {
      message.warning('未获取到当前租户信息，请重新登录后重试');
      return;
    }
    setTarget(user.tenantId);
    setOpen(true);
  }, [message]);

  return (
    <>
      <span className={styles.trigger} onClick={openSwitcher}>
        <Space size={4}>
          <SwapOutlined />
          <span>{currentTenantName}</span>
        </Space>
      </span>
      <Modal
        title="切换租户"
        open={open}
        okText="切换"
        cancelText="取消"
        confirmLoading={submitting}
        onOk={onConfirm}
        onCancel={() => setOpen(false)}
        destroyOnHidden
      >
        <Space orientation="vertical" size={8} style={{ width: '100%' }}>
          <span className={styles.hint}>
            当前租户：{currentTenantName}
            {currentTenantId ? `（${currentTenantId}）` : ''}
          </span>
          <Select
            style={{ width: '100%' }}
            placeholder="请选择租户"
            loading={loading}
            options={options}
            value={target}
            onChange={setTarget}
          />
          <span className={styles.hint}>
            切换后会重新签发令牌并刷新页面，菜单与按钮权限按新租户重新加载。
          </span>
        </Space>
      </Modal>
    </>
  );
};

export default TenantSwitcher;
