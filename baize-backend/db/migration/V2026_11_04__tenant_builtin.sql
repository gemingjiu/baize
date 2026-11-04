-- =============================================================================
-- V2026_11_04__tenant_builtin.sql
--
-- 用途：租户管理引入「内置租户」标记（is_builtin），落地 2026-11-04 需求——
--       "租户管理需要增加内置逻辑，将保留字段换成内置类型，不让删除"。
--
-- 【背景】
--   平台租户（id = nil UUID '00000000-0000-0000-0000-000000000000'）此前靠
--   tenant_name = '平台租户(保留)' 这种"写在名称里的土法标记"表达"保留"语义：
--   - 没有结构化字段，后端无法据此做删除守卫（任何有 system:tenant:remove
--     权限的人都能逻辑删除平台租户，全平台登录/平台态数据随之不可用）；
--   - "(保留)"混入业务名称，租户下拉/展示都不干净。
--   本次对齐角色模块已验证的 is_builtin 模式（V2026_10_29__role_type_builtin.sql +
--   SysRoleServiceImpl.removeById 双守卫）：平台租户改为 is_builtin='1' 标记，
--   名称去掉"(保留)"后缀，后端 removeById 增加内置守卫拒绝删除。
--
-- 【变更清单】
--   1. sys_tenant 新增 is_builtin varchar(1) NOT NULL DEFAULT '0'
--      （0:非内置, 1:内置（内置不可删除）；与 sys_role.is_builtin 同构）。
--   2. 存量数据：平台租户（nil UUID）标记 is_builtin='1'；
--      tenant_name '平台租户(保留)' → '平台租户'；remark 同步把"保留租户"措辞换为"内置租户"。
--   3. init 种子脚本（baize-system-init.sql）同步本变更（新装库直接落新结构）。
--   4. 后端 SysTenant 实体/DTO 加 isBuiltin，removeById 加内置守卫（Java 侧同 commit）。
--
-- 【ON CONFLICT 仲裁联动】
--   init 对 sys_tenant 用 ON CONFLICT (id)（主键仲裁），新增列不影响；
--   sys_tenant 现有 uk（tenant_code/domain，V2026_11_03 已转 partial）不涉及本列。
--
-- 【幂等性】
--   - ADD COLUMN IF NOT EXISTS：重复执行 0 变更；
--   - 平台租户标记：WHERE id = nil UUID AND is_builtin <> '1'，二跑 0 行；
--   - 名称/remark 更新：以旧值为 WHERE 条件（tenant_name = '平台租户(保留)' /
--     remark = 旧文本），值已是新值时二跑 0 行；
--   - 不依赖调用方 search_path，schema 显式限定 "baize-system"，并自行 CREATE SCHEMA。
--
-- 【执行方式】
--   psql -v ON_ERROR_STOP=1 -f 本文件。
--
-- 【执行后自检】
--   SELECT id, tenant_code, tenant_name, is_builtin
--     FROM "baize-system".sys_tenant
--    WHERE id = '00000000-0000-0000-0000-000000000000';
--   -- 预期 1 行：tenant_name = '平台租户'，is_builtin = '1'
--   SELECT count(*) FROM "baize-system".sys_tenant WHERE is_builtin = '1';
--   -- 预期 1（仅平台租户）
-- =============================================================================

CREATE SCHEMA IF NOT EXISTS "baize-system";

-- 1. 新增 is_builtin：1 内置租户（内置不可删除），默认 0 非内置
ALTER TABLE "baize-system".sys_tenant
    ADD COLUMN IF NOT EXISTS is_builtin varchar(1) DEFAULT '0' NOT NULL;

COMMENT ON COLUMN "baize-system".sys_tenant.is_builtin IS '是否内置租户;0:非内置,1:内置（内置不可删除）';

-- 2. 平台租户标记为内置（幂等：已内置则不更新）
UPDATE "baize-system".sys_tenant
   SET is_builtin = '1'
 WHERE id = '00000000-0000-0000-0000-000000000000'
   AND is_builtin <> '1';

-- 3. 名称去「(保留)」后缀：土法标记由 is_builtin 结构化字段替代（幂等：仅旧名才更新）
UPDATE "baize-system".sys_tenant
   SET tenant_name = '平台租户'
 WHERE id = '00000000-0000-0000-0000-000000000000'
   AND tenant_name = '平台租户(保留)';

-- 4. remark 措辞同步：「保留租户」→「内置租户」（幂等：仅旧文本才更新）
UPDATE "baize-system".sys_tenant
   SET remark = '平台内置租户，平台租户(nil UUID) 的数据归属平台态'
 WHERE id = '00000000-0000-0000-0000-000000000000'
   AND remark = '平台级保留租户，平台租户(nil UUID) 的数据归属平台态';