/// <reference types="vitest" />
import path from 'node:path';
import { defineConfig } from 'vitest/config';

/**
 * T2.5 前端单测：Vitest 2.x + jsdom + React Testing Library。
 *
 * - 只承载纯逻辑（services/utils/access）与少量组件交互测试，
 *   ProTable/ProForm 等重量级 Umi 组件由 Playwright E2E 覆盖（另见 e2e/）。
 * - 路径别名与 tsconfig.json 保持一致（@ → src）。
 * - environment 默认 jsdom；纯逻辑文件可用文件头 `// @vitest-environment node` 加速。
 */
export default defineConfig({
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
      '@@': path.resolve(__dirname, './src/.umi'),
    },
  },
  esbuild: {
    jsx: 'automatic',
  },
  test: {
    environment: 'jsdom',
    globals: true,
    setupFiles: ['./tests/vitest.setup.ts'],
    include: [
      'src/**/*.{test,spec}.{ts,tsx}',
      'tests/unit/**/*.{test,spec}.{ts,tsx}',
    ],
    exclude: ['node_modules/**', 'dist/**', 'src/.umi*/**'],
    coverage: {
      provider: 'v8',
      reporter: ['text', 'json-summary'],
      include: ['src/utils/**', 'src/access.ts'],
    },
  },
});
