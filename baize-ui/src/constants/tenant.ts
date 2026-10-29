/**
 * 平台（保留）租户 ID（全局唯一来源）。
 *
 * 与后端保持一致：
 * - CustomTenantHandler.PLATFORM_TENANT（行级租户过滤兜底值）
 * - BaseEntityObjectHandler（未指定租户时的填充值）
 * - 网关 TenantFilter 的默认租户（X-Tenant-Id 缺失时）
 *
 * UUIDv7 迁移后平台租户 id 是 nil UUID（全零），
 * 不再是旧版的数字 '0'；所有租户身份比较都必须用这个常量。
 */
export const PLATFORM_TENANT_ID = '00000000-0000-0000-0000-000000000000';
