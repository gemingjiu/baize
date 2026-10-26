-- =============================================================================
-- V2026_10_22_02__menu_buttons_incremental.sql
--
-- 用途：把 db/init/baize-system-init.sql 中新增的 17 个菜单/按钮及其授权
--       增量同步到数据库。
--
-- 【为什么需要这个脚本】
--   baize-system-init.sql 是「全量种子脚本」，其中 68 条 sys_menu 与 68 条
--   sys_role_menu 早已在库中。若整体重跑该脚本，会在这 68 条上直接撞
--   uk_sys_menu_tenant_name_parent / uk_sys_role_menu_tenant_role_menu 而中止。
--   因此这里只抽出增量部分，并用 WHERE NOT EXISTS 保证可重复执行。
--
-- 【增量内容（共 17 项）】
--   定时任务菜单（1 个菜单 + 4 个按钮）：id 110 / 1101-1104
--   岗位管理按钮（4 个）：                  id 1071-1074
--   参数设置按钮（4 个）：                  id 1081-1084
--   通知公告按钮（4 个）：                  id 1091-1094
--   对应 sys_role_menu 授权（超级管理员 role_id=1）共 17 条
--
-- 【安全性论证】
--   1. 幂等：全部使用 INSERT ... SELECT ... WHERE NOT EXISTS，
--      重复执行是 0 行变更，不会报错也不会产生重复数据。
--   2. 唯一键无冲突：已实测（见下方注释中的核查 SQL），17 个新菜单的
--      (tenant_id, menu_name, parent_id) 组合与 perms 均未被占用。
--   3. 父菜单均已存在：107（岗位管理）、108（参数设置）、109（通知公告）
--      是既有菜单行；110（定时任务）的父级是 1（系统管理目录），也已存在。
--   4. 无序列依赖：全表 id 由应用雪花算法（ASSIGN_ID）生成，
--      pg_get_serial_sequence 对这些表返回 NULL，不存在序列撞值。
--   5. 不涉及数据删除或更新，只做 INSERT。
--
-- 【执行前必做的只读核查】（在 psql / JDBC 客户端执行，确认后再跑本脚本）
--   -- a. 确认 17 个 id 均未被占用（应全部返回 0）
--   SELECT count(*) FROM "baize-system".sys_menu
--    WHERE id IN (110,1071,1072,1073,1074,1081,1082,1083,1084,1091,1092,1093,1094,1101,1102,1103,1104);
--   -- b. 确认 17 个 perms 均未被占用（应全部返回 0）
--   SELECT count(*) FROM "baize-system".sys_menu
--    WHERE perms IN ('system:job:list','system:job:query','system:job:add','system:job:edit','system:job:remove',
--                    'system:post:query','system:post:add','system:post:edit','system:post:remove',
--                    'system:config:query','system:config:add','system:config:edit','system:config:remove',
--                    'system:notice:query','system:notice:add','system:notice:edit','system:notice:remove');
--   -- c. 确认父菜单存在（应全部返回 1）
--   SELECT count(*) FROM "baize-system".sys_menu WHERE id IN (1,107,108,109);
--   -- d. 确认目标授权关系不存在（应返回 0）
--   SELECT count(*) FROM "baize-system".sys_role_menu
--    WHERE role_id = 1 AND menu_id IN (110,1071,1072,1073,1074,1081,1082,1083,1084,1091,1092,1093,1094,1101,1102,1103,1104);
--
-- 【执行方式】
--   psql 或任意 JDBC 客户端整文件执行；本仓库无 psql 时用单文件 Java + 42.3.5 驱动。
--   建议先在事务中执行并核对影响行数，再 COMMIT：
--     BEGIN;
--     \i V2026_10_22_02__menu_buttons_incremental.sql
--     SELECT count(*) FROM "baize-system".sys_menu WHERE id IN (110,1071,1101);  -- 预期 3
--     COMMIT;
-- =============================================================================

CREATE SCHEMA IF NOT EXISTS "baize-system";

-- -----------------------------------------------------------------------------
-- 1. 定时任务菜单（id=110，父级 1=系统管理）
--    列顺序与 sys_menu 表结构一致：
--    id, tenant_id, parent_id, menu_name, path, component, parameters, external,
--    cacheable, menu_type, visible, icon, perms, sort, status, deleted, "version",
--    created_by, created_time, modified_by, modified_time, remark
-- -----------------------------------------------------------------------------
INSERT INTO "baize-system".sys_menu
    (id, tenant_id, parent_id, menu_name, path, component, parameters, external,
     cacheable, menu_type, visible, icon, perms, sort, status, deleted, "version",
     created_by, created_time, modified_by, modified_time, remark)
SELECT 110, 0, 1, '定时任务', 'job', 'system/job/index', NULL, '0',
       '1', '1', '0', 'job', 'system:job:list', 11, '0', '0', 1,
       1, '2025-08-11 17:33:07.797', NULL, NULL, '定时任务菜单'
WHERE NOT EXISTS (SELECT 1 FROM "baize-system".sys_menu WHERE id = 110);

-- -----------------------------------------------------------------------------
-- 2. 定时任务按钮（id=1101-1104，父级 110）
-- -----------------------------------------------------------------------------
INSERT INTO "baize-system".sys_menu
    (id, tenant_id, parent_id, menu_name, path, component, parameters, external,
     cacheable, menu_type, visible, icon, perms, sort, status, deleted, "version",
     created_by, created_time, modified_by, modified_time, remark)
SELECT v.id, 0, 110, v.menu_name, '', NULL, NULL, '0',
       '0', '2', '0', '#', v.perms, v.sort, '0', '0', 1,
       1, '2025-08-11 17:33:07.797', NULL, NULL, NULL
FROM (VALUES
    (1101, '任务查询', 'system:job:query',  1),
    (1102, '任务新增', 'system:job:add',    2),
    (1103, '任务修改', 'system:job:edit',   3),
    (1104, '任务删除', 'system:job:remove', 4)
) AS v(id, menu_name, perms, sort)
WHERE NOT EXISTS (SELECT 1 FROM "baize-system".sys_menu WHERE id = v.id);

-- -----------------------------------------------------------------------------
-- 3. 岗位管理按钮（id=1071-1074，父级 107）
-- -----------------------------------------------------------------------------
INSERT INTO "baize-system".sys_menu
    (id, tenant_id, parent_id, menu_name, path, component, parameters, external,
     cacheable, menu_type, visible, icon, perms, sort, status, deleted, "version",
     created_by, created_time, modified_by, modified_time, remark)
SELECT v.id, 0, 107, v.menu_name, '', NULL, NULL, '0',
       '0', '2', '0', '#', v.perms, v.sort, '0', '0', 1,
       1, '2025-08-11 17:33:07.797', NULL, NULL, NULL
FROM (VALUES
    (1071, '岗位查询', 'system:post:query',  1),
    (1072, '岗位新增', 'system:post:add',    2),
    (1073, '岗位修改', 'system:post:edit',   3),
    (1074, '岗位删除', 'system:post:remove', 4)
) AS v(id, menu_name, perms, sort)
WHERE NOT EXISTS (SELECT 1 FROM "baize-system".sys_menu WHERE id = v.id);

-- -----------------------------------------------------------------------------
-- 4. 参数设置按钮（id=1081-1084，父级 108）
-- -----------------------------------------------------------------------------
INSERT INTO "baize-system".sys_menu
    (id, tenant_id, parent_id, menu_name, path, component, parameters, external,
     cacheable, menu_type, visible, icon, perms, sort, status, deleted, "version",
     created_by, created_time, modified_by, modified_time, remark)
SELECT v.id, 0, 108, v.menu_name, '', NULL, NULL, '0',
       '0', '2', '0', '#', v.perms, v.sort, '0', '0', 1,
       1, '2025-08-11 17:33:07.797', NULL, NULL, NULL
FROM (VALUES
    (1081, '参数查询', 'system:config:query',  1),
    (1082, '参数新增', 'system:config:add',    2),
    (1083, '参数修改', 'system:config:edit',   3),
    (1084, '参数删除', 'system:config:remove', 4)
) AS v(id, menu_name, perms, sort)
WHERE NOT EXISTS (SELECT 1 FROM "baize-system".sys_menu WHERE id = v.id);

-- -----------------------------------------------------------------------------
-- 5. 通知公告按钮（id=1091-1094，父级 109）
-- -----------------------------------------------------------------------------
INSERT INTO "baize-system".sys_menu
    (id, tenant_id, parent_id, menu_name, path, component, parameters, external,
     cacheable, menu_type, visible, icon, perms, sort, status, deleted, "version",
     created_by, created_time, modified_by, modified_time, remark)
SELECT v.id, 0, 109, v.menu_name, '', NULL, NULL, '0',
       '0', '2', '0', '#', v.perms, v.sort, '0', '0', 1,
       1, '2025-08-11 17:33:07.797', NULL, NULL, NULL
FROM (VALUES
    (1091, '公告查询', 'system:notice:query',  1),
    (1092, '公告新增', 'system:notice:add',    2),
    (1093, '公告修改', 'system:notice:edit',   3),
    (1094, '公告删除', 'system:notice:remove', 4)
) AS v(id, menu_name, perms, sort)
WHERE NOT EXISTS (SELECT 1 FROM "baize-system".sys_menu WHERE id = v.id);

-- -----------------------------------------------------------------------------
-- 6. 授权给超级管理员（role_id=1），共 17 条
--    与 sys_role_menu 的 (tenant_id, role_id, menu_id) 列顺序一致
-- -----------------------------------------------------------------------------
INSERT INTO "baize-system".sys_role_menu (tenant_id, role_id, menu_id)
SELECT 0, 1, v.menu_id
FROM (VALUES
    (110),
    (1101), (1102), (1103), (1104),
    (1071), (1072), (1073), (1074),
    (1081), (1082), (1083), (1084),
    (1091), (1092), (1093), (1094)
) AS v(menu_id)
WHERE NOT EXISTS (
    SELECT 1 FROM "baize-system".sys_role_menu
     WHERE tenant_id = 0 AND role_id = 1 AND menu_id = v.menu_id
);

-- -----------------------------------------------------------------------------
-- 7. 收尾自检（只读，按需执行）
-- -----------------------------------------------------------------------------
-- SELECT id, menu_name, perms FROM "baize-system".sys_menu
--  WHERE id IN (110,1071,1072,1073,1074,1081,1082,1083,1084,1091,1092,1093,1094,1101,1102,1103,1104)
--  ORDER BY id;
-- -- 预期 17 行
-- SELECT count(*) FROM "baize-system".sys_role_menu WHERE role_id = 1;
-- -- 预期 85（68 既有 + 17 新增）
