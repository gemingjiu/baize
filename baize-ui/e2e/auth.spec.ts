import { expect, test } from '@playwright/test';

/**
 * E2E 骨架（T2.5）——登录主链路。
 * 这些用例需要真实后端栈；凭证从环境变量读取，未配置则跳过，
 * 避免在没有部署栈的 CI/本地环境误报失败。
 */
const USERNAME = process.env.E2E_USERNAME;
const PASSWORD = process.env.E2E_PASSWORD;
const TENANT = process.env.E2E_TENANT;

test.describe('登录主链路', () => {
  test('未登录访问受保护页应重定向到登录页', async ({ page }) => {
    await page.goto('/welcome');
    await expect(page).toHaveURL(/\/system\/login/);
    await expect(page.getByTestId('login-submit')).toBeVisible();
  });

  test('登录页渲染租户/用户名/密码字段', async ({ page }) => {
    await page.goto('/system/login');
    await expect(page.getByTestId('login-tenant')).toBeAttached();
    await expect(page.getByTestId('login-username')).toBeVisible();
    await expect(page.getByTestId('login-password')).toBeVisible();
    await expect(page.getByTestId('login-submit')).toBeVisible();
  });

  test.skip(
    !USERNAME || !PASSWORD || !TENANT,
    '需要 E2E_USERNAME / E2E_PASSWORD / E2E_TENANT 环境变量',
  );

  test('使用有效凭证登录应进入欢迎页', async ({ page }) => {
    await page.goto('/system/login');
    // 租户下拉为 antd Select，点击后选择目标租户
    await page.getByTestId('login-tenant').click();
    await page.getByTitle(TENANT!).click();
    await page.getByTestId('login-username').fill(USERNAME!);
    await page.getByTestId('login-password').fill(PASSWORD!);
    await page.getByTestId('login-submit').click();
    await expect(page).toHaveURL(/\/welcome/, { timeout: 15_000 });
  });
});
