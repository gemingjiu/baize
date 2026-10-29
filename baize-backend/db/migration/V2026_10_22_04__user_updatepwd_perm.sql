-- =============================================================================
-- V2026_10_22_04__user_updatepwd_perm.sql
--
-- 用途：把 db/init/baize-system-init.sql 中新增的「修改密码」按钮权限
--       （id = '00000000-0000-7000-0000-0000000003f0', perms='system:user:updatePwd'）增量同步到数据库。
--
-- 【为什么需要这个脚本】
--   SysUserController#updatePassword 标注的是 @RequiresPermission("system:user:updatePwd")，
--   但 sys_menu 中原本只有 1007（system:user:resetPwd），没有 1008。
--   结果：非超管角色调用「修改密码」会被 403（超管持有 *:*:* 不受影响，因此该问题长期被掩盖）。
--   baize-system-init.sql 已补上 1008 行，但因该脚本是「全量种子」不能整体重跑
--   （既有 68+ 行会撞唯一键），故在此增量同步。
--
-- 【增量内容】
--   1. sys_menu 新增按钮行 id = '00000000-0000-7000-0000-0000000003f0'（父级 100=用户管理，menu_type='2'）
--   2. sys_role_menu 授权超级管理员（role_id = '00000000-0000-7000-0000-000000000001'）
--
-- 【安全性论证】
--   1. 幂等：使用 INSERT ... SELECT ... WHERE NOT EXISTS，重复执行 0 行变更、不报错。
--   2. 唯一键无冲突：1008 未占用；(tenant_id, menu_name, parent_id) 组合未占用；
--      perms='system:user:updatePwd' 未占用（执行前已核查）。
--   3. 父菜单 100（用户管理）已存在。
--   4. 只做 INSERT，不 UPDATE / DELETE / DROP。
--
-- 【执行前只读核查】（应全部返回 0）
--   SELECT count(*) FROM "baize-system".sys_menu WHERE id = '00000000-0000-7000-0000-0000000003f0';
--   SELECT count(*) FROM "baize-system".sys_menu WHERE perms = 'system:user:updatePwd';
--   SELECT count(*) FROM "baize-system".sys_role_menu WHERE role_id = '00000000-0000-7000-0000-000000000001' AND menu_id = '00000000-0000-7000-0000-0000000003f0';
--   -- 父菜单应存在（返回 1）
--   SELECT count(*) FROM "baize-system".sys_menu WHERE id = '00000000-0000-7000-0000-000000000064';
-- =============================================================================

CREATE SCHEMA IF NOT EXISTS "baize-system";

-- 1. 修改密码按钮（id = '00000000-0000-7000-0000-0000000003f0'，父级 100=用户管理）
--    列顺序与 sys_menu 表结构一致（22 列）
INSERT INTO "baize-system".sys_menu
    (id, tenant_id, parent_id, menu_name, path, component, parameters, external,
     cacheable, menu_type, visible, icon, perms, sort, status, deleted, "version",
     created_by, created_time, modified_by, modified_time, remark)
SELECT '00000000-0000-7000-0000-0000000003f0', '00000000-0000-0000-0000-000000000000', '00000000-0000-7000-0000-000000000064', '修改密码', '', NULL, NULL, '0',
       '0', '2', '0', '#', 'system:user:updatePwd', 8, '0', '0', 1,
       1, '2025-08-11 17:33:07.797', NULL, NULL, NULL
WHERE NOT EXISTS (SELECT 1 FROM "baize-system".sys_menu WHERE id = '00000000-0000-7000-0000-0000000003f0');

-- 2. 授权给超级管理员（role_id = '00000000-0000-7000-0000-000000000001'）
INSERT INTO "baize-system".sys_role_menu (tenant_id, role_id, menu_id)
SELECT '00000000-0000-0000-0000-000000000000', '00000000-0000-7000-0000-000000000001', '00000000-0000-7000-0000-0000000003f0'
WHERE NOT EXISTS (
    SELECT 1 FROM "baize-system".sys_role_menu
     WHERE tenant_id = '00000000-0000-0000-0000-000000000000' AND role_id = '00000000-0000-7000-0000-000000000001' AND menu_id = '00000000-0000-7000-0000-0000000003f0'
);

-- -----------------------------------------------------------------------------
-- 收尾自检（只读，按需执行）
--   SELECT id, menu_name, perms FROM "baize-system".sys_menu WHERE id = '00000000-0000-7000-0000-0000000003f0';
--   -- 预期 1 行：1008 | 修改密码 | system:user:updatePwd
--   SELECT count(*) FROM "baize-system".sys_role_menu WHERE role_id = '00000000-0000-7000-0000-000000000001';
--   -- 预期 86（85 + 本次 1 条）
-- -----------------------------------------------------------------------------
