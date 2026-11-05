-- =============================================================================
-- V2026_11_07__user_post_pk.sql
--
-- 用途：落地 2026-11-05 系统管理功能补全 P6——启用用户-岗位绑定（sys_user_post）。
--       该表自建库起只有 tenant_id/user_id/post_id 三列、无主键无唯一键
--       （2026-11-03 D3 决策"保留暂未实现"），本期启用前需补齐关系表标配的
--       uk + PK，防止重复绑定并支撑物理 DELETE 的精确语义。
--
-- 【实现方式】对齐 V2026_10_22_03__relation_table_pk 的三步法：
--   1. 先建普通唯一索引 uk_sys_user_post_tenant_user_post；
--   2. ADD PRIMARY KEY USING INDEX 把它提升为主键（避免直接 ADD CONSTRAINT
--      在存量重复行上硬失败——本表历史上无实体无写入，存量数据应为空；
--      若确有重复行，建索引失败只 NOTICE 跳过，请清洗后重跑）。
--   幂等：每步判存在，重复执行 0 变更。
--
-- 【ON CONFLICT 仲裁联动】无；本表不参与任何 ON CONFLICT 写入
--   （SysUserPostService 走先物理 DELETE 再 INSERT 的全量替换语义）。
--
-- 【执行方式】
--   psql -v ON_ERROR_STOP=1 -f 本文件。
--
-- 【执行后自检】
--   SELECT conname, contype FROM pg_constraint
--    WHERE conrelid = '"baize-system".sys_user_post'::regclass;
--   -- 预期 1 行：pk_sys_user_post（主键）
--   SELECT indexname FROM pg_indexes
--    WHERE schemaname='baize-system' AND tablename='sys_user_post';
--   -- 预期含 uk_sys_user_post_tenant_user_post（主键索引化后的名字）
-- =============================================================================

CREATE SCHEMA IF NOT EXISTS "baize-system";

-- 1. 建普通唯一索引（同名已存在或存量重复行建索引失败时跳过）
DO $$
DECLARE
    v_reg  regclass := to_regclass('"baize-system".sys_user_post');
    v_dups bigint;
BEGIN
    IF v_reg IS NULL THEN
        RAISE NOTICE 'USER_POST_PK 跳过（sys_user_post 不存在）';
        RETURN;
    END IF;
    IF EXISTS (SELECT 1 FROM pg_indexes
                WHERE schemaname = 'baize-system' AND tablename = 'sys_user_post'
                  AND indexname IN ('uk_sys_user_post_tenant_user_post', 'pk_sys_user_post')) THEN
        RAISE NOTICE 'USER_POST_PK 跳过（索引/主键已存在）';
        RETURN;
    END IF;
    EXECUTE 'SELECT count(*) FROM (
                SELECT 1 FROM "baize-system".sys_user_post
                 GROUP BY tenant_id, user_id, post_id HAVING count(*) > 1) d' INTO v_dups;
    IF v_dups > 0 THEN
        RAISE NOTICE 'USER_POST_PK 跳过：存在 % 组重复 (tenant_id,user_id,post_id)，请先清洗', v_dups;
        RETURN;
    END IF;
    EXECUTE 'CREATE UNIQUE INDEX uk_sys_user_post_tenant_user_post
               ON "baize-system".sys_user_post (tenant_id, user_id, post_id)';
    RAISE NOTICE 'USER_POST_PK 已建唯一索引 uk_sys_user_post_tenant_user_post';
END $$;

-- 2. 唯一索引提升为主键（PG 惯用法：ADD PRIMARY KEY USING INDEX）
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM pg_constraint
                WHERE conrelid = '"baize-system".sys_user_post'::regclass
                  AND contype = 'p') THEN
        RAISE NOTICE 'USER_POST_PK 跳过（已有主键）';
        RETURN;
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_indexes
                    WHERE schemaname = 'baize-system' AND tablename = 'sys_user_post'
                      AND indexname = 'uk_sys_user_post_tenant_user_post') THEN
        RAISE NOTICE 'USER_POST_PK 跳过（唯一索引不存在，主键无从提升）';
        RETURN;
    END IF;
    ALTER TABLE "baize-system".sys_user_post
        ADD CONSTRAINT pk_sys_user_post PRIMARY KEY USING INDEX uk_sys_user_post_tenant_user_post;
    RAISE NOTICE 'USER_POST_PK 已提升主键 pk_sys_user_post';
END $$;