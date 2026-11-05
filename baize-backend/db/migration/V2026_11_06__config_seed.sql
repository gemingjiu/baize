-- =============================================================================
-- V2026_11_06__config_seed.sql
--
-- 用途：落地 2026-11-05 系统管理功能补全 P2——参数配置首批消费参数种子。
--       此前 sys_config 全系统 0 个读取方，参数只能增删改查；本次实现
--       getConfigValueByKey（租户自定义 → 平台兜底合并解析）并接入两个消费方：
--         1. sys.user.initPassword       创建用户未显式指定密码时的初始密码
--                                       （SysUserServiceImpl#create 消费，缺失则拒绝创建）
--         2. sys.account.captchaEnabled  登录验证码开关（baize-auth 消费，'false'=关闭；
--                                       P5 验证码能力已实现，打开此开关即生效——
--                                       前置条件：网关白名单放行 /api/v1/auth/captcha*）
--
-- 【种子约定】tenant_id = 平台租户（nil UUID）作为全租户兜底；config_type='Y' 系统内置；
--   ID 段 N=4201..4202（hex16 映射约定：N=4201 → 0x1069 → 00000000-0000-7000-0000-000000001069），
--   与字典种子段 4001..4140 无冲突。
--
-- 【幂等性】INSERT ... ON CONFLICT DO NOTHING（裸形态，容忍 id 与
--   uk_sys_config_tenant_config_key 双情形冲突——存量库若已有同键业务行则跳过种子行）；
--   重复执行 0 行变更。
--
-- 【ON CONFLICT 仲裁联动】无新增唯一键；本批不触碰 V2026_11_03 已转 partial 的
--   uk_sys_config_tenant_config_key。
--
-- 【执行方式】
--   psql -v ON_ERROR_STOP=1 -f 本文件。
--
-- 【执行后自检】
--   SELECT config_key, config_value, config_type FROM "baize-system".sys_config
--    WHERE config_key IN ('sys.user.initPassword','sys.account.captchaEnabled');
--   -- 预期 2 行
-- =============================================================================

CREATE SCHEMA IF NOT EXISTS "baize-system";

INSERT INTO "baize-system".sys_config
    (id, tenant_id, config_name, config_key, config_value, config_type, sort, status, deleted, "version", created_by, created_time, remark)
VALUES
    ('00000000-0000-7000-0000-000000001069', '00000000-0000-0000-0000-000000000000',
     '用户初始密码', 'sys.user.initPassword', 'Baize@123456', 'Y', 1, '0', '0', 0,
     '00000000-0000-7000-0000-000000000001', now(),
     '创建用户未显式指定密码时的初始密码（P2：创建链路消费；修改后对新建用户即时生效）'),
    ('00000000-0000-7000-0000-00000000106a', '00000000-0000-0000-0000-000000000000',
     '登录验证码开关', 'sys.account.captchaEnabled', 'false', 'Y', 2, '0', '0', 0,
     '00000000-0000-7000-0000-000000000001', now(),
     '登录页图形验证码开关（P5：auth 登录链路消费；开启前需在网关白名单放行 /api/v1/auth/captcha*）')
ON CONFLICT DO NOTHING;