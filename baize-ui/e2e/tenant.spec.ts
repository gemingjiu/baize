import { expect, test } from '@playwright/test';

/**
 * E2E 骨架（T2.5）——租户切换。
 * 依赖已登录态，未配置凭证则跳过。
 */
const USERNAME = process.env.E2E_USERNAME;
const PASSWORD = process.env.E2E_PASSWORD;
const TENANT = process.env.E2E_TENANT;

test.skip(
  !USERNAME || !PASSWORD || !TENANT,
  '需要 E2E_USERNAME / E2E_PASSWORD / E2E_TENANT 环境变量',
);

test.describe('租户切换', () => {
  test('登录后可打开租户切换弹窗', async ({ page }) => {
    // 复用登录流程进入系统
    await page.goto('/system/login');
    await page.getByTestId('login-tenant').click();
    await page.getByTitle(TENANT!).click();
    await page.getByTestId('login-username').fill(USERNAME!);
    await page.getByTestId('login-password').fill(PASSWORD!);
    await page.getByTestId('login-submit').click();
    await expect(page).toHaveURL(/\/welcome/, { timeout: 15_000 });

    // 顶部右侧租户切换入口
    await page.getByTestId('tenant-switcher-trigger').click();
    await expect(page.getByTestId('tenant-switcher-select')).toBeVisible();
  });
});
