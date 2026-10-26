-- =============================================================================
-- V2026_10_22_03__relation_table_pk.sql
-- 目标库：PostgreSQL（18.6）；Schema："baize-system"（带连字符，必须双引号）
--
-- 【这个脚本解决什么】
--   sys_role_menu / sys_role_perm / sys_user_role 三张关系表历史上"只有唯一键、没有主键"：
--     DDL 见 db/ddl/baize-system-ddl.sql:585 / :618 / :818
--       uk_sys_role_menu_tenant_role_menu UNIQUE (tenant_id, role_id, menu_id)
--       uk_sys_role_perm_tenant_role_perm UNIQUE (tenant_id, role_id, perm_id)
--       uk_sys_user_role_tenant_user_role UNIQUE (tenant_id, user_id, role_id)
--   上一版 V2026_10_22__constraints_and_align.sql 的 S7 明确记录了不补的原因：
--     "ALTER TABLE ... ADD PRIMARY KEY USING INDEX uk_xxx 在 PG 上直接报
--      index uk_xxx is already associated with a constraint"。
--   即：唯一约束"拥有"它的索引，USING INDEX 不能直接借用。
--   本脚本给出正解——先解除约束对索引的拥有关系（DROP CONSTRAINT 会连带删掉它自己的索引），
--   再用同名普通唯一索引接管，最后 ADD PRIMARY KEY USING INDEX 把它提升为主键。
--   结果：这三张表既有复合主键语义，又仍然只有**一个**索引（不产生重复索引、不放大写入）。
--
-- 【为什么不直接 ADD CONSTRAINT PRIMARY KEY (cols)】
--   那样会在同一组列上再建一个一模一样的索引，与既有 uk 索引重复（写入放大 + 磁盘浪费），
--   且 uk 约束名保留、PK 另起一名，两套索引长期并存。故不采用；仅在表上没有同名唯一键时
--   才走这条兜底路径（见下方 DO 块分支）。
--
-- 【为什么不引入外键】
--   当前 DDL 全库 0 个 FOREIGN KEY（已 grep 确认），关系表与主表之间无级联约束，
--   这是既有设计。本脚本只补主键，不外扩影响面。
--
-- 【对应用代码无影响】
--   SysRoleMenu / SysRolePerm / SysUserRole 实体继承 BaseRelationEntity，
--   该类只有 tenant_id 一个字段、无 @TableId（见 BaseRelationEntity.java:19-26），
--   MyBatis-Plus 不依赖主键做任何 CRUD；加主键不改变 SQL 行为。
--   逻辑删除插件对这三张表无效（无 deleted 列，与既有结论一致）。
--
-- 【幂等性】
--   每个表一个独立 DO 块；进入后先查 pg_constraint：
--     * 已有主键（contype='p'）        -> NOTICE 跳过；
--     * 目标列存在 NULL / 非 NOT NULL   -> NOTICE 跳过（PG 主键列必须 NOT NULL）；
--     * 目标列组合存在重复行            -> NOTICE 跳过（理论上唯一键已保证，此处防回归）。
--   因此重复执行第二次全部命中"已有主键"直接跳过，结果与首次执行后一致。--
-- 【安全性】
--   * 唯一的破坏性动作是 DROP CONSTRAINT（丢的是唯一约束，不是数据）；
--   * DROP / CREATE INDEX / ADD PRIMARY KEY 三步全部包在嵌套 BEGIN..EXCEPTION 里，
--     任一步失败 => 该表的子事务整体回滚，唯一约束原样恢复，脚本继续处理下一张表；
--   * PostgreSQL 的 DDL 是事务性的，DO 块的异常处理器会回滚到块起点，不会留半截状态；
--   * 每个 DO 块自身即一个原子单位：即使外层没有显式事务、以 JDBC 批量逐条执行，
--     也是"要么这张表完整改完，要么这张表完全没改"，不会出现丢了 uk 又没建成 pk 的中间态；
--   * 不使用 DROP TABLE / TRUNCATE / DELETE，不动任何一行数据。
--
-- 【执行方式】psql 或 JDBC 客户端整文件执行。建议先按文末"只读体检"段核对现状。
--   验证可回滚：BEGIN; <本文件>; 再执行一遍; 查 pg_constraint; ROLLBACK;
-- =============================================================================

DO $$
DECLARE
    v_spec        text;
    v_table       text;
    v_cols        text;
    v_cols_arr    text[];
    v_reg         regclass;
    v_conname     text;
    v_has_pk      boolean;
    v_cols_ok     boolean;
    v_dups        bigint;
BEGIN
    -- 表名 | 主键列（顺序与 DDL 中 uk 定义一致）
    FOREACH v_spec IN ARRAY ARRAY[
        'sys_role_menu|tenant_id,role_id,menu_id',
        'sys_role_perm|tenant_id,role_id,perm_id',
        'sys_user_role|tenant_id,user_id,role_id'
    ] LOOP
        v_table    := split_part(v_spec, '|', 1);
        v_cols     := split_part(v_spec, '|', 2);
        v_cols_arr := string_to_array(v_cols, ',');

        v_reg := to_regclass(format('%I.%I', 'baize-system', v_table));
        IF v_reg IS NULL THEN
            RAISE NOTICE '跳过（表不存在）：%', v_table;
            CONTINUE;
        END IF;

        -- 1) 已有主键 => 幂等跳过
        SELECT EXISTS (SELECT 1 FROM pg_constraint
                        WHERE conrelid = v_reg AND contype = 'p') INTO v_has_pk;
        IF v_has_pk THEN
            RAISE NOTICE '跳过（已有主键）：%', v_table;
            CONTINUE;
        END IF;

        -- 2) 目标列必须全部存在且 NOT NULL（PG 主键列不允许 NULL）
        SELECT NOT EXISTS (
            SELECT 1
              FROM unnest(v_cols_arr) AS c(col)
              LEFT JOIN pg_attribute a
                     ON a.attrelid = v_reg AND a.attname = c.col AND NOT a.attisdropped
             WHERE a.attname IS NULL OR NOT a.attnotnull
        ) INTO v_cols_ok;
        IF NOT v_cols_ok THEN
            RAISE NOTICE '跳过（目标列缺失或可为 NULL，PG 主键列必须 NOT NULL）：% (%)', v_table, v_cols;
            CONTINUE;
        END IF;

        -- 3) 判重（唯一约束理论上已保证无重复，这里是防回归的硬闸门）
        EXECUTE format('SELECT count(*) FROM (
                            SELECT 1 FROM %I.%I GROUP BY %s HAVING count(*) > 1) d',
                       'baize-system', v_table, v_cols) INTO v_dups;
        IF v_dups > 0 THEN
            RAISE NOTICE '跳过（存在 % 组重复 %，请先清洗数据）：%', v_dups, v_cols, v_table;
            CONTINUE;
        END IF;

        -- 4) 按"列集合"（而非约束名）找既有唯一约束，兼容历史命名不一致
        SELECT con.conname INTO v_conname
          FROM pg_constraint con
         WHERE con.conrelid = v_reg
           AND con.contype = 'u'
           AND (SELECT array_agg(a.attname::text ORDER BY a.attname)
                  FROM unnest(con.conkey) AS k(attnum)
                  JOIN pg_attribute a
                    ON a.attrelid = con.conrelid AND a.attnum = k.attnum)
             = (SELECT array_agg(x ORDER BY x) FROM unnest(v_cols_arr) AS x);

        IF v_conname IS NOT NULL THEN
            -- 正解路径：解除约束对索引的拥有关系 -> 同名唯一索引接管 -> 提升为主键
            BEGIN
                EXECUTE format('ALTER TABLE %I.%I DROP CONSTRAINT %I',
                               'baize-system', v_table, v_conname);
                EXECUTE format('CREATE UNIQUE INDEX %I ON %I.%I (%s)',
                               v_conname, 'baize-system', v_table, v_cols);
                EXECUTE format('ALTER TABLE %I.%I ADD PRIMARY KEY USING INDEX %I',
                               'baize-system', v_table, v_conname);
                RAISE NOTICE '已补复合主键（保留唯一索引与原名 %）：% (%)', v_conname, v_table, v_cols;
            EXCEPTION WHEN others THEN
                RAISE NOTICE '补主键失败（本表变更已整体回滚，唯一约束不受影响）：% -> %',
                             v_table, SQLERRM;
            END;
        ELSE
            -- 兜底路径：表上没有对应唯一键时，直接建主键（会新建一个索引）
            BEGIN
                EXECUTE format('ALTER TABLE %I.%I ADD CONSTRAINT %I PRIMARY KEY (%s)',
                               'baize-system', v_table, 'pk_' || v_table, v_cols);
                RAISE NOTICE '已补复合主键（兜底路径，新建索引）：pk_% (%)', v_table, v_cols;
            EXCEPTION WHEN others THEN
                RAISE NOTICE '补主键失败（已跳过）：% -> %', v_table, SQLERRM;
            END;
        END IF;
    END LOOP;
END $$;

-- =============================================================================
-- 【只读体检】执行本脚本前后各跑一次，对比结果（全部为 SELECT，不产生任何变更）
--
--   1) 三张表主键/唯一约束现状（执行前应只有 contype='u'，执行后应变成 contype='p'）：
--      SELECT conrelid::regclass::text AS tbl, conname, contype
--        FROM pg_constraint
--       WHERE connamespace = '"baize-system"'::regnamespace
--         AND conrelid::regclass::text IN ('"baize-system".sys_role_menu',
--                                          '"baize-system".sys_role_perm',
--                                          '"baize-system".sys_user_role')
--       ORDER BY 1, 2;
--
--   2) 确认索引没有重复（每张表应仍只有 1 个覆盖主键列的索引 + 1 个 tenant_id 单列索引）：
--      SELECT tablename, indexname, indexdef
--        FROM pg_indexes
--       WHERE schemaname = 'baize-system'
--         AND tablename IN ('sys_role_menu', 'sys_role_perm', 'sys_user_role')
--       ORDER BY 1, 2;
--
--   3) 主键列 NULL 检查（应全部为 0，否则第 2 步守卫会跳过建键）：
--      SELECT count(*) FILTER (WHERE tenant_id IS NULL) AS t_null,
--             count(*) FILTER (WHERE role_id   IS NULL) AS r_null,
--             count(*) FILTER (WHERE menu_id   IS NULL) AS m_null
--        FROM "baize-system".sys_role_menu;
--
--   4) 重复行检查（应返回 0 行）：
--      SELECT tenant_id, role_id, menu_id, count(*)
--        FROM "baize-system".sys_role_menu GROUP BY 1,2,3 HAVING count(*) > 1;
-- =============================================================================

-- =============================================================================
-- 【回滚】如需撤销（仅当确认没有依赖该主键的下游对象时）：
--   ALTER TABLE "baize-system".sys_role_menu DROP CONSTRAINT uk_sys_role_menu_tenant_role_menu;
--   ALTER TABLE "baize-system".sys_role_menu ADD CONSTRAINT uk_sys_role_menu_tenant_role_menu
--       UNIQUE (tenant_id, role_id, menu_id);
--   -- sys_role_perm / sys_user_role 同理，约束名分别替换为
--   --   uk_sys_role_perm_tenant_role_perm / uk_sys_user_role_tenant_user_role
-- =============================================================================
