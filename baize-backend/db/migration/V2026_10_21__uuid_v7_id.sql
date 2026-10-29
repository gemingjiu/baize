-- ============================================================================
-- 主键类型迁移：bigint → varchar(36)（UUIDv7 主键）
--
-- 背景：实体主键统一改为 java.lang.String + MP IdType.ASSIGN_UUID，由
--       Uuidv7IdentifierGenerator 生成 36 位带连字符的 UUIDv7 字符串；
--       对应 DDL（db/ddl/baize-system-ddl.sql）已同步为 varchar(36)，
--       本迁移用于把存量库的 id 类列转换过来。
--
-- 幂等：仅当列仍为 bigint 时才执行转换，重复运行自动跳过；
--       转换语句依赖 information_schema 过滤，新装库（已是 varchar）直接跳过。
--
-- 值映射（必须与 db/init/baize-system-init.sql 的种子字面量完全一致，
-- 存量库迁移后与重新灌种子得到同一批主键，ON CONFLICT 才能对齐）：
--   tenant_id = 0  → '00000000-0000-0000-0000-000000000000'（平台租户 nil UUID，
--                    与 CustomTenantHandler / TenantFilter 的平台租户常量一致）
--   parent_id = 0  → '0'（树根哨兵保留原义，TreeUtils/SysRoleMenuServiceImpl
--                    等按 parentId='0' 判根，不改成 UUID）
--   其余 id 类列 N → '00000000-0000-7000-' || 16 位小写 hex(N) 的前 4 位 || '-'
--                    || 后 12 位
--                    例：1    → '00000000-0000-7000-0000-000000000001'
--                        100  → '00000000-0000-7000-0000-000000000064'
--                        2115370221412851714（雪花存量）→ '00000000-0000-7000-1xxx-xxxxxxxxxxxx'
--
-- 注意：
--   * cost_time（消耗时间毫秒）是业务数值列，不是 id，保持 bigint 不动。
--   * 本映射是确定性单射；映射值的时间位全 0，不会与运行期生成的真 UUIDv7
--     （时间位为当前毫秒）冲突。
--   * 手工执行：psql -v ON_ERROR_STOP=1 -f 本文件
-- ============================================================================

DO $uuid_v7$
DECLARE
    col    record;
    zero_v text;
    expr   text;
BEGIN
    FOR col IN
        SELECT table_name, column_name
        FROM information_schema.columns
        WHERE table_schema = 'baize-system'
          AND data_type = 'bigint'
          AND column_name IN ('id', 'tenant_id', 'parent_id', 'created_by', 'modified_by',
                              'dept_id', 'user_id', 'role_id', 'menu_id', 'perm_id', 'post_id')
        ORDER BY table_name, column_name
    LOOP
        IF col.column_name = 'parent_id' THEN
            zero_v := '''0''';
        ELSE
            zero_v := '''00000000-0000-0000-0000-000000000000''';
        END IF;

        expr := format(
            'CASE WHEN %1$I = 0 THEN %2$s ELSE %3$L || substr(lpad(to_hex(%1$I), 16, ''0''), 1, 4) || ''-'' || substr(lpad(to_hex(%1$I), 16, ''0''), 5, 12) END',
            col.column_name,
            zero_v,
            '00000000-0000-7000-'
        );

        EXECUTE format(
            'ALTER TABLE "baize-system".%I ALTER COLUMN %I TYPE varchar(36) USING %s',
            col.table_name, col.column_name, expr
        );
    END LOOP;
END
$uuid_v7$;
