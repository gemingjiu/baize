-- =============================================================================
-- V2026_10_22__constraints_and_align.sql
-- 目标库：PostgreSQL（实测 throwaway 探针连的是 PostgreSQL 18.6）
-- Schema：所有对象在 "baize-system"（注意：schema 名带连字符，必须加双引号；URL 里的 baize 是库名，不存在 baize-gen schema）
--
-- 【这个脚本解决什么】
--   1) 实体 ↔ 库结构对齐：把 BaseEntity(BaseRelationEntity 之外) 的公共列在每张业务表上补齐/收紧，
--      消除"实体有列、库缺列"与"库可空、实体是原生 int/Long"这两类历史遗留问题。
--   2) 索引补齐：按实际 Mapper 查询条件补复合索引（tenant_id 打头，to 让多租户拦截器下推后仍命中索引）。
--   3) 约束补齐：线上 20 张表 **没有任何主键**（已用 information_schema/pg_constraint 探针确认 pk_count=0），
--      其中 sys_oper_log / sys_login_log 连唯一约束都没有。本脚本对 15 张带 id 的表逐个"判重后再补主键"，
--      对关系表补复合主键，对 sys_dept 补业务唯一键。
--
-- 【幂等性】所有语句都可重复执行：
--   * 加列 / 加索引：ADD COLUMN IF NOT EXISTS、CREATE INDEX IF NOT EXISTS；
--   * 主键 / 唯一键：DO 块内先查 pg_constraint 是否已有、再查候选键是否有 NULL/重复，命中任何一种就跳过；
--     真正的 ALTER TABLE ... ADD CONSTRAINT 还额外包了 EXCEPTION 兜底，失败只 NOTICE、不中断脚本；
--   * DEFAULT / NOT NULL：SET DEFAULT 本身幂等；SET NOT NULL 前先用 pg_attribute.attnotnull 判断；
--   * 空值回填：UPDATE ... WHERE col IS NULL，重复执行为 0 行。
--
-- 【安全说明】不使用 DROP / TRUNCATE / DELETE 任何对象或数据；只做 ADD / SET / UPDATE(仅 NULL 回填)。
--   在存量库上执行前，建议先跑文件末尾"前置只读体检"段，确认没有重复 id / 重复业务键。
--
-- 【执行方式】psql 或任意 JDBC 客户端把整个文件作为一批执行（本仓库无 psql 时用单文件 Java + 42.3.5 驱动）。
--   验证语法/幂等性不要直接提交：BEGIN; <整个脚本>; 再执行一遍; 查询 pg_constraint 校验; ROLLBACK;
--   PostgreSQL 的 DDL 是事务性的，ROLLBACK 能把建索引/补主键/回填全部撤销，线上不会留痕。
-- =============================================================================

CREATE SCHEMA IF NOT EXISTS "baize-system";

-- =============================================================================
-- S1. 公共列补齐（ADD COLUMN IF NOT EXISTS）
--     对齐 BaseEntity：id / tenant_id / sort / version / status / deleted /
--     created_by / created_time / modified_by / modified_time / remark
--     注意：所在库实际列名是 created_by/created_time/modified_by/modified_time + deleted，
--     不是 create_by/update_time/del_flag（Java 侧也没有 del_flag，全库 grep 为 0 命中）。
--     这里只"新增缺失列"，不存在的列按可空/带默认值加，避免历史的 NULL 审计值被伪造。
-- =============================================================================

ALTER TABLE "baize-system".sys_config
    ADD COLUMN IF NOT EXISTS tenant_id varchar(36)         NULL,
    ADD COLUMN IF NOT EXISTS sort          int4         NULL,
    ADD COLUMN IF NOT EXISTS version       int4         NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS status        varchar(1)   NOT NULL DEFAULT '0',
    ADD COLUMN IF NOT EXISTS deleted       varchar(1)   NOT NULL DEFAULT '0',
    ADD COLUMN IF NOT EXISTS created_by varchar(36)         NULL,
    ADD COLUMN IF NOT EXISTS created_time  timestamp    NULL,
    ADD COLUMN IF NOT EXISTS modified_by varchar(36)         NULL,
    ADD COLUMN IF NOT EXISTS modified_time timestamp    NULL,
    ADD COLUMN IF NOT EXISTS remark        varchar(255) NULL;

ALTER TABLE "baize-system".sys_data_perm
    ADD COLUMN IF NOT EXISTS tenant_id varchar(36)         NULL,
    ADD COLUMN IF NOT EXISTS sort          int4         NULL,
    ADD COLUMN IF NOT EXISTS version       int4         NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS status        varchar(1)   NOT NULL DEFAULT '0',
    ADD COLUMN IF NOT EXISTS deleted       varchar(1)   NOT NULL DEFAULT '0',
    ADD COLUMN IF NOT EXISTS created_by varchar(36)         NULL,
    ADD COLUMN IF NOT EXISTS created_time  timestamp    NULL,
    ADD COLUMN IF NOT EXISTS modified_by varchar(36)         NULL,
    ADD COLUMN IF NOT EXISTS modified_time timestamp    NULL,
    ADD COLUMN IF NOT EXISTS remark        varchar(255) NULL;

ALTER TABLE "baize-system".sys_dept
    ADD COLUMN IF NOT EXISTS tenant_id varchar(36)         NULL,
    ADD COLUMN IF NOT EXISTS sort          int4         NULL,
    ADD COLUMN IF NOT EXISTS version       int4         NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS status        varchar(1)   NOT NULL DEFAULT '0',
    ADD COLUMN IF NOT EXISTS deleted       varchar(1)   NOT NULL DEFAULT '0',
    ADD COLUMN IF NOT EXISTS created_by varchar(36)         NULL,
    ADD COLUMN IF NOT EXISTS created_time  timestamp    NULL,
    ADD COLUMN IF NOT EXISTS modified_by varchar(36)         NULL,
    ADD COLUMN IF NOT EXISTS modified_time timestamp    NULL,
    ADD COLUMN IF NOT EXISTS remark        varchar(255) NULL;

ALTER TABLE "baize-system".sys_dict_data
    ADD COLUMN IF NOT EXISTS tenant_id varchar(36)         NULL,
    ADD COLUMN IF NOT EXISTS sort          int4         NULL,
    ADD COLUMN IF NOT EXISTS version       int4         NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS status        varchar(1)   NOT NULL DEFAULT '0',
    ADD COLUMN IF NOT EXISTS deleted       varchar(1)   NOT NULL DEFAULT '0',
    ADD COLUMN IF NOT EXISTS created_by varchar(36)         NULL,
    ADD COLUMN IF NOT EXISTS created_time  timestamp    NULL,
    ADD COLUMN IF NOT EXISTS modified_by varchar(36)         NULL,
    ADD COLUMN IF NOT EXISTS modified_time timestamp    NULL,
    ADD COLUMN IF NOT EXISTS remark        varchar(255) NULL;

ALTER TABLE "baize-system".sys_dict_type
    ADD COLUMN IF NOT EXISTS tenant_id varchar(36)         NULL,
    ADD COLUMN IF NOT EXISTS sort          int4         NULL,
    ADD COLUMN IF NOT EXISTS version       int4         NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS status        varchar(1)   NOT NULL DEFAULT '0',
    ADD COLUMN IF NOT EXISTS deleted       varchar(1)   NOT NULL DEFAULT '0',
    ADD COLUMN IF NOT EXISTS created_by varchar(36)         NULL,
    ADD COLUMN IF NOT EXISTS created_time  timestamp    NULL,
    ADD COLUMN IF NOT EXISTS modified_by varchar(36)         NULL,
    ADD COLUMN IF NOT EXISTS modified_time timestamp    NULL,
    ADD COLUMN IF NOT EXISTS remark        varchar(255) NULL;

ALTER TABLE "baize-system".sys_job
    ADD COLUMN IF NOT EXISTS tenant_id varchar(36)         NULL,
    ADD COLUMN IF NOT EXISTS sort          int4         NULL,
    ADD COLUMN IF NOT EXISTS version       int4         NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS status        varchar(1)   NOT NULL DEFAULT '0',
    ADD COLUMN IF NOT EXISTS deleted       varchar(1)   NOT NULL DEFAULT '0',
    ADD COLUMN IF NOT EXISTS created_by varchar(36)         NULL,
    ADD COLUMN IF NOT EXISTS created_time  timestamp    NULL,
    ADD COLUMN IF NOT EXISTS modified_by varchar(36)         NULL,
    ADD COLUMN IF NOT EXISTS modified_time timestamp    NULL,
    ADD COLUMN IF NOT EXISTS remark        varchar(255) NULL;

ALTER TABLE "baize-system".sys_menu
    ADD COLUMN IF NOT EXISTS tenant_id varchar(36)         NULL,
    ADD COLUMN IF NOT EXISTS sort          int4         NULL,
    ADD COLUMN IF NOT EXISTS version       int4         NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS status        varchar(1)   NOT NULL DEFAULT '0',
    ADD COLUMN IF NOT EXISTS deleted       varchar(1)   NOT NULL DEFAULT '0',
    ADD COLUMN IF NOT EXISTS created_by varchar(36)         NULL,
    ADD COLUMN IF NOT EXISTS created_time  timestamp    NULL,
    ADD COLUMN IF NOT EXISTS modified_by varchar(36)         NULL,
    ADD COLUMN IF NOT EXISTS modified_time timestamp    NULL,
    ADD COLUMN IF NOT EXISTS remark        varchar(255) NULL;

ALTER TABLE "baize-system".sys_notice
    ADD COLUMN IF NOT EXISTS tenant_id varchar(36)         NULL,
    ADD COLUMN IF NOT EXISTS sort          int4         NULL,
    ADD COLUMN IF NOT EXISTS version       int4         NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS status        varchar(1)   NOT NULL DEFAULT '0',
    ADD COLUMN IF NOT EXISTS deleted       varchar(1)   NOT NULL DEFAULT '0',
    ADD COLUMN IF NOT EXISTS created_by varchar(36)         NULL,
    ADD COLUMN IF NOT EXISTS created_time  timestamp    NULL,
    ADD COLUMN IF NOT EXISTS modified_by varchar(36)         NULL,
    ADD COLUMN IF NOT EXISTS modified_time timestamp    NULL,
    ADD COLUMN IF NOT EXISTS remark        varchar(255) NULL;

ALTER TABLE "baize-system".sys_perm
    ADD COLUMN IF NOT EXISTS tenant_id varchar(36)         NULL,
    ADD COLUMN IF NOT EXISTS sort          int4         NULL,
    ADD COLUMN IF NOT EXISTS version       int4         NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS status        varchar(1)   NOT NULL DEFAULT '0',
    ADD COLUMN IF NOT EXISTS deleted       varchar(1)   NOT NULL DEFAULT '0',
    ADD COLUMN IF NOT EXISTS created_by varchar(36)         NULL,
    ADD COLUMN IF NOT EXISTS created_time  timestamp    NULL,
    ADD COLUMN IF NOT EXISTS modified_by varchar(36)         NULL,
    ADD COLUMN IF NOT EXISTS modified_time timestamp    NULL,
    ADD COLUMN IF NOT EXISTS remark        varchar(255) NULL;

ALTER TABLE "baize-system".sys_post
    ADD COLUMN IF NOT EXISTS tenant_id varchar(36)         NULL,
    ADD COLUMN IF NOT EXISTS sort          int4         NULL,
    ADD COLUMN IF NOT EXISTS version       int4         NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS status        varchar(1)   NOT NULL DEFAULT '0',
    ADD COLUMN IF NOT EXISTS deleted       varchar(1)   NOT NULL DEFAULT '0',
    ADD COLUMN IF NOT EXISTS created_by varchar(36)         NULL,
    ADD COLUMN IF NOT EXISTS created_time  timestamp    NULL,
    ADD COLUMN IF NOT EXISTS modified_by varchar(36)         NULL,
    ADD COLUMN IF NOT EXISTS modified_time timestamp    NULL,
    ADD COLUMN IF NOT EXISTS remark        varchar(255) NULL;

ALTER TABLE "baize-system".sys_role
    ADD COLUMN IF NOT EXISTS tenant_id varchar(36)         NULL,
    ADD COLUMN IF NOT EXISTS sort          int4         NULL,
    ADD COLUMN IF NOT EXISTS version       int4         NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS status        varchar(1)   NOT NULL DEFAULT '0',
    ADD COLUMN IF NOT EXISTS deleted       varchar(1)   NOT NULL DEFAULT '0',
    ADD COLUMN IF NOT EXISTS created_by varchar(36)         NULL,
    ADD COLUMN IF NOT EXISTS created_time  timestamp    NULL,
    ADD COLUMN IF NOT EXISTS modified_by varchar(36)         NULL,
    ADD COLUMN IF NOT EXISTS modified_time timestamp    NULL,
    ADD COLUMN IF NOT EXISTS remark        varchar(255) NULL;

ALTER TABLE "baize-system".sys_user
    ADD COLUMN IF NOT EXISTS tenant_id varchar(36)         NULL,
    ADD COLUMN IF NOT EXISTS sort          int4         NULL,
    ADD COLUMN IF NOT EXISTS version       int4         NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS status        varchar(1)   NOT NULL DEFAULT '0',
    ADD COLUMN IF NOT EXISTS deleted       varchar(1)   NOT NULL DEFAULT '0',
    ADD COLUMN IF NOT EXISTS created_by varchar(36)         NULL,
    ADD COLUMN IF NOT EXISTS created_time  timestamp    NULL,
    ADD COLUMN IF NOT EXISTS modified_by varchar(36)         NULL,
    ADD COLUMN IF NOT EXISTS modified_time timestamp    NULL,
    ADD COLUMN IF NOT EXISTS remark        varchar(255) NULL;

-- sys_tenant 是租户元数据表本身：没有 tenant_id 列（实体 SysTenant 已用 @TableField(exist=false) 屏蔽），
-- CustomTenantHandler 也把 sys_tenant 放进 IGNORED_TABLES，故此处只补其余公共列。
ALTER TABLE "baize-system".sys_tenant
    ADD COLUMN IF NOT EXISTS sort          int4         NULL,
    ADD COLUMN IF NOT EXISTS version       int4         NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS status        varchar(1)   NOT NULL DEFAULT '0',
    ADD COLUMN IF NOT EXISTS deleted       varchar(1)   NOT NULL DEFAULT '0',
    ADD COLUMN IF NOT EXISTS created_by varchar(36)         NULL,
    ADD COLUMN IF NOT EXISTS created_time  timestamp    NULL,
    ADD COLUMN IF NOT EXISTS modified_by varchar(36)         NULL,
    ADD COLUMN IF NOT EXISTS modified_time timestamp    NULL,
    ADD COLUMN IF NOT EXISTS remark        varchar(255) NULL;

-- 日志表（实体不继承 BaseEntity，只有 id/tenant_id 是公共列；其余为日志自有列）
ALTER TABLE "baize-system".sys_oper_log
    ADD COLUMN IF NOT EXISTS tenant_id varchar(36)         NULL,
    ADD COLUMN IF NOT EXISTS business_type int4         DEFAULT 0 NULL,
    ADD COLUMN IF NOT EXISTS operator_type int4         DEFAULT 0 NULL,
    ADD COLUMN IF NOT EXISTS status        int4         DEFAULT 0 NULL,
    ADD COLUMN IF NOT EXISTS cost_time     int8         DEFAULT 0 NULL;

ALTER TABLE "baize-system".sys_login_log
    ADD COLUMN IF NOT EXISTS tenant_id varchar(36)         NULL,
    ADD COLUMN IF NOT EXISTS status        varchar(1)   NOT NULL DEFAULT '0';

-- =============================================================================
-- S2. 公共列收紧（空值回填 -> SET DEFAULT -> SET NOT NULL）
--     根因：实体侧 sort 是原生 int（BaseEntity:23 `private int sort`），
--     列上只要有一行 NULL，Mapper 映射阶段就会抛
--     "Error attempting to get column 'sort' ... (null)"; version 是 @Version Long，
--     乐观锁 SQL 形如 WHERE version = ?，NULL 会让更新直接静默失效。
--     现状：线上 12 张表 sort 均无 NULL（已探针确认 null_sort=0），此处是防回归。
--     说明：tenant_id 只回填 + 收紧 NOT NULL，**不设 DEFAULT**（留 DEFAULT 0 会掩盖漏传租户写入）。
-- =============================================================================

DO $$
DECLARE
    v_table   text;
    v_col     text;
    v_fill    text;
    v_default boolean;
    v_reg     regclass;
BEGIN
    FOREACH v_table IN ARRAY ARRAY[
        'sys_config', 'sys_data_perm', 'sys_dept', 'sys_dict_data', 'sys_dict_type',
        'sys_job', 'sys_menu', 'sys_notice', 'sys_perm', 'sys_post', 'sys_role',
        'sys_tenant', 'sys_user'
    ] LOOP
        v_reg := to_regclass(format('%I.%I', 'baize-system', v_table));
        IF v_reg IS NULL THEN
            RAISE NOTICE 'S2 跳过（表不存在）：%', v_table;
            CONTINUE;
        END IF;

        FOR v_col, v_fill, v_default IN
            SELECT * FROM (VALUES
                ('tenant_id', '0',    false),
                ('sort',      '0',    true),
                ('version',   '0',    true),
                ('status',    '''0''', true),
                ('deleted',   '''0''', true)
            ) AS t(c, f, d)
        LOOP
            -- 列不存在（如 sys_tenant 没有 tenant_id、sys_oper_log 没有 sort）直接跳过
            IF NOT EXISTS (SELECT 1 FROM pg_attribute
                            WHERE attrelid = v_reg AND attname = v_col AND NOT attisdropped) THEN
                CONTINUE;
            END IF;

            EXECUTE format('UPDATE %I.%I SET %I = %s WHERE %I IS NULL',
                           'baize-system', v_table, v_col, v_fill, v_col);

            IF v_default THEN
                EXECUTE format('ALTER TABLE %I.%I ALTER COLUMN %I SET DEFAULT %s',
                               'baize-system', v_table, v_col, v_fill);
            END IF;

            IF NOT EXISTS (SELECT 1 FROM pg_attribute
                            WHERE attrelid = v_reg AND attname = v_col AND attnotnull) THEN
                EXECUTE format('ALTER TABLE %I.%I ALTER COLUMN %I SET NOT NULL',
                               'baize-system', v_table, v_col);
                RAISE NOTICE 'S2 已收紧 NOT NULL：%.%', v_table, v_col;
            END IF;
        END LOOP;
    END LOOP;
END $$;

-- 日志表不继承 BaseEntity，只有 tenant_id 需要同样收紧（status 在日志表是 int4 业务状态，不动）
DO $$
DECLARE
    v_table text;
    v_reg   regclass;
BEGIN
    FOREACH v_table IN ARRAY ARRAY['sys_oper_log', 'sys_login_log'] LOOP
        v_reg := to_regclass(format('%I.%I', 'baize-system', v_table));
        IF v_reg IS NULL THEN
            RAISE NOTICE 'S2 跳过（表不存在）：%', v_table;
            CONTINUE;
        END IF;
        IF NOT EXISTS (SELECT 1 FROM pg_attribute
                        WHERE attrelid = v_reg AND attname = 'tenant_id' AND NOT attisdropped) THEN
            CONTINUE;
        END IF;
        EXECUTE format('UPDATE %I.%I SET tenant_id = ''00000000-0000-0000-0000-000000000000'' WHERE tenant_id IS NULL',
                       'baize-system', v_table);
        IF NOT EXISTS (SELECT 1 FROM pg_attribute
                        WHERE attrelid = v_reg AND attname = 'tenant_id' AND attnotnull) THEN
            EXECUTE format('ALTER TABLE %I.%I ALTER COLUMN tenant_id SET NOT NULL',
                           'baize-system', v_table);
            RAISE NOTICE 'S2 已收紧 NOT NULL：%.tenant_id', v_table;
        END IF;
    END LOOP;
END $$;

-- =============================================================================
-- S3. 索引补齐（按真实 Mapper 查询条件，全部 tenant_id 打头）
--     已有的单列 tenant_id 索引保留不动（低选择性但无害），这里只补复合索引。
--     依据（文件:行）：
--       sys_menu       SysMenuServiceImpl:91   eq(parent_id) + orderBy sort + 租户过滤
--       sys_dept       SysSysDeptServiceImpl:73 eq(parent_id)（删除前判子节点）
--       sys_perm       SysPermServiceImpl:91   eq(parent_id)
--       sys_user       SysUserServiceImpl:98   eq(phone)
--       sys_user_role  SysUserRoleServiceImpl:36/69 eq/in(role_id)
--                      （user_id 方向已被 uk_sys_user_role_tenant_user_role 覆盖）
--       sys_oper_log   SysOperLogServiceImpl:30  orderByDesc(oper_time) + 租户过滤
--       sys_login_log  SysLoginLogServiceImpl:31 orderByDesc(login_time) + 租户过滤
--       sys_notice     SysNoticeServiceImpl:59   orderByDesc(created_time) + 租户过滤
--     sys_role_menu / sys_role_perm 按 role_id 查：已被 uk(tenant_id,role_id,xxx) 前缀覆盖，不重复建。
-- =============================================================================

CREATE INDEX IF NOT EXISTS idx_sys_menu_tenant_parent_id
    ON "baize-system".sys_menu (tenant_id, parent_id);

CREATE INDEX IF NOT EXISTS idx_sys_dept_tenant_parent_id
    ON "baize-system".sys_dept (tenant_id, parent_id);

CREATE INDEX IF NOT EXISTS idx_sys_perm_tenant_parent_id
    ON "baize-system".sys_perm (tenant_id, parent_id);

CREATE INDEX IF NOT EXISTS idx_sys_user_tenant_phone
    ON "baize-system".sys_user (tenant_id, phone);

CREATE INDEX IF NOT EXISTS idx_sys_user_role_tenant_role_id
    ON "baize-system".sys_user_role (tenant_id, role_id);

CREATE INDEX IF NOT EXISTS idx_sys_oper_log_tenant_oper_time
    ON "baize-system".sys_oper_log (tenant_id, oper_time DESC);

CREATE INDEX IF NOT EXISTS idx_sys_login_log_tenant_login_time
    ON "baize-system".sys_login_log (tenant_id, login_time DESC);

CREATE INDEX IF NOT EXISTS idx_sys_notice_tenant_created_time
    ON "baize-system".sys_notice (tenant_id, created_time DESC);

-- =============================================================================
-- S4. 业务唯一键补齐（DO 块先判重，有重复就跳过并 NOTICE）
--     sys_dept 现在只有三个普通索引，没有 (tenant_id,parent_id,dept_name) 唯一键；
--     同一父节点下可插入同名部门，而兄弟表 sys_menu 已有 uk(tenant_id,menu_name,parent_id)。
-- =============================================================================

DO $$
DECLARE
    v_reg  regclass;
    v_dups bigint;
BEGIN
    v_reg := to_regclass(format('%I.%I', 'baize-system', 'sys_dept'));
    IF v_reg IS NULL THEN
        RAISE NOTICE 'S4 跳过（sys_dept 不存在）';
    ELSIF EXISTS (SELECT 1 FROM pg_constraint
                   WHERE conrelid = v_reg AND conname = 'uk_sys_dept_tenant_parent_dept_name') THEN
        RAISE NOTICE 'S4 跳过（唯一键已存在）：uk_sys_dept_tenant_parent_dept_name';
    ELSE
        EXECUTE 'SELECT count(*) FROM (
                   SELECT 1 FROM "baize-system".sys_dept
                    GROUP BY tenant_id, parent_id, dept_name
                   HAVING count(*) > 1) d' INTO v_dups;
        IF v_dups > 0 THEN
            RAISE NOTICE 'S4 跳过：sys_dept 存在 % 组重复 (tenant_id,parent_id,dept_name)，请先清洗数据', v_dups;
        ELSE
            BEGIN
                ALTER TABLE "baize-system".sys_dept
                    ADD CONSTRAINT uk_sys_dept_tenant_parent_dept_name
                    UNIQUE (tenant_id, parent_id, dept_name);
                RAISE NOTICE 'S4 已补唯一键：uk_sys_dept_tenant_parent_dept_name(tenant_id,parent_id,dept_name)';
            EXCEPTION WHEN others THEN
                RAISE NOTICE 'S4 补唯一键失败（已跳过）：%', SQLERRM;
            END;
        END IF;
    END IF;
END $$;

-- =============================================================================
-- S5. 线上 sys_oper_log 主键（重点：该表当前确实没有主键）
--     实测依据（只读探针）：
--       pg_constraint 中 sys_oper_log 只有 2 个 NOT NULL CHECK 约束 + 4 个普通索引，
--       contype='p' 的行数为 0；全库 20 张表 pk_count 都是 0，uk_count=13。
--       db/ddl/baize-system-ddl.sql:846 与 db/init/baize-system-operlog.sql:28 已声明
--       CONSTRAINT pk_sys_oper_log PRIMARY KEY (id)，但线上库是旧结构，未同步。
--     安全策略：先统计 行数 / 去重 id 数 / NULL id 数，任何一项不合规就只 NOTICE 不落 DDL；
--               真正 ALTER 再包一层 EXCEPTION，异常（并发写入、锁冲突等）也只是跳过。
-- =============================================================================

DO $$
DECLARE
    v_reg      regclass := to_regclass('"baize-system".sys_oper_log');
    v_total    bigint;
    v_distinct bigint;
    v_nulls    bigint;
BEGIN
    IF v_reg IS NULL THEN
        RAISE NOTICE 'S5 跳过（sys_oper_log 不存在）';
        RETURN;
    END IF;

    IF EXISTS (SELECT 1 FROM pg_constraint WHERE conrelid = v_reg AND contype = 'p') THEN
        RAISE NOTICE 'S5 跳过（sys_oper_log 已有主键）';
        RETURN;
    END IF;

    EXECUTE 'SELECT count(*), count(DISTINCT id), count(*) FILTER (WHERE id IS NULL)
               FROM "baize-system".sys_oper_log' INTO v_total, v_distinct, v_nulls;
    RAISE NOTICE 'S5 sys_oper_log 体检：行数=%，去重 id=%，NULL id=%', v_total, v_distinct, v_nulls;

    IF v_nulls > 0 OR v_total <> v_distinct THEN
        RAISE NOTICE 'S5 跳过：sys_oper_log.id 存在 NULL 或重复值，请先清洗后重跑本脚本';
        RETURN;
    END IF;

    BEGIN
        ALTER TABLE "baize-system".sys_oper_log
            ADD CONSTRAINT pk_sys_oper_log PRIMARY KEY (id);
        RAISE NOTICE 'S5 已补主键：pk_sys_oper_log(id)';
    EXCEPTION WHEN others THEN
        RAISE NOTICE 'S5 补主键失败（已跳过，不影响后续步骤）：%', SQLERRM;
    END;
END $$;

-- 登录日志同形问题：同样没有主键（探针 pk_count=0），且比 oper_log 更早开始积数据（现 10 行）。
DO $$
DECLARE
    v_reg      regclass := to_regclass('"baize-system".sys_login_log');
    v_total    bigint;
    v_distinct bigint;
    v_nulls    bigint;
BEGIN
    IF v_reg IS NULL THEN
        RAISE NOTICE 'S5 跳过（sys_login_log 不存在）';
        RETURN;
    END IF;
    IF EXISTS (SELECT 1 FROM pg_constraint WHERE conrelid = v_reg AND contype = 'p') THEN
        RAISE NOTICE 'S5 跳过（sys_login_log 已有主键）';
        RETURN;
    END IF;

    EXECUTE 'SELECT count(*), count(DISTINCT id), count(*) FILTER (WHERE id IS NULL)
               FROM "baize-system".sys_login_log' INTO v_total, v_distinct, v_nulls;
    RAISE NOTICE 'S5 sys_login_log 体检：行数=%，去重 id=%，NULL id=%', v_total, v_distinct, v_nulls;

    IF v_nulls > 0 OR v_total <> v_distinct THEN
        RAISE NOTICE 'S5 跳过：sys_login_log.id 存在 NULL 或重复值，请先清洗后重跑本脚本';
        RETURN;
    END IF;

    BEGIN
        ALTER TABLE "baize-system".sys_login_log
            ADD CONSTRAINT pk_sys_login_log PRIMARY KEY (id);
        RAISE NOTICE 'S5 已补主键：pk_sys_login_log(id)';
    EXCEPTION WHEN others THEN
        RAISE NOTICE 'S5 补主键失败（已跳过）：%', SQLERRM;
    END;
END $$;

-- =============================================================================
-- S6. 其余 13 张带 id 的表补主键（DO 块逐表判重后补，命名统一 pk_<表名>）
--     跳过条件：表不存在 / 已有主键 / id 有 NULL / id 有重复 / ALTER 抛异常。
-- =============================================================================

DO $$
DECLARE
    v_table text;
    v_reg   regclass;
    v_dups  bigint;
    v_nulls bigint;
BEGIN
    FOREACH v_table IN ARRAY ARRAY[
        'sys_config', 'sys_data_perm', 'sys_dept', 'sys_dict_data', 'sys_dict_type',
        'sys_job', 'sys_menu', 'sys_notice', 'sys_perm', 'sys_post', 'sys_role',
        'sys_tenant', 'sys_user'
    ] LOOP
        v_reg := to_regclass(format('%I.%I', 'baize-system', v_table));
        IF v_reg IS NULL THEN
            RAISE NOTICE 'S6 跳过（表不存在）：%', v_table;
            CONTINUE;
        END IF;
        IF EXISTS (SELECT 1 FROM pg_constraint WHERE conrelid = v_reg AND contype = 'p') THEN
            RAISE NOTICE 'S6 跳过（已有主键）：%', v_table;
            CONTINUE;
        END IF;

        EXECUTE format('SELECT count(*) - count(DISTINCT id),
                               count(*) FILTER (WHERE id IS NULL)
                          FROM %I.%I', 'baize-system', v_table) INTO v_dups, v_nulls;
        IF v_nulls > 0 OR v_dups > 0 THEN
            RAISE NOTICE 'S6 跳过：%.id 存在 % 行 NULL、% 行重复，请先清洗', v_table, v_nulls, v_dups;
            CONTINUE;
        END IF;

        BEGIN
            EXECUTE format('ALTER TABLE %I.%I ADD CONSTRAINT %I PRIMARY KEY (id)',
                           'baize-system', v_table, 'pk_' || v_table);
            RAISE NOTICE 'S6 已补主键：pk_%(id)', v_table;
        EXCEPTION WHEN others THEN
            RAISE NOTICE 'S6 补主键失败（已跳过）：% -> %', v_table, SQLERRM;
        END;
    END LOOP;
END $$;

-- =============================================================================
-- S7. 关系表键约束
--     sys_user_role / sys_role_menu / sys_role_perm 已有 uk(tenant_id, 双方 id)：
--     唯一约束 + 唯一索引已经是等价主键，**不再重复补主键**——
--     实测 `ALTER TABLE ... ADD PRIMARY KEY USING INDEX uk_xxx` 在 PG 上直接报
--     "index uk_xxx is already associated with a constraint"，而在同一组列上再建主键
--     只会在同一列上多出一个一模一样的索引，纯属浪费（写入放大）。
--     sys_role_dept / sys_user_post 既无主键也无唯一键（重复关联可以直接插进去），
--     这里补复合主键把重复关掉；有重复时先判重跳过。
--     判重方式：按"约束列集合"匹配，而不是按约束名匹配，避免历史命名不一致导致重复补键。
-- =============================================================================

DO $$
DECLARE
    v_spec  text;
    v_table text;
    v_cols  text;
    v_kind  text;
    v_reg   regclass;
    v_dups  bigint;
    v_has   boolean;
BEGIN
    FOREACH v_spec IN ARRAY ARRAY[
        -- 表名 | 键列 | 期望约束类型（uk=唯一约束即可，pk=必须有主键）
        'sys_user_role|tenant_id,user_id,role_id|uk',
        'sys_role_menu|tenant_id,role_id,menu_id|uk',
        'sys_role_perm|tenant_id,role_id,perm_id|uk',
        'sys_role_dept|tenant_id,role_id,dept_id|pk',
        'sys_user_post|tenant_id,user_id,post_id|pk'
    ] LOOP
        v_table := split_part(v_spec, '|', 1);
        v_cols  := split_part(v_spec, '|', 2);
        v_kind  := split_part(v_spec, '|', 3);

        v_reg := to_regclass(format('%I.%I', 'baize-system', v_table));
        IF v_reg IS NULL THEN
            RAISE NOTICE 'S7 跳过（表不存在）：%', v_table;
            CONTINUE;
        END IF;

        -- 已有主键；或(期望 uk 时)已有同列集合的唯一约束 -> 跳过
        SELECT EXISTS (
            SELECT 1
              FROM pg_constraint con
             WHERE con.conrelid = v_reg
               AND (con.contype = 'p' OR (v_kind = 'uk' AND con.contype = 'u'))
               AND (SELECT array_agg(a.attname::text ORDER BY a.attname)
                      FROM unnest(con.conkey) AS k(attnum)
                      JOIN pg_attribute a
                        ON a.attrelid = con.conrelid AND a.attnum = k.attnum)
                 = (SELECT array_agg(x ORDER BY x)
                      FROM unnest(string_to_array(v_cols, ',')) AS x)
        ) INTO v_has;
        IF v_has THEN
            RAISE NOTICE 'S7 跳过（% 已有键约束：%）', v_table, v_cols;
            CONTINUE;
        END IF;

        BEGIN
            EXECUTE format('SELECT count(*) FROM (SELECT 1 FROM %I.%I GROUP BY %s HAVING count(*) > 1) d',
                           'baize-system', v_table, v_cols) INTO v_dups;
            IF v_dups > 0 THEN
                RAISE NOTICE 'S7 跳过：% 存在 % 组重复 (%).', v_table, v_dups, v_cols;
            ELSIF v_kind = 'pk' THEN
                EXECUTE format('ALTER TABLE %I.%I ADD CONSTRAINT %I PRIMARY KEY (%s)',
                               'baize-system', v_table, 'pk_' || v_table, v_cols);
                RAISE NOTICE 'S7 已补主键：pk_%（%）', v_table, v_cols;
            ELSE
                EXECUTE format('ALTER TABLE %I.%I ADD CONSTRAINT %I UNIQUE (%s)',
                               'baize-system', v_table, 'uk_' || v_table, v_cols);
                RAISE NOTICE 'S7 已补唯一键：uk_%（%）', v_table, v_cols;
            END IF;
        EXCEPTION WHEN others THEN
            RAISE NOTICE 'S7 补键失败（已跳过）：% -> %', v_table, SQLERRM;
        END;
    END LOOP;
END $$;

-- =============================================================================
-- S8. 收尾自检（只读，不产生变更）：按需在客户端执行查看结果
--   SELECT conrelid::regclass::text AS tbl, conname, contype
--     FROM pg_constraint
--    WHERE connamespace = '"baize-system"'::regnamespace AND contype IN ('p','u')
--    ORDER BY 1,2;
--   SELECT tablename, indexname FROM pg_indexes
--    WHERE schemaname = 'baize-system' ORDER BY 1,2;
-- =============================================================================

-- =============================================================================
-- 【前置只读体检（在真库上执行本脚本之前建议先跑，全部为 SELECT，不产生任何变更）】
--   1) 主键/唯一约束现状：
--      SELECT conrelid::regclass, conname, contype FROM pg_constraint
--       WHERE connamespace = '"baize-system"'::regnamespace AND contype IN ('p','u');
--   2) 补主键前的重复检查：
--      SELECT count(*), count(DISTINCT id) FROM "baize-system".sys_oper_log;
--   3) 实体原生 int 列的空值检查（S2 会回填）：
--      SELECT count(*) FROM "baize-system".sys_user WHERE sort IS NULL;
--   4) 唯一键候选重复检查（S4/S7 会判重）：
--      SELECT tenant_id, parent_id, dept_name, count(*) FROM "baize-system".sys_dept
--       GROUP BY 1,2,3 HAVING count(*) > 1;
-- =============================================================================
