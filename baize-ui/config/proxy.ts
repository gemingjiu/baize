/**
 * @name 代理的配置
 * @see 在生产环境 代理是无法生效的，所以这里没有生产环境的配置
 * -------------------------------
 * The agent cannot take effect in the production environment
 * so there is no configuration for the production environment
 * For details, please see
 * https://pro.ant.design/docs/deploy
 *
 * @doc https://umijs.org/docs/guides/proxy
 */

/** 本地 / dev / test 环境后端网关（不要带 /api/v1，前缀由请求自带） */
const LOCAL_GATEWAY = 'http://localhost:8000';

/**
 * pre 环境网关地址。
 * 通过环境变量 BAIZE_PRE_GATEWAY 配置，例如：
 * BAIZE_PRE_GATEWAY=https://pre-gateway.example.com REACT_APP_ENV=pre npm start
 * 未配置时回退到本地网关（LOCAL_GATEWAY），不再保留占位字符串。
 */
const PRE_GATEWAY = process.env.BAIZE_PRE_GATEWAY || LOCAL_GATEWAY;

/**
 * 只透传 /api/v1/ 前缀的请求，不做任何 pathRewrite。
 *
 * 历史包袱（2026-10-22 修）：之前 service 层写的是 /auth/xxx、/system/xxx（不带版本前缀），
 * 靠这里 pathRewrite 补 /api/v1；但那样"版本号"藏在代理层，契约不可见，
 * 且 pathRewrite: { '^': '/api/v1' } 是整体替换，会把 /login 丢掉（踩过坑）。
 *
 * 现在的约定：
 *   - 版本前缀全局唯一来源 = src/constants/api.ts 的 API_VERSION（'/api/v1'）；
 *   - 所有 service 的请求路径写成 `${API_VERSION}/auth/xxx`，契约显式、升级只改一处；
 *   - 因此代理层不再重写，直接透传，杜绝"双重前缀"这类问题。
 *
 * 若日后升级到 /api/v2：改 src/constants/api.ts 一处即可，本文件无需改动。
 */
const apiProxy = (target: string) => ({
  '/api/v1/': {
    target,
    changeOrigin: true,
  },
});

export default {
  dev: apiProxy(LOCAL_GATEWAY),
  test: apiProxy(LOCAL_GATEWAY),
  pre: apiProxy(PRE_GATEWAY),
};
