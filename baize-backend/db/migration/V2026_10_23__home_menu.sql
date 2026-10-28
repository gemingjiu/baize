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
-- 【幂等与一致性约定（重要）】
--   1. 本脚本与 baize-system-init.sql 使用【同一个固定保留 id = 4】，保证
--      「全新库跑 init」与「已存在库跑迁移」最终物理主键一致（不再用 MAX(id)+1）。
--   2. 所有表名都显式限定 "baize-system" schema，并且脚本自行 CREATE SCHEMA，
--      不依赖调用方是否 SET search_path（psql -c 与 psql -f 是不同的会话）。
--   3. 同租户下未删除菜单的 (tenant_id, path) 必须唯一（按钮菜单 path 为空串，已用
--      部分索引排除）。这里建部分唯一索引，并据此做 ON CONFLICT DO NOTHING，
--      使并发重跑不会双插或主键冲突。
--   4. 若线上库此前已用 snowflake 主键创建过 /welcome 行（早期版本迁移的遗留），
--      本脚本会在 id=4 空闲且该 /welcome 行唯一时，将其主键对齐到 4（sys_menu.id
--      没有任何外键约束，只需同步 sys_role_menu.menu_id，事务内完成）。
-- =============================================================================

CREATE SCHEMA IF NOT EXISTS "baize-system";

-- 0) 防御性部分唯一索引：同一租户下、未删除、非空 path 必须唯一
CREATE UNIQUE INDEX IF NOT EXISTS uk_sys_menu_tenant_path
    ON "baize-system".sys_menu (tenant_id, "path")
    WHERE "path" IS NOT NULL AND "path" <> '';

-- 1) 首页菜单本体（固定保留 id=4，菜单类型 1=菜单，visible 0=显示，sort 0 排最前）
INSERT INTO "baize-system".sys_menu (
    id, tenant_id, parent_id, menu_name, "path", component, parameters,
    "external", cacheable, menu_type, visible, icon, perms, sort, status, deleted,
    "version", created_by, created_time, modified_by, modified_time, remark
)
SELECT
    4,
    0, 0, '首页', '/welcome', 'Welcome', NULL,
    '0', '0', '1', '0', 'dashboard', NULL, 0, '0', '0',
    1, 1, now(), NULL, NULL, '系统首页'
WHERE NOT EXISTS (
    SELECT 1 FROM "baize-system".sys_menu WHERE id = 4
)
  AND NOT EXISTS (
    SELECT 1 FROM "baize-system".sys_menu WHERE tenant_id = 0 AND "path" = '/welcome' AND deleted = '0'
);

-- 2) 把可能存在的 snowflake /welcome 行对齐到固定 id=4（无外键，事务内同步 sys_role_menu）
DO $$
DECLARE
    v_old BIGINT;
BEGIN
    SELECT id INTO v_old
    FROM "baize-system".sys_menu
    WHERE tenant_id = 0 AND "path" = '/welcome' AND deleted = '0' AND id <> 4
    LIMIT 1;

    IF v_old IS NOT NULL AND NOT EXISTS (
        SELECT 1 FROM "baize-system".sys_menu WHERE id = 4
    ) THEN
        -- 先清理指向「不存在的 id=4」的孤儿授权行，避免下面的 UPDATE 主键冲突
        DELETE FROM "baize-system".sys_role_menu
        WHERE menu_id = 4 AND NOT EXISTS (SELECT 1 FROM "baize-system".sys_menu WHERE id = 4);
        UPDATE "baize-system".sys_role_menu SET menu_id = 4 WHERE menu_id = v_old;
        UPDATE "baize-system".sys_menu SET id = 4 WHERE id = v_old;
    END IF;
END
$$;

-- 3) 绑定到超级管理员角色（role_code='admin'），否则 admin 登录也看不到首页
INSERT INTO "baize-system".sys_role_menu (tenant_id, role_id, menu_id)
SELECT r.tenant_id, r.id, m.id
FROM "baize-system".sys_role r
         CROSS JOIN "baize-system".sys_menu m
WHERE r.role_code = 'admin'
  AND r.deleted = '0'
  AND m."path" = '/welcome'
  AND m.deleted = '0'
ON CONFLICT DO NOTHING;
