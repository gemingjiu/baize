import { ApartmentOutlined, LockOutlined, UserOutlined } from '@ant-design/icons';
import {
  ProFormCheckbox,
  ProFormSelect,
  ProFormText,
} from '@ant-design/pro-components';
import { Helmet, history, useModel } from '@umijs/max';
import { Alert, App, Button, Form } from 'antd';
import { createStyles } from 'antd-style';
import React, { useCallback, useEffect, useState } from 'react';
import { flushSync } from 'react-dom';
import { login } from '@/services/ant-design-pro/api';
import { getUserMenuTree } from '@/services/system/menu';
import { getTenantList } from '@/services/system/tenant';
import {
  saveAuth,
  saveMenuTree,
  savePermissions,
} from '@/utils/auth';
import { getAllPermissions, transformMenu } from '@/utils/menu';
import { PLATFORM_TENANT_ID } from '@/constants/tenant';

const useStyles = createStyles(({ css, token }) => ({
  container: css`
    display: flex;
    height: 100vh;
    width: 100vw;
    overflow: hidden;
    background: ${token.colorBgLayout};
  `,
  leftPanel: css`
    flex: 1;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    padding: 48px;
    position: relative;
    background-image: radial-gradient(${token.colorBorderSecondary} 1px, transparent 0);
    background-size: 22px 22px;

    @media (max-width: 768px) {
      display: none;
    }
  `,
  brandingArea: css`
    position: relative;
    z-index: 1;
    text-align: center;
    max-width: 420px;
  `,
  logoWrapper: css`
    margin-bottom: 24px;
  `,
  logo: css`
    width: 56px;
    height: 56px;
  `,
  brandTitle: css`
    font-size: 26px;
    font-weight: 600;
    letter-spacing: -0.02em;
    color: ${token.colorTextHeading};
    margin: 0 0 8px 0;
  `,
  brandSubtitle: css`
    font-size: 12px;
    color: ${token.colorTextTertiary};
    letter-spacing: 3px;
    margin: 0;
  `,
  features: css`
    margin-top: 40px;
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 10px;
  `,
  featureItem: css`
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 10px 14px;
    background: ${token.colorBgContainer};
    border: 1px solid ${token.colorBorderSecondary};
    border-radius: ${token.borderRadius}px;
    color: ${token.colorTextSecondary};
    font-size: 13px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    transition: border-color 0.2s, color 0.2s;

    &:hover {
      border-color: ${token.colorPrimaryBorder};
      color: ${token.colorText};
    }
  `,
  rightPanel: css`
    width: 420px;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 24px;

    @media (max-width: 768px) {
      width: 100%;
    }
  `,
  loginFormWrapper: css`
    background: ${token.colorBgContainer};
    border: 1px solid ${token.colorBorderSecondary};
    border-radius: ${token.borderRadiusLG}px;
    padding: 32px 28px;
    width: 100%;
    box-sizing: border-box;
  `,
  loginTitle: css`
    color: ${token.colorTextHeading};
    font-size: 20px;
    font-weight: 600;
    text-align: center;
    margin: 0 0 6px 0;
  `,
  loginSubtitle: css`
    color: ${token.colorTextTertiary};
    font-size: 13px;
    text-align: center;
    margin: 0 0 24px 0;
  `,
  errorMessage: css`
    margin-bottom: 16px;
    border-radius: ${token.borderRadius}px;
    font-size: 13px;
  `,
  submitBtn: css`
    width: 100%;
    height: 42px;
    font-size: 15px;
    font-weight: 500;
    margin-top: 8px;
  `,
  loginForm: css`
    width: 100%;

    .ant-form-item {
      margin-bottom: 16px;
    }

    .ant-pro-field {
      width: 100%;
    }
  `,
}));

const LoginMessage: React.FC<{ content: string }> = ({ content }) => {
  const { styles: cls } = useStyles();
  return (
    <Alert
      className={cls.errorMessage}
      title={content}
      type="error"
      showIcon
    />
  );
};

/**
 * 平台（保留）租户ID 见 @/constants/tenant（nil UUID，与后端
 * CustomTenantHandler.PLATFORM_TENANT、网关 TenantFilter 的默认租户一致）。
 * 登录页默认选中平台租户。
 */

const Login: React.FC = () => {
  const [loginErrorMessage, setLoginErrorMessage] = useState<string>('');
  const { initialState, setInitialState } = useModel('@@initialState');
  const { styles } = useStyles();
  const { message } = App.useApp();
  const [submitting, setSubmitting] = useState(false);
  const [tenants, setTenants] = useState<API.SysTenantDto[]>([]);
  const [form] = Form.useForm();

  // 租户下拉框数据来自免鉴权接口 GET /system/tenant/list（网关白名单放行），
  // 因为此时还没有令牌。拉取失败时保留「平台租户」兜底选项，保证登录可用。
  useEffect(() => {
    const loadTenants = async () => {
      try {
        const res = await getTenantList({ skipErrorHandler: true });
        if (res?.success && Array.isArray(res.data)) {
          setTenants(res.data);
        }
      } catch (_e) {
        // 忽略：下面会兜底成只有「平台租户」一项
      }
    };
    loadTenants();
  }, []);

  const tenantOptions = React.useMemo(() => {
    const options = tenants
      .filter((item) => item.id)
      .map((item) => ({
        value: item.id as string,
        // 平台租户（id=0）固定显示为「平台租户」，其余显示租户名
        label:
          item.id === PLATFORM_TENANT_ID
            ? '平台租户'
            : item.tenantName || `租户 ${item.id}`,
      }));
    if (!options.some((option) => option.value === PLATFORM_TENANT_ID)) {
      options.unshift({ value: PLATFORM_TENANT_ID, label: '平台租户' });
    }
    return options;
  }, [tenants]);

  const handleSubmit = useCallback(
    async (values: API.LoginParams) => {
      setSubmitting(true);
      setLoginErrorMessage('');
      try {
        // values.tenant 会被后端 AuthServiceImpl 当作 tenant_id 用于 getByUsernameAndTenantId，
        // 所以这里传的是租户 ID（数字字符串），不是租户编码 / 域名。
        const res = await login(values, { skipErrorHandler: true });
        if (res.success) {
          message.success('登录成功！');
          // 落地 accessToken / refreshToken / userInfo（userInfo 仅作展示缓存）
          saveAuth(res.data);

          const user = res.data.user;
          const menuRes = await getUserMenuTree(user?.id || '', {
            skipErrorHandler: true,
          });
          let menuData: any[] = [];
          if (menuRes.success && menuRes.data) {
            menuData = transformMenu(menuRes.data);
            saveMenuTree(menuData);
            // 权限码来自后端菜单，前端仅用于按钮/菜单显隐（逗号分隔会被拆分）
            const permissions = getAllPermissions(menuRes.data);
            savePermissions(permissions);
          }

          flushSync(() => {
            setInitialState((s) => ({
              ...s,
              currentUser: res.data.user,
              menuData,
            }));
          });

          const urlParams = new URL(window.location.href).searchParams;
          history.push(urlParams.get('redirect') || '/');
          return;
        }
        setLoginErrorMessage(res.errorMessage || '登录失败，请重试');
      } catch (error: any) {
        setLoginErrorMessage(error?.message || '登录失败，请重试');
      } finally {
        setSubmitting(false);
      }
    },
    [setInitialState, message],
  );

  return (
    <>
      <style>{`
        html, body, #root {
          margin: 0 !important;
          padding: 0 !important;
          overflow: hidden !important;
        }
      `}</style>
      <div className={styles.container}>
        <Helmet>
          <title>白泽管理系统 - 登录</title>
        </Helmet>

        <div className={styles.leftPanel}>
          <div className={styles.brandingArea}>
            <div className={styles.logoWrapper}>
              <img src="/logo.svg" alt="logo" className={styles.logo} />
            </div>
            <h1 className={styles.brandTitle}>白泽管理系统</h1>
            <p className={styles.brandSubtitle}>BAIZE MANAGEMENT SYSTEM</p>

            <div className={styles.features}>
              <div className={styles.featureItem}>🔐 安全可靠</div>
              <div className={styles.featureItem}>⚡ 高效便捷</div>
              <div className={styles.featureItem}>🎨 美观易用</div>
              <div className={styles.featureItem}>🔧 灵活配置</div>
            </div>
          </div>
        </div>

        <div className={styles.rightPanel}>
          <div className={styles.loginFormWrapper}>
            <div className={styles.loginTitle}>欢迎登录</div>
            <div className={styles.loginSubtitle}>请输入您的账号和密码</div>

            {loginErrorMessage && <LoginMessage content={loginErrorMessage} />}

            <Form
              form={form}
              className={styles.loginForm}
              initialValues={{
                // 不再预填演示账号密码（模板遗留，且会泄露默认口令）
                remember: true,
                // 默认平台租户：与后端默认租户一致，登录页不强制用户先选租户
                tenant: PLATFORM_TENANT_ID,
              }}
              onFinish={async (values) => {
                await handleSubmit(values as API.LoginParams);
              }}
            >
              <ProFormSelect
                name="tenant"
                label=""
                fieldProps={{
                  size: 'large',
                  suffixIcon: <ApartmentOutlined />,
                }}
                placeholder="请选择租户"
                options={tenantOptions}
                rules={[{ required: true, message: '请选择租户!' }]}
              />

              <ProFormText
                name="username"
                fieldProps={{
                  size: 'large',
                  prefix: <UserOutlined />,
                }}
                placeholder="请输入用户名"
                rules={[{ required: true, message: '请输入用户名!' }]}
              />

              <ProFormText.Password
                name="password"
                fieldProps={{
                  size: 'large',
                  prefix: <LockOutlined />,
                }}
                placeholder="请输入密码"
                rules={[{ required: true, message: '请输入密码!' }]}
              />

              <ProFormCheckbox name="remember">记住密码</ProFormCheckbox>

              <Button
                type="primary"
                htmlType="submit"
                loading={submitting}
                size="large"
                className={styles.submitBtn}
              >
                登 录
              </Button>
            </Form>
          </div>
        </div>
      </div>
    </>
  );
};

export default Login;
