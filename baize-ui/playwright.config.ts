import { defineConfig, devices } from '@playwright/test';

/**
 * E2E 配置（T2.5）
 * ----------------------------------------------------------------------------
 * E2E 针对的是「已部署的完整栈」（前端 + 网关 + 后端 + PG/Redis/Nacos），
 * 不随单测在 CI 里裸跑：CI 没有这套栈，故 E2E job 先以 advisory 运行，
 * 由 E2E_BASE_URL 指向目标环境（本地 dev / test / pre 均可）。
 *
 * 本地跑：
 *   1) 启动完整栈（见 deploy/ 与 README）
 *   2) E2E_BASE_URL=http://localhost:8000 npx playwright test
 *   3) 首次需 npx playwright install chromium
 */
const baseURL = process.env.E2E_BASE_URL || 'http://localhost:8000';

export default defineConfig({
  testDir: './e2e',
  fullyParallel: false,
  forbidOnly: !!process.env.CI,
  retries: process.env.CI ? 1 : 0,
  workers: 1,
  reporter: process.env.CI ? 'line' : 'list',
  timeout: 30_000,
  expect: { timeout: 5_000 },
  use: {
    baseURL,
    trace: 'on-first-retry',
    screenshot: 'only-on-failure',
    ...devices['Desktop Chrome'],
  },
  projects: [{ name: 'chromium', use: { ...devices['Desktop Chrome'] } }],
});
