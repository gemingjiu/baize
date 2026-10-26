-- =============================================================================
-- V2026_10_23__home_menu.sql
--
-- 用途：新增「首页」菜单（path=/welcome, component=Welcome, icon=dashboard），
--       让首页入口和其它菜单一样由后端 sys_menu 统一控制，而不是前端硬编码。
--
-- 【背景】
--   前端 config/routes.ts 里 `/welcome` 是一条前端静态路由，只负责「路由存在」；
--   菜单项、排序、显隐、授权本来都应该来自 sys_menu。
--   此前若依模板的种子数据里没有「首页」，前端便在 app.tsx 里合成一个 HOME_MENU 节点。
--   这会导致：菜单顺序/权限绕过后端、多租户下无法按租户差异化、导入导出菜单数据时丢首页。
--   现在改为在 sys_menu 里落一条正式记录，并把前端合成逻辑删掉。
--
-- 【为什么需要这个脚本】
--   baize-system-init.sql 是「全量种子」，已有库不能整体重跑（主键会撞车），
--   因此这里做增量同步，且用 NOT EXISTS 保证可重复执行。
--
--   主键取 MAX(id)+1 而不是写死 4：既有库里 id 已被占用的情况不可预期，
--   只有在「全新库刚跑完种子脚本」这种场景下才会恰好等于 4。
-- =============================================================================

-- 1) 首页菜单本体（菜单类型 1=菜单，visible 0=显示，sort 0 保证排在一级目录最前）
INSERT INTO sys_menu (
    id, tenant_id, parent_id, menu_name, "path", component, parameters,
    "external", cacheable, menu_type, visible, icon, perms, sort, status, deleted,
    "version", created_by, created_time, modified_by, modified_time, remark
)
SELECT
    (SELECT COALESCE(MAX(id), 0) + 1 FROM sys_menu),
    0, 0, '首页', '/welcome', 'Welcome', NULL,
    '0', '0', '1', '0', 'dashboard', NULL, 0, '0', '0',
    1, 1, now(), NULL, NULL, '系统首页'
WHERE NOT EXISTS (
    SELECT 1 FROM sys_menu WHERE "path" = '/welcome' AND deleted = '0'
);

-- 2) 绑定到超级管理员角色（role_code='admin'），否则 admin 登录也看不到首页
INSERT INTO sys_role_menu (tenant_id, role_id, menu_id)
SELECT r.tenant_id, r.id, m.id
FROM sys_role r
         CROSS JOIN sys_menu m
WHERE r.role_code = 'admin'
  AND r.deleted = '0'
  AND m."path" = '/welcome'
  AND m.deleted = '0'
  AND NOT EXISTS (
    SELECT 1
    FROM sys_role_menu rm
    WHERE rm.role_id = r.id
      AND rm.menu_id = m.id
);
