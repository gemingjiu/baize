-- =============================================================================
-- V2026_11_03__soft_delete_unique_index.sql
--
-- 用途：落地 2026-11-03 评审决策「软删除 × 唯一键冲突」——把带 deleted 列的业务表
--       的普通唯一约束/索引，改造为 partial unique index（WHERE deleted='0'），
--       解除「逻辑删除后唯一键永久占用」的约束。
--
-- 【背景】
--   业务表走逻辑删除（deleted 0/1），但唯一键是「全局」约束。逻辑删除某条记录后，
--   其编码（用户名/角色码/岗位码/字典值/参数键/任务名/租户码/域名/部门名等）仍被
--   唯一键占用，无法再次插入同编码的新记录，形成永久占用。
--   解法：唯一约束改为部分唯一索引（partial unique index）WHERE deleted='0'——
--   只对「未删除」记录强制唯一；逻辑删除（deleted='1'）的记录不进入索引，编码可复用。
--
-- 【改动的唯一键】（全部是带 deleted 列的业务表；关系表无 deleted 列不涉及）
--   1. sys_config    uk_sys_config_tenant_config_key         ("tenant_id","config_key")
--   2. sys_dept      uk_sys_dept_tenant_parent_dept_name   ("tenant_id","parent_id","dept_name")
--   3. sys_dict_data uk_sys_dict_data_tenant_type_value    ("tenant_id","dict_type","dict_value")
--   4. sys_dict_type uk_sys_dict_type_tenant_dict_type     ("tenant_id","dict_type")
--   5. sys_menu      uk_sys_menu_tenant_name_parent        ("tenant_id","menu_name","parent_id")
--   6. sys_perm      uk_sys_perm_tenant_perm_code          ("tenant_id","perm_code")
--   7. sys_post      uk_sys_post_tenant_post_code          ("tenant_id","post_code")
--   8. sys_role      uk_sys_role_tenant_role_code          ("tenant_id","role_code")
--   9. sys_tenant    uk_sys_tenant_tenant_code             ("tenant_code")        [约束]
--                    uk_sys_tenant_domain                  ("domain")             [索引，非约束]
--  10. sys_user      uk_sys_user_tenant_user_name          ("tenant_id","user_name")
--  11. sys_job       uk_sys_job_tenant_job_name            ("tenant_id","job_name")
--
-- 【ON CONFLICT 仲裁联动评估】
--   init 脚本对 sys_menu 用 ON CONFLICT (tenant_id,menu_name,parent_id) 仲裁（由本
--   uk 支撑）；其余种子表 ON CONFLICT (id) 走主键、关系表走各自 uk，均与本改动无关。
--   PostgreSQL 的 ON CONFLICT (cols) 可用「部分唯一索引」作仲裁索引：插入行不满足
--   WHERE 谓词（deleted='1'）时不判冲突直接落库（这正是想要的复用语义）；种子行
--   deleted='0' 时与既有活跃行判重，语义与改造前一致。因此 init/migration 的
--   ON CONFLICT 无需改动。
--
-- 【安全性论证】
--   1. 幂等：DO 块逐表先 DROP 约束/索引（判存在），再 CREATE UNIQUE INDEX
--      判 pg_class 存在），重跑 0 行变更。
--   2. 数据预检：DROP 与 CREATE 前都先统计「活跃(deleted='0')记录按唯一列分组重复组数」，
--      有重复组只 NOTICE 跳过、不落 DDL，避免建部分索引因存量重复活跃数据直接失败。
--   3. 索引名沿用原 uk_ 名：DROP CONSTRAINT / DROP INDEX 释放原名后，CREATE 同名
--      部分索引，无需改任何引用方（ON CONFLICT / 查询按列推断，与索引名无关）。
--   4. 全新库（ddl→init→migration）与存量库（仅 migration）殊途同归：DDL 仍声明
--      约束，但本脚本在所有迁移最后执行，最终态统一为部分唯一索引。
--   5. sys_tenant.domain 原本是唯一索引（非约束），本脚本统一转成部分索引，保持
--      「唯一域名」语义只对活跃租户成立。
--
-- 【执行方式】
--   psql -v ON_ERROR_STOP=1 -f 本文件。
--
-- 【执行后自检】
--   SELECT c.relname, pg_get_indexdef(i.indexrelid)
--     FROM pg_index i
--     JOIN pg_class c ON c.oid = i.indexrelid
--     JOIN pg_namespace n ON n.oid = c.relnamespace
--    WHERE n.nspname = 'baize-system' AND c.relname IN (
--      'uk_sys_config_tenant_config_key','uk_sys_dept_tenant_parent_dept_name',
--      'uk_sys_dict_data_tenant_type_value','uk_sys_dict_type_tenant_dict_type',
--      'uk_sys_menu_tenant_name_parent','uk_sys_perm_tenant_perm_code',
--      'uk_sys_post_tenant_post_code','uk_sys_role_tenant_role_code',
--      'uk_sys_tenant_tenant_code','uk_sys_tenant_domain',
--      'uk_sys_user_tenant_user_name','uk_sys_job_tenant_job_name');
--   -- 预期每行 indexdef 都以 "WHERE deleted" 结尾（部分唯一索引）
-- =============================================================================

CREATE SCHEMA IF NOT EXISTS "baize-system";

-- -----------------------------------------------------------------------------
-- 通用转换：对每个 (表, 唯一列, uk 名)：
--   ① 若 uk 是约束 → 判活跃重复 → DROP CONSTRAINT；
--   ② 若 uk 是索引且非「部分唯一」→ DROP INDEX（约束 DROP 后其名已让出）；
--   ③ 若 uk 索引不存在 → CREATE UNIQUE INDEX ... WHERE deleted='0'。
-- -----------------------------------------------------------------------------
DO $$
DECLARE
    v_t    text;
    v_cols text;
    v_uk   text;
    v_reg  regclass;
    v_dups bigint;
BEGIN
    FOR v_t, v_cols, v_uk IN
        SELECT * FROM (VALUES
            ('sys_config',    '"tenant_id","config_key"',              'uk_sys_config_tenant_config_key'),
            ('sys_dept',      '"tenant_id","parent_id","dept_name"',   'uk_sys_dept_tenant_parent_dept_name'),
            ('sys_dict_data', '"tenant_id","dict_type","dict_value"',  'uk_sys_dict_data_tenant_type_value'),
            ('sys_dict_type', '"tenant_id","dict_type"',               'uk_sys_dict_type_tenant_dict_type'),
            ('sys_menu',      '"tenant_id","menu_name","parent_id"',   'uk_sys_menu_tenant_name_parent'),
            ('sys_perm',      '"tenant_id","perm_code"',               'uk_sys_perm_tenant_perm_code'),
            ('sys_post',      '"tenant_id","post_code"',               'uk_sys_post_tenant_post_code'),
            ('sys_role',      '"tenant_id","role_code"',               'uk_sys_role_tenant_role_code'),
            ('sys_tenant',    '"tenant_code"',                         'uk_sys_tenant_tenant_code'),
            ('sys_tenant',    '"domain"',                              'uk_sys_tenant_domain'),
            ('sys_user',      '"tenant_id","user_name"',               'uk_sys_user_tenant_user_name'),
            ('sys_job',       '"tenant_id","job_name"',                'uk_sys_job_tenant_job_name')
        ) AS x(t, cols, uk)
    LOOP
        v_reg := to_regclass(format('%I.%I', 'baize-system', v_t));
        IF v_reg IS NULL THEN
            RAISE NOTICE 'SOFT_DELETE_UK 跳过（表不存在）：%', v_t;
            CONTINUE;
        END IF;

        -- ① 约束分支：存在 uk 约束时，先验活跃重复再 DROP
        IF EXISTS (SELECT 1 FROM pg_constraint
                    WHERE conrelid = v_reg AND conname = v_uk AND contype = 'u') THEN
            EXECUTE format('SELECT count(*) FROM (
                              SELECT 1 FROM %I.%I
                               WHERE deleted = ''0''
                               GROUP BY %s HAVING count(*) > 1) d',
                           'baize-system', v_t, v_cols) INTO v_dups;
            IF v_dups > 0 THEN
                RAISE NOTICE 'SOFT_DELETE_UK 跳过 DROP：% 存在 % 组活跃重复（%），请先清洗', v_uk, v_dups, v_cols;
            ELSE
                EXECUTE format('ALTER TABLE %I.%I DROP CONSTRAINT %I',
                               'baize-system', v_t, v_uk);
                RAISE NOTICE 'SOFT_DELETE_UK 已 DROP 约束：%', v_uk;
            END IF;
        END IF;

        -- ② 索引分支：若存在同名索引且非「部分唯一」→ DROP；已是部分唯一则保持
        IF EXISTS (SELECT 1 FROM pg_class c JOIN pg_namespace n ON n.oid = c.relnamespace
                    WHERE n.nspname = 'baize-system' AND c.relname = v_uk AND c.relkind = 'i') THEN
            IF NOT EXISTS (SELECT 1
                             FROM pg_index i
                             JOIN pg_class c ON c.oid = i.indexrelid
                             JOIN pg_namespace n ON n.oid = c.relnamespace
                            WHERE n.nspname = 'baize-system' AND c.relname = v_uk
                              AND i.indisunique AND i.indpred IS NOT NULL) THEN
                EXECUTE format('DROP INDEX %I.%I', 'baize-system', v_uk);
                RAISE NOTICE 'SOFT_DELETE_UK 已 DROP 非部分唯一索引：%', v_uk;
            ELSE
                RAISE NOTICE 'SOFT_DELETE_UK 保持（已是部分唯一）：%', v_uk;
                CONTINUE;
            END IF;
        END IF;

        -- ③ 建部分唯一索引（索引名已让出）；建前同样验活跃重复，避免失败
        IF NOT EXISTS (SELECT 1 FROM pg_class c JOIN pg_namespace n ON n.oid = c.relnamespace
                        WHERE n.nspname = 'baize-system' AND c.relname = v_uk AND c.relkind = 'i') THEN
            EXECUTE format('SELECT count(*) FROM (
                              SELECT 1 FROM %I.%I
                               WHERE deleted = ''0''
                               GROUP BY %s HAVING count(*) > 1) d',
                           'baize-system', v_t, v_cols) INTO v_dups;
            IF v_dups > 0 THEN
                RAISE NOTICE 'SOFT_DELETE_UK 跳过建索引：% 存在 % 组活跃重复（%），请先清洗', v_uk, v_dups, v_cols;
            ELSE
                EXECUTE format('CREATE UNIQUE INDEX %I ON %I.%I (%s) WHERE deleted = ''0''',
                               v_uk, 'baize-system', v_t, v_cols);
                RAISE NOTICE 'SOFT_DELETE_UK 已建部分唯一索引：%(%s)', v_uk, v_cols;
            END IF;
        END IF;
    END LOOP;
END $$;