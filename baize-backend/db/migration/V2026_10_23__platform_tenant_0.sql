-- =============================================================================
-- V2026_10_23__platform_tenant_0.sql
--
-- 用途：把 0 确立为「平台级保留租户」。
--
-- 背景（需求 m03289）：
--   admin 用户的 tenant_id 已为 0（平台态），超级管理员角色/平台种子菜单/部门等
--   也全部挂在 tenant_id=0 下；但 sys_tenant 表里没有 id = '00000000-0000-0000-0000-000000000000' 的行，导致「平台租户」
--   只存在于代码常量与数据行的隐式约定里，租户管理页也看不到它。
--   本脚本在 sys_tenant 中补入 id = '00000000-0000-0000-0000-000000000000' 平台保留行，并同步到 init 全量种子脚本。
--
-- 语义：
--   - 平台态隔离（而非跳过过滤）：CustomTenantHandler.getTenantId() 在请求上下文缺失
--     租户 ID 时兜底为 "0"，租户过滤条件始终可下推为 tenant_id=0；
--     ignoreTable 只豁免 sys_tenant 自身（租户元数据表必须全局可见）。
--   - 0 为 NOT NULL 平台保留值：sys_tenant.id 为主键天然 NOT NULL；
--     所有业务表的 tenant_id 列也已是 NOT NULL（见 V2026_10_22__constraints_and_align.sql），
--     平台保留行的其余 NOT NULL 列（sort/status/deleted/version/created_by/created_time）
--     也全部给值，保证插入不违约。
--
-- 幂等：
--   - 显式限定 "baize-system" schema 并自行 CREATE SCHEMA，不依赖调用方 search_path；
--   - 用 ON CONFLICT (id) DO NOTHING 兜底，可重复执行、并发安全。
-- =============================================================================

CREATE SCHEMA IF NOT EXISTS "baize-system";

INSERT INTO "baize-system".sys_tenant (id, tenant_code, tenant_name, expire_time, contact_person, contact_phone, contact_email, "domain", max_user, sort, status, deleted, "version", created_by, created_time, modified_by, modified_time, remark)
SELECT '00000000-0000-0000-0000-000000000000', 'PLATFORM', '平台租户(保留)', NULL, NULL, NULL, NULL, NULL, 10, 0, '0', '0', 0, '00000000-0000-7000-0000-000000000001', now(), NULL, NULL, '平台级保留租户，平台租户(nil UUID) 的数据归属平台态'
WHERE NOT EXISTS (SELECT 1 FROM "baize-system".sys_tenant WHERE id = '00000000-0000-0000-0000-000000000000')
ON CONFLICT (id) DO NOTHING;
