/**
 * API 版本前缀（全局唯一来源）。
 *
 * 背景：网关路由（Nacos baize-gateway-dev.yml）按 /api/v1/** 匹配，
 * 因此所有前端请求都必须带此前缀；同时后端各服务的 Controller 直接挂在
 * /auth、/system 之下（StripPrefix=2 由网关去掉 /api/v1 两层），
 * 所以前端看到的完整路径 = API_VERSION + 业务路径。
 *
 * 升级版本时只改这里（例如 '/api/v2'），全部 service 自动跟随，
 * 不需要逐个文件替换，也不会漏改。
 *
 * @example
 *   import { API_VERSION } from '@/constants/api';
 *   request(`${API_VERSION}/auth/login`)   // -> /api/v1/auth/login
 */
export const API_VERSION = '/api/v1';
