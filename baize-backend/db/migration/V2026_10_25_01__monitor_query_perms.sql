-- =============================================================================
-- V2026_10_25_01__monitor_query_perms.sql
--
-- 用途：服务监控 / 缓存监控 两个页面新增「查询」权限码，并授权给超级管理员。
--
-- 【为什么需要这个脚本】
--   1. 服务监控（菜单 203）与缓存监控（菜单 204）在库中只有目录级权限
--      monitor:server:list / monitor:cache:list（menu_type='1'），没有按钮级
--      权限码。而后端 ServerInfoController（GET /system/monitor/server）与
--      新增的 CacheMonitorController（GET /system/monitor/cache、
--      GET /system/monitor/cache/keys）都用
--      @RequiresPermission("monitor:server:query" / "monitor:cache:query")
--      做校验 —— 权限码不存在 ⇒ 非超管用户一律被拒，页面打不开也拿不到数据。
--   2. db/init/baize-system-init.sql 是全量种子脚本，整体重跑会在既有 68 条
--      sys_menu / sys_role_menu 上撞唯一键而中止，因此这里只做增量，
--      并用 WHERE NOT EXISTS 保证可重复执行（幂等）。
--
-- 【增量内容（共 2 项）】
--   服务监控-服务查询：id 2031，父级 203，perms = monitor:server:query
--   缓存监控-缓存查询：id 2041，父级 204，perms = monitor:cache:query
--   对应 sys_role_menu 授权（超级管理员 role_id=1，tenant_id=0）共 2 条
--
-- 【安全性论证】
--   1. 幂等：全部使用 INSERT ... SELECT ... WHERE NOT EXISTS，
--      重复执行是 0 行变更，既不报错也不产生重复数据。
--   2. 唯一键无冲突：已实测 id 2031/2041 未被占用，perms 也未被占用
--      （见下方【执行前必做的只读核查】，两条 count 均应返回 0）。
--   3. 父菜单均已存在：203（服务监控）、204（缓存监控）是既有菜单行，
--      parent_id=2（系统监控目录）。
--   4. 无序列依赖：sys_menu 的 id 由应用雪花算法（ASSIGN_ID）生成，
--      pg_get_serial_sequence 返回 NULL，不存在序列撞值。
--   5. 只做 INSERT，不涉及任何 DELETE / UPDATE；本脚本也不写入
--      缓存监控所需的 Redis 数据（那是运行时行为，与 DB 无关）。
--
-- 【执行前必做的只读核查】（在 psql / JDBC 客户端执行，确认后再跑本脚本）
--   -- a. 确认 2 个 id 均未被占用（应返回 0）
--   SELECT count(*) FROM "baize-system".sys_menu WHERE id IN (2031, 2041);
--   -- b. 确认 2 个 perms 均未被占用（应返回 0）
--   SELECT count(*) FROM "baize-system".sys_menu
--    WHERE perms IN ('monitor:server:query','monitor:cache:query');
--   -- c. 确认父菜单存在（应返回 2）
--   SELECT count(*) FROM "baize-system".sys_menu WHERE id IN (203, 204);
--   -- d. 确认目标授权关系不存在（应返回 0）
--   SELECT count(*) FROM "baize-system".sys_role_menu
--    WHERE role_id = 1 AND menu_id IN (2031, 2041);
--
-- 【执行方式】
--   psql 或任意 JDBC 客户端整文件执行。建议先在事务中执行并核对影响行数：
--     BEGIN;
--     \i V2026_10_25_01__monitor_query_perms.sql
--     SELECT id, menu_name, perms FROM "baize-system".sys_menu WHERE id IN (2031, 2041);  -- 预期 2 行
--     COMMIT;
-- =============================================================================

CREATE SCHEMA IF NOT EXISTS "baize-system";

-- -----------------------------------------------------------------------------
-- 1. 两个查询按钮（id 2031 / 2041）
--    列顺序与 sys_menu 表结构一致：
--    id, tenant_id, parent_id, menu_name, path, component, parameters, external,
--    cacheable, menu_type, visible, icon, perms, sort, status, deleted, "version",
--    created_by, created_time, modified_by, modified_time, remark
--    menu_type='2' 表示按钮（非菜单），visible='0' 表示不在侧边栏展示
-- -----------------------------------------------------------------------------
INSERT INTO "baize-system".sys_menu
    (id, tenant_id, parent_id, menu_name, path, component, parameters, external,
     cacheable, menu_type, visible, icon, perms, sort, status, deleted, "version",
     created_by, created_time, modified_by, modified_time, remark)
SELECT v.id, 0, v.parent_id, v.menu_name, '', NULL, NULL, '0',
       '0', '2', '0', '#', v.perms, 1, '0', '0', 1,
       1, '2025-08-11 17:33:07.797', NULL, NULL, NULL
FROM (VALUES
    (2031, 203, '服务查询', 'monitor:server:query'),
    (2041, 204, '缓存查询', 'monitor:cache:query')
) AS v(id, parent_id, menu_name, perms)
WHERE NOT EXISTS (SELECT 1 FROM "baize-system".sys_menu WHERE id = v.id)
  AND NOT EXISTS (SELECT 1 FROM "baize-system".sys_menu WHERE perms = v.perms AND deleted = '0');

-- -----------------------------------------------------------------------------
-- 2. 授权给超级管理员（role_id=1，tenant_id=0）
--    与 sys_role_menu 的 (tenant_id, role_id, menu_id) 列顺序一致
-- -----------------------------------------------------------------------------
INSERT INTO "baize-system".sys_role_menu (tenant_id, role_id, menu_id)
SELECT 0, 1, v.menu_id
FROM (VALUES
    (2031),
    (2041)
) AS v(menu_id)
WHERE NOT EXISTS (
    SELECT 1 FROM "baize-system".sys_role_menu
     WHERE tenant_id = 0 AND role_id = 1 AND menu_id = v.menu_id
);

-- -----------------------------------------------------------------------------
-- 3. 收尾自检（只读，按需执行）
-- -----------------------------------------------------------------------------
-- SELECT id, parent_id, menu_name, perms, menu_type FROM "baize-system".sys_menu
--  WHERE id IN (2031, 2041) ORDER BY id;
-- -- 预期 2 行：
-- --   2031 | 203 | 服务查询 | monitor:server:query | 2
-- --   2041 | 204 | 缓存查询 | monitor:cache:query  | 2
-- SELECT count(*) FROM "baize-system".sys_role_menu
--  WHERE tenant_id = 0 AND role_id = 1 AND menu_id IN (2031, 2041);
-- -- 预期 2
