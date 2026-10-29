-- =============================================================================
-- V2026_10_31__index_optimize.sql
--
-- 用途：落地 2026-10-29 表模型评审的索引类低风险动作——补 2 个缺口索引、
--       删 4 个冗余索引、sys_tenant.domain 补唯一索引。
--
-- 【背景】
--   1. 索引缺口 A：sys_user.dept_id 无索引。DataScopeInnerInterceptor 按
--      部门档（3 本部门 / 4 本部门及以下）过滤时走 dept_id IN (...) 全表扫。
--   2. 索引缺口 B：sys_user_role.role_id 不被 PK(tenant_id, user_id, role_id)
--      覆盖（role_id 非最左前缀）。角色管理「查看绑定用户」、removeById 的
--      占用守卫（getUserIdsByRoleId）按 role_id 查询全表扫。
--   3. 冗余索引：4 个关系表的单列 tenant_id 索引与各自 PK 的最左前缀完全
--      重复（PK 首列即 tenant_id），保留只会浪费写放大：
--        idx_role_menu_tenant_id  ≺ PK(tenant_id, role_id, menu_id)
--        idx_user_role_tenant_id  ≺ PK(tenant_id, user_id, role_id)
--        idx_role_perm_tenant_id  ≺ PK(tenant_id, role_id, perm_id)
--        idx_role_dept_tenant_id  ≺ PK(tenant_id, role_id, dept_id)
--   4. sys_tenant.domain 列注释声明「唯一域名」（ddl:639）但从未建唯一约束，
--      文档承诺与库结构漂移——补唯一索引对齐语义。
--
-- 【安全性论证】
--   1. 幂等：CREATE INDEX IF NOT EXISTS；DROP INDEX IF EXISTS，重复执行 0 行变更。
--   2. 删冗余索引前以 pg_indexes 实查存在性（dev 库 4 个全部存在），
--      DROP IF EXISTS 保证脚本在未跑过 V2026_10_22__constraints_and_align.sql
--      的库上同样安全。
--   3. domain 唯一索引允许多 NULL（PG 语义：NULL ≠ NULL），与「域名可选」
--      的业务模型一致；若存量数据已有重复 domain 将建索引失败——执行前
--      请跑文末自检查询确认。
--   4. CREATE INDEX CONCURRENTLY 不在事务块内使用（本脚本逐语句自动提交，
--      psql 默认非单事务执行，可直接用；如用 JDBC 单事务执行请去掉
--      CONCURRENTLY 关键字或分两次提交）。
--
-- 【执行方式】
--   psql -v ON_ERROR_STOP=1 -f 本文件。
--
-- 【执行后自检】
--   SELECT indexname FROM pg_indexes WHERE schemaname='baize-system'
--     AND indexname IN ('idx_sys_user_dept_id','idx_user_role_role_id',
--                       'uk_sys_tenant_domain');
--   -- 预期 3 行
--   SELECT indexname FROM pg_indexes WHERE schemaname='baize-system'
--     AND indexname IN ('idx_role_menu_tenant_id','idx_user_role_tenant_id',
--                       'idx_role_perm_tenant_id','idx_role_dept_tenant_id');
--   -- 预期 0 行
-- =============================================================================

CREATE SCHEMA IF NOT EXISTS "baize-system";

-- -----------------------------------------------------------------------------
-- 1. 补缺口索引
-- -----------------------------------------------------------------------------
CREATE INDEX IF NOT EXISTS idx_sys_user_dept_id
    ON "baize-system".sys_user(dept_id);

CREATE INDEX IF NOT EXISTS idx_user_role_role_id
    ON "baize-system".sys_user_role(role_id);

-- -----------------------------------------------------------------------------
-- 2. 删冗余索引（单列 tenant_id 与 PK 最左前缀重复）
-- -----------------------------------------------------------------------------
DROP INDEX IF EXISTS "baize-system".idx_role_menu_tenant_id;
DROP INDEX IF EXISTS "baize-system".idx_user_role_tenant_id;
DROP INDEX IF EXISTS "baize-system".idx_role_perm_tenant_id;
DROP INDEX IF EXISTS "baize-system".idx_role_dept_tenant_id;

-- -----------------------------------------------------------------------------
-- 3. sys_tenant.domain 唯一索引（对齐 ddl 列注释「唯一域名」承诺）
--    注意：若存量数据存在重复 domain 此句会失败，先跑：
--      SELECT "domain", count(*) FROM "baize-system".sys_tenant
--       WHERE "domain" IS NOT NULL GROUP BY "domain" HAVING count(*) > 1;
-- -----------------------------------------------------------------------------
CREATE UNIQUE INDEX IF NOT EXISTS uk_sys_tenant_domain
    ON "baize-system".sys_tenant("domain");

-- -----------------------------------------------------------------------------
-- 4. DDL 注释同步：修正 ddl 中冗余索引注释漂移（删除项在 DDL 里同步移除）
--    （DDL 文件变更随本迁移同 commit 更新，此处不涉及 SQL）
-- -----------------------------------------------------------------------------
