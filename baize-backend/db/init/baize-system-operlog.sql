-- 操作日志表（PostgreSQL 版）
-- 与 db/ddl/baize-system-ddl.sql 中 "baize-system".sys_oper_log 保持一致；
-- 全脚本幂等：表 / 索引 / 注释已存在时重复执行不报错。
-- 原文件是 MySQL 方言（反引号、TINYINT、DATETIME、COMMENT 内联、ENGINE=InnoDB、KEY 索引），
-- 在 PostgreSQL 上会直接语法报错，故整体改写。

CREATE SCHEMA IF NOT EXISTS "baize-system";

CREATE TABLE IF NOT EXISTS "baize-system".sys_oper_log (
    id            int8         NOT NULL,       -- 日志主键
    tenant_id     int8         NOT NULL,       -- 租户ID
    title         varchar(50)  NULL,           -- 模块标题
    business_type int4         DEFAULT 0 NULL, -- 业务类型（0其它 1新增 2修改 3删除 4查询 5导出 6导入 7强退 8生成代码 9清空数据）
    method        varchar(100) NULL,           -- 方法名称
    request_method varchar(10) NULL,           -- 请求方式
    operator_type int4         DEFAULT 0 NULL, -- 操作类别（0其它 1后台用户 2手机端用户）
    user_name     varchar(50)  NULL,           -- 操作人员
    dept_name     varchar(50)  NULL,           -- 部门名称
    oper_url      varchar(255) NULL,           -- 请求URL
    oper_ip       varchar(128) NULL,           -- 主机地址
    oper_location varchar(255) NULL,           -- 操作地点
    oper_param    text         NULL,           -- 请求参数
    json_result   text         NULL,           -- 返回参数
    status        int4         DEFAULT 0 NULL, -- 操作状态（0正常 1异常）
    error_msg     text         NULL,           -- 错误消息
    oper_time     timestamp    NULL,           -- 操作时间
    cost_time     int8         DEFAULT 0 NULL, -- 消耗时间（毫秒）
    CONSTRAINT pk_sys_oper_log PRIMARY KEY (id)
);

COMMENT ON TABLE "baize-system".sys_oper_log IS '操作日志记录表';

COMMENT ON COLUMN "baize-system".sys_oper_log.id IS '日志主键';
COMMENT ON COLUMN "baize-system".sys_oper_log.tenant_id IS '租户ID';
COMMENT ON COLUMN "baize-system".sys_oper_log.title IS '模块标题';
COMMENT ON COLUMN "baize-system".sys_oper_log.business_type IS '业务类型（0其它 1新增 2修改 3删除 4查询 5导出 6导入 7强退 8生成代码 9清空数据）';
COMMENT ON COLUMN "baize-system".sys_oper_log.method IS '方法名称';
COMMENT ON COLUMN "baize-system".sys_oper_log.request_method IS '请求方式';
COMMENT ON COLUMN "baize-system".sys_oper_log.operator_type IS '操作类别（0其它 1后台用户 2手机端用户）';
COMMENT ON COLUMN "baize-system".sys_oper_log.user_name IS '操作人员';
COMMENT ON COLUMN "baize-system".sys_oper_log.dept_name IS '部门名称';
COMMENT ON COLUMN "baize-system".sys_oper_log.oper_url IS '请求URL';
COMMENT ON COLUMN "baize-system".sys_oper_log.oper_ip IS '主机地址';
COMMENT ON COLUMN "baize-system".sys_oper_log.oper_location IS '操作地点';
COMMENT ON COLUMN "baize-system".sys_oper_log.oper_param IS '请求参数';
COMMENT ON COLUMN "baize-system".sys_oper_log.json_result IS '返回参数';
COMMENT ON COLUMN "baize-system".sys_oper_log.status IS '操作状态（0正常 1异常）';
COMMENT ON COLUMN "baize-system".sys_oper_log.error_msg IS '错误消息';
COMMENT ON COLUMN "baize-system".sys_oper_log.oper_time IS '操作时间';
COMMENT ON COLUMN "baize-system".sys_oper_log.cost_time IS '消耗时间（毫秒）';

CREATE INDEX IF NOT EXISTS idx_sys_oper_log_tenant_id
    ON "baize-system".sys_oper_log (tenant_id);
CREATE INDEX IF NOT EXISTS idx_sys_oper_log_business_type
    ON "baize-system".sys_oper_log (business_type);
CREATE INDEX IF NOT EXISTS idx_sys_oper_log_status
    ON "baize-system".sys_oper_log (status);
CREATE INDEX IF NOT EXISTS idx_sys_oper_log_oper_time
    ON "baize-system".sys_oper_log (oper_time);

-- 存量库补主键（新建库已由上方的 CONSTRAINT 建好，此处仅对历史库生效）：
-- 若目标库缺少主键，先确认无重复 id 再执行下面两行。
-- ALTER TABLE "baize-system".sys_oper_log ADD CONSTRAINT pk_sys_oper_log PRIMARY KEY (id);
