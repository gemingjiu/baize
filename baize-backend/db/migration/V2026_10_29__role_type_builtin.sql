-- =============================================================================
-- V2026_10_29__role_type_builtin.sql
--
-- 用途：sys_role 角色表补充「角色类型」与「内置标记」两列，并修正 data_scope 注释。
--
-- 【背景】
--   1. 此前平台角色仅靠 tenant_id=0 + 代码白名单（baize.security.super-admin-role-codes）
--      隐式推断，表结构无法区分「平台预置」与「租户自建」角色；为后续
--      「建租户时初始化默认角色」预留分类依据。
--   2. 内置角色（admin）此前可被逻辑删除且无任何保护——删除后绑定行成死引用，
--      用户下次刷新 token 权限静默变化。新增 is_builtin 标记，配合
--      SysRoleServiceImpl.removeById 的「内置拒绝 + 被占用拒绝」双守卫做删除保护。
--   3. DDL 注释 data_scope 只写了 1-4，代码（CustomDataScopeHandler）实际支持
--      5 仅本人，本次一并修正文档漂移。
--
-- 【增量内容（共 4 项）】
--   1. sys_role 新增 role_type varchar(1) NOT NULL DEFAULT '1'
--      （0:平台角色 1:租户角色；跟随全库 varchar(1) 标志位约定，不用 tinyint）
--   2. sys_role 新增 is_builtin varchar(1) NOT NULL DEFAULT '0'
--      （1:内置角色，内置不可删除）
--   3. 存量内置角色标记：id=1 admin → role_type='0', is_builtin='1'
--   4. data_scope 列注释补「5：仅本人数据权限」
--
-- 【安全性论证】
--   1. 幂等：ADD COLUMN IF NOT EXISTS + UPDATE 带 WHERE 守卫，重复执行 0 行变更。
--   2. DEFAULT '1'/'0' + NOT NULL：存量行由 PG 自动填充默认值，不锁全表
--      （PG 11+ ADD COLUMN 带常量默认值是元数据级操作）。
--   3. 实体兼容：SysRole/SysRoleDto 同步新增字段；旧代码读到新列无影响
--      （MP @TableField 显式映射，不因新列报错）。
--
-- 【执行方式】
--   psql 或任意 JDBC 客户端整文件执行。
--
-- 【执行后自检】
--   SELECT column_name, column_default, is_nullable FROM information_schema.columns
--    WHERE table_schema='baize-system' AND table_name='sys_role'
--      AND column_name IN ('role_type','is_builtin');          -- 预期 2 行
--   SELECT id, role_code, role_type, is_builtin FROM "baize-system".sys_role WHERE id = 1;
--   -- 预期：1 | admin | 0 | 1
-- =============================================================================

CREATE SCHEMA IF NOT EXISTS "baize-system";

-- -----------------------------------------------------------------------------
-- 1. 新增 role_type：0 平台角色 / 1 租户角色（默认租户角色）
-- -----------------------------------------------------------------------------
ALTER TABLE "baize-system".sys_role
    ADD COLUMN IF NOT EXISTS role_type varchar(1) DEFAULT '1' NOT NULL;

COMMENT ON COLUMN "baize-system".sys_role.role_type IS '角色类型;0:平台角色,1:租户角色';

-- -----------------------------------------------------------------------------
-- 2. 新增 is_builtin：1 内置角色（内置不可删除）
-- -----------------------------------------------------------------------------
ALTER TABLE "baize-system".sys_role
    ADD COLUMN IF NOT EXISTS is_builtin varchar(1) DEFAULT '0' NOT NULL;

COMMENT ON COLUMN "baize-system".sys_role.is_builtin IS '是否内置角色;0:非内置,1:内置（内置不可删除）';

-- -----------------------------------------------------------------------------
-- 3. 存量内置角色标记（admin，id=1，tenant_id=0 平台共享角色）
--    WHERE 守卫保证幂等：已标记时 0 行变更
-- -----------------------------------------------------------------------------
UPDATE "baize-system".sys_role
   SET role_type  = '0',
       is_builtin = '1'
 WHERE id = 1
   AND (role_type <> '0' OR is_builtin <> '1');

-- -----------------------------------------------------------------------------
-- 4. 修正 data_scope 注释：补「5 仅本人」（CustomDataScopeHandler 实际支持）
-- -----------------------------------------------------------------------------
COMMENT ON COLUMN "baize-system".sys_role.data_scope IS '数据范围;1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限 5：仅本人数据权限';
