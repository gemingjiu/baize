import { LockOutlined, UserOutlined } from '@ant-design/icons';
import { ProFormCheckbox, ProFormText } from '@ant-design/pro-components';
import { Helmet, history, useModel } from '@umijs/max';
import { Alert, App, Button, Form } from 'antd';
import { createStyles } from 'antd-style';
import React, { useCallback, useState } from 'react';
import { flushSync } from 'react-dom';
import { login } from '@/services/ant-design-pro/api';
import { getUserMenuTree } from '@/services/system/menu';
import { getAllButtonPermissions, transformMenu } from '@/utils/menu';

const useStyles = createStyles(({ css, token }) => ({
  container: css`
    display: flex;
    height: 100vh;
    width: 100vw;
    overflow: hidden;
    background: linear-gradient(135deg, #0f172a 0%, #1e293b 100%);
  `,
  leftPanel: css`
    flex: 1;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    padding: 48px;
    position: relative;
    overflow: hidden;

    &::before {
      content: '';
      position: absolute;
      inset: 0;
      background:
        radial-gradient(circle at 20% 30%, rgba(24, 144, 255, 0.12) 0%, transparent 50%),
        radial-gradient(circle at 80% 70%, rgba(99, 102, 241, 0.1) 0%, transparent 50%);
    }
  `,
  brandingArea: css`
    position: relative;
    z-index: 1;
    text-align: center;
    max-width: 400px;
  `,
  logoWrapper: css`
    margin-bottom: 28px;
  `,
  logo: css`
    width: 64px;
    height: 64px;
    filter: drop-shadow(0 0 16px rgba(24, 144, 255, 0.4));
  `,
  brandTitle: css`
    font-size: 28px;
    font-weight: 700;
    color: #f1f5f9;
    margin: 0 0 8px 0;
  `,
  brandSubtitle: css`
    font-size: 13px;
    color: #64748b;
    letter-spacing: 2px;
    margin: 0;
  `,
  features: css`
    margin-top: 48px;
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
  `,
  featureItem: css`
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 10px 14px;
    background: rgba(255, 255, 255, 0.04);
    border: 1px solid rgba(255, 255, 255, 0.06);
    border-radius: 8px;
    color: #94a3b8;
    font-size: 13px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;

    &:hover {
      background: rgba(24, 144, 255, 0.08);
      border-color: rgba(24, 144, 255, 0.2);
    }
  `,
  rightPanel: css`
    width: 360px;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 24px;
  `,
  loginFormWrapper: css`
    background: rgba(255, 255, 255, 0.04);
    backdrop-filter: blur(16px);
    border: 1px solid rgba(255, 255, 255, 0.08);
    border-radius: 16px;
    padding: 32px 24px;
    box-shadow: 0 20px 40px rgba(0, 0, 0, 0.3);
    width: 100%;
    box-sizing: border-box;
    overflow: hidden;
  `,
  loginTitle: css`
    color: #f1f5f9;
    font-size: 22px;
    font-weight: 600;
    text-align: center;
    margin: 0 0 6px 0;
  `,
  loginSubtitle: css`
    color: #64748b;
    font-size: 13px;
    text-align: center;
    margin: 0 0 28px 0;
  `,
  errorMessage: css`
    margin-bottom: 16px;
    border-radius: 8px;
    border: 1px solid rgba(239, 68, 68, 0.2);
    background: rgba(239, 68, 68, 0.06);
    font-size: 13px;
  `,
  submitBtn: css`
    width: 100%;
    height: 42px;
    font-size: 15px;
    font-weight: 500;
    border-radius: 8px;
    background: #1890ff;
    border: none;
    margin-top: 8px;
    transition: all 0.3s;

    &:hover {
      background: #40a9ff;
    }
  `,
  loginForm: css`
    width: 100%;

    .ant-form-item {
      margin-bottom: 16px;
    }

    .ant-pro-field {
      width: 100%;
    }

    .ant-input-affix-wrapper {
      background: rgba(255, 255, 255, 0.06) !important;
      border: 1px solid rgba(255, 255, 255, 0.12) !important;
      border-radius: 8px !important;
      color: #f1f5f9 !important;
      transition: all 0.3s !important;

      &:hover {
        border-color: rgba(255, 255, 255, 0.25) !important;
      }

      &:focus,
      &.ant-input-affix-wrapper-focused {
        border-color: ${token.colorPrimary} !important;
        box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.2) !important;
      }

      .ant-input {
        background: transparent !important;
        color: #f1f5f9 !important;
        font-size: 14px !important;

        &::placeholder {
          color: #475569 !important;
        }
      }

      .anticon {
        color: #64748b !important;
      }
    }

    .ant-checkbox-wrapper {
      color: #94a3b8 !important;
      font-size: 13px !important;

      .ant-checkbox-inner {
        background: rgba(255, 255, 255, 0.05) !important;
        border-color: rgba(255, 255, 255, 0.2) !important;
      }

      .ant-checkbox-checked .ant-checkbox-inner {
        background-color: ${token.colorPrimary} !important;
        border-color: ${token.colorPrimary} !important;
      }
    }
  `,
}));

const LoginMessage: React.FC<{ content: string }> = ({ content }) => {
  const { styles: cls } = useStyles();
  return (
    <Alert
      className={cls.errorMessage}
      message={content}
      type="error"
      showIcon
    />
  );
};

const Login: React.FC = () => {
  const [loginErrorMessage, setLoginErrorMessage] = useState<string>('');
  const { initialState, setInitialState } = useModel('@@initialState');
  const { styles } = useStyles();
  const { message } = App.useApp();
  const [submitting, setSubmitting] = useState(false);
  const [form] = Form.useForm();

  const handleSubmit = useCallback(
    async (values: API.LoginParams) => {
      setSubmitting(true);
      setLoginErrorMessage('');
      try {
        const res = await login(values, { skipErrorHandler: true });
        if (res.success) {
          message.success('登录成功！');
          localStorage.setItem('accessToken', res.data.accessToken);
          localStorage.setItem('refreshToken', res.data.refreshToken);
          localStorage.setItem('userInfo', JSON.stringify(res.data.user));

          const user = res.data.user;
          const menuRes = await getUserMenuTree(user?.id || '', {
            skipErrorHandler: true,
          });
          let menuData: any[] = [];
          if (menuRes.success && menuRes.data) {
            menuData = transformMenu(menuRes.data);
            localStorage.setItem('menuTree', JSON.stringify(menuData));
            const permissions = getAllButtonPermissions(menuRes.data);
            localStorage.setItem('permissions', JSON.stringify(permissions));
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
                username: 'admin',
                password: 'admin123',
                remember: true,
              }}
              onFinish={async (values) => {
                await handleSubmit(values as API.LoginParams);
              }}
            >
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
