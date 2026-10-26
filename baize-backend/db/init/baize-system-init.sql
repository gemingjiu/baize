-- ============================================================================
-- 类型映射约定（请勿随意改动）：
--   DDL 中 id / tenant_id / created_by / modified_by 为 int8，而 Java 实体（BaseEntity、
--   BaseRelationEntity）中对应字段为 String；该 String↔int8 映射依赖 JDBC URL 上的
--   stringtype=unspecified 参数（见 doc/DEFAULT_GROUP/baize-system-dev.yml）。
--   version 为 int4、Java 侧为 Long，属隐式数值转换。调整列类型或移除该参数需同步评估。
-- ============================================================================

-- 租户数据
-- id=0 为平台级保留租户（平台态），归属 tenant_id=0 的数据（如 admin 用户、超级管理员角色、
-- 平台种子菜单/部门等）都挂在平台态下；CustomTenantHandler 在租户上下文缺失时兜底为 "0"，
-- 保证过滤条件始终可下推为 tenant_id=0（平台态隔离，而非跳过过滤）。
INSERT INTO sys_tenant (id, tenant_code, tenant_name, expire_time, contact_person, contact_phone, contact_email, "domain", max_user, sort, status, deleted, "version", created_by, created_time, modified_by, modified_time, remark)
VALUES(0, 'PLATFORM', '平台租户(保留)', NULL, NULL, NULL, NULL, NULL, 10, 0, '0', '0', 0, 1, '2026-10-23 16:55:00.000', NULL, NULL, '平台级保留租户，tenant_id=0 的数据归属平台态')
ON CONFLICT (id) DO NOTHING;

INSERT INTO sys_tenant (id, tenant_code, tenant_name, expire_time, contact_person, contact_phone, contact_email, "domain", max_user, sort, status, deleted, "version", created_by, created_time, modified_by, modified_time, remark)
VALUES(1, 'XIAOMI', '小米科技有限责任公司', '2025-07-17 16:51:06.290', '雷军', '18988888888', '18988888888@189.cn', 'xiaomi', 10, 1, '0', '0', 0, 1, '2025-07-17 16:51:06.290', 1, '2025-07-17 16:51:06.290', NULL)
ON CONFLICT (id) DO NOTHING;

INSERT INTO sys_tenant (id, tenant_code, tenant_name, expire_time, contact_person, contact_phone, contact_email, "domain", max_user, sort, status, deleted, "version", created_by, created_time, modified_by, modified_time, remark)
VALUES(2, 'HUAWEI', '华为科技有限责任公司', '2025-07-17 16:51:06.290', '大嘴', '18944441234', '18944441234@189.cn', 'huawei', 10, 2, '0', '0', 0, 1, '2025-07-17 16:51:06.290', 1, '2025-07-17 16:51:06.290', NULL)
ON CONFLICT (id) DO NOTHING;

INSERT INTO sys_tenant (id, tenant_code, tenant_name, expire_time, contact_person, contact_phone, contact_email, "domain", max_user, sort, status, deleted, "version", created_by, created_time, modified_by, modified_time, remark)
VALUES(3, 'ZIJIE', '字节跳动有限责任公司', '2025-07-17 16:51:06.290', '跳动', '18912341234', '18912341234@189.cn', 'zijie', 10, 3, '0', '0', 0, 1, '2025-07-17 16:51:06.290', 1, '2025-07-17 16:51:06.290', NULL)
ON CONFLICT (id) DO NOTHING;

INSERT INTO sys_tenant (id, tenant_code, tenant_name, expire_time, contact_person, contact_phone, contact_email, "domain", max_user, sort, status, deleted, "version", created_by, created_time, modified_by, modified_time, remark)
VALUES(4, 'ZTE', '中兴科技有限责任公司', '2025-07-17 16:51:06.290', '中兴', '18988888888', '18988888888@189.cn', 'zte', 10, 4, '0', '0', 0, 1, '2025-07-17 16:51:06.290', 1, '2025-07-17 16:51:06.290', NULL)
ON CONFLICT (id) DO NOTHING;

INSERT INTO sys_tenant (id, tenant_code, tenant_name, expire_time, contact_person, contact_phone, contact_email, "domain", max_user, sort, status, deleted, "version", created_by, created_time, modified_by, modified_time, remark)
VALUES(5, 'CXMT', '长鑫科技有限责任公司', '2025-07-25 16:48:10.000', '长鑫', '18988888888', '18988888888@189.cn', 'cxmt', 10, 5, '0', '0', 0, 1, '2025-07-25 16:48:10.654', NULL, '2025-07-25 16:48:10.655', NULL)
ON CONFLICT (id) DO NOTHING;


-- 部门数据（tenant_id=0 表示 master 租户
INSERT INTO sys_dept (id, tenant_id, parent_id, dept_name, leader, phone, email, sort, status, deleted, "version", created_by, created_time, modified_by, modified_time, remark)
VALUES(1, 0, 0, '总经理办公室', '雷军', '18988888888', '18988888888@189.cn', 1, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL)
ON CONFLICT (id) DO NOTHING;


-- ============================================================================
-- 菜单数据（tenant_id=0 平台/master 租户共享菜单）
-- ID 规划：
--   1-9       一级目录
--   100-199   系统管理子菜单（含按钮，按钮 ID > 1000）
--   200-299   系统监控子菜单
--   300-399   系统工具子菜单
-- menu_type: 0=目录, 1=菜单, 2=按钮
-- visible:   0=显示, 1=隐藏
-- external:  0=内链, 1=外链
-- cacheable: 0=不缓存, 1=缓存
-- ============================================================================

-- 一级目录
INSERT INTO sys_menu (id, tenant_id, parent_id, menu_name, path, component, parameters, external, cacheable, menu_type, visible, icon, perms, sort, status, deleted, "version", created_by, created_time, modified_by, modified_time, remark) VALUES
(4, 0, 0, '首页',     '/welcome', 'Welcome', NULL, '0', '0', '1', '0', 'dashboard', NULL,    0, '0', '0', 1, 1, '2026-10-23 14:20:00.000', NULL, NULL, '系统首页'),
(1, 0, 0, '系统管理', '/system', NULL, NULL, '0', '0', '0', '0', 'system',  NULL, 1, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, '系统管理目录'),
(2, 0, 0, '系统监控', '/monitor', NULL, NULL, '0', '0', '0', '0', 'monitor', NULL, 2, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, '系统监控目录'),
(3, 0, 0, '系统工具', '/tool', NULL, NULL, '0', '0', '0', '0', 'tool',     NULL, 3, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, '系统工具目录')
ON CONFLICT (tenant_id, menu_name, parent_id) DO NOTHING;

-- 系统管理子菜单
INSERT INTO sys_menu (id, tenant_id, parent_id, menu_name, path, component, parameters, external, cacheable, menu_type, visible, icon, perms, sort, status, deleted, "version", created_by, created_time, modified_by, modified_time, remark) VALUES
(100, 0, 1, '用户管理', 'user',   'system/user/index',   NULL, '0', '1', '1', '0', 'user',       'system:user:list',   1, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, '用户管理菜单'),
(101, 0, 1, '角色管理', 'role',   'system/role/index',   NULL, '0', '1', '1', '0', 'peoples',    'system:role:list',   2, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, '角色管理菜单'),
(102, 0, 1, '菜单管理', 'menu',   'system/menu/index',   NULL, '0', '1', '1', '0', 'tree-table', 'system:menu:list',   3, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, '菜单管理菜单'),
(103, 0, 1, '部门管理', 'dept',   'system/dept/index',   NULL, '0', '1', '1', '0', 'tree',       'system:dept:list',   4, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, '部门管理菜单'),
(104, 0, 1, '租户管理', 'tenant', 'system/tenant/index', NULL, '0', '1', '1', '0', 'international','system:tenant:list',5, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, '租户管理菜单'),
(105, 0, 1, '字典管理', 'dict',   'system/dict/index',   NULL, '0', '1', '1', '0', 'dict',       'system:dict:list',   6, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, '字典管理菜单'),
(106, 0, 1, '权限管理', 'perm',   'system/perm/index',   NULL, '0', '1', '1', '0', 'lock',       'system:perm:list',   7, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, '权限管理菜单'),
(107, 0, 1, '岗位管理', 'post',   'system/post/index',   NULL, '0', '1', '1', '0', 'post',       'system:post:list',   8, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, '岗位管理菜单'),
(108, 0, 1, '参数设置', 'config', 'system/config/index', NULL, '0', '1', '1', '0', 'edit',       'system:config:list', 9, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, '参数设置菜单'),
(109, 0, 1, '通知公告', 'notice', 'system/notice/index', NULL, '0', '1', '1', '0', 'message',    'system:notice:list',10, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, '通知公告菜单')
ON CONFLICT (tenant_id, menu_name, parent_id) DO NOTHING;

-- 系统监控子菜单
INSERT INTO sys_menu (id, tenant_id, parent_id, menu_name, path, component, parameters, external, cacheable, menu_type, visible, icon, perms, sort, status, deleted, "version", created_by, created_time, modified_by, modified_time, remark) VALUES
(200, 0, 2, '操作日志', 'operlog', 'monitor/operlog/index', NULL, '0', '1', '1', '0', 'form',      'monitor:operlog:list', 1, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, '操作日志菜单'),
(201, 0, 2, '登录日志', 'logininfor','monitor/logininfor/index', NULL, '0', '1', '1', '0', 'logininfor','monitor:logininfor:list',2,'0','0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, '登录日志菜单'),
(202, 0, 2, '在线用户', 'online',  'monitor/online/index',  NULL, '0', '1', '1', '0', 'online',    'monitor:online:list',  3, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, '在线用户菜单'),
(203, 0, 2, '服务监控', 'server',  'monitor/server/index',  NULL, '0', '1', '1', '0', 'server',    'monitor:server:list',  4, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, '服务监控菜单'),
(204, 0, 2, '缓存监控', 'cache',   'monitor/cache/index',   NULL, '0', '1', '1', '0', 'redis',     'monitor:cache:list',   5, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, '缓存监控菜单')
ON CONFLICT (tenant_id, menu_name, parent_id) DO NOTHING;

-- 系统工具子菜单
INSERT INTO sys_menu (id, tenant_id, parent_id, menu_name, path, component, parameters, external, cacheable, menu_type, visible, icon, perms, sort, status, deleted, "version", created_by, created_time, modified_by, modified_time, remark) VALUES
(300, 0, 3, '表单构建', 'build',   'tool/build/index',     NULL, '0', '1', '1', '0', 'build',     'tool:build:list',  1, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, '表单构建菜单'),
(301, 0, 3, '代码生成', 'gen',     'tool/gen/index',       NULL, '0', '1', '1', '0', 'code',      'tool:gen:list',    2, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, '代码生成菜单'),
(302, 0, 3, '系统接口', 'swagger', 'tool/swagger/index',   NULL, '0', '1', '1', '0', 'swagger',   'tool:swagger:list',3, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, '系统接口菜单')
ON CONFLICT (tenant_id, menu_name, parent_id) DO NOTHING;

-- 用户管理按钮
INSERT INTO sys_menu (id, tenant_id, parent_id, menu_name, path, component, parameters, external, cacheable, menu_type, visible, icon, perms, sort, status, deleted, "version", created_by, created_time, modified_by, modified_time, remark) VALUES
(1001, 0, 100, '用户查询', '', NULL, NULL, '0', '0', '2', '0', '#', 'system:user:query',          1, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL),
(1002, 0, 100, '用户新增', '', NULL, NULL, '0', '0', '2', '0', '#', 'system:user:add',            2, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL),
(1003, 0, 100, '用户修改', '', NULL, NULL, '0', '0', '2', '0', '#', 'system:user:edit',           3, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL),
(1004, 0, 100, '用户删除', '', NULL, NULL, '0', '0', '2', '0', '#', 'system:user:remove',         4, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL),
(1005, 0, 100, '用户导出', '', NULL, NULL, '0', '0', '2', '0', '#', 'system:user:export',         5, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL),
(1006, 0, 100, '用户导入', '', NULL, NULL, '0', '0', '2', '0', '#', 'system:user:import',         6, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL),
(1007, 0, 100, '重置密码', '', NULL, NULL, '0', '0', '2', '0', '#', 'system:user:resetPwd',       7, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL),
(1008, 0, 100, '修改密码', '', NULL, NULL, '0', '0', '2', '0', '#', 'system:user:updatePwd',      8, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL)
ON CONFLICT (tenant_id, menu_name, parent_id) DO NOTHING;

-- 角色管理按钮
INSERT INTO sys_menu (id, tenant_id, parent_id, menu_name, path, component, parameters, external, cacheable, menu_type, visible, icon, perms, sort, status, deleted, "version", created_by, created_time, modified_by, modified_time, remark) VALUES
(1011, 0, 101, '角色查询', '', NULL, NULL, '0', '0', '2', '0', '#', 'system:role:query',          1, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL),
(1012, 0, 101, '角色新增', '', NULL, NULL, '0', '0', '2', '0', '#', 'system:role:add',            2, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL),
(1013, 0, 101, '角色修改', '', NULL, NULL, '0', '0', '2', '0', '#', 'system:role:edit',           3, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL),
(1014, 0, 101, '角色删除', '', NULL, NULL, '0', '0', '2', '0', '#', 'system:role:remove',         4, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL),
(1015, 0, 101, '角色导出', '', NULL, NULL, '0', '0', '2', '0', '#', 'system:role:export',         5, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL)
ON CONFLICT (tenant_id, menu_name, parent_id) DO NOTHING;

-- 菜单管理按钮
INSERT INTO sys_menu (id, tenant_id, parent_id, menu_name, path, component, parameters, external, cacheable, menu_type, visible, icon, perms, sort, status, deleted, "version", created_by, created_time, modified_by, modified_time, remark) VALUES
(1021, 0, 102, '菜单查询', '', NULL, NULL, '0', '0', '2', '0', '#', 'system:menu:query',          1, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL),
(1022, 0, 102, '菜单新增', '', NULL, NULL, '0', '0', '2', '0', '#', 'system:menu:add',            2, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL),
(1023, 0, 102, '菜单修改', '', NULL, NULL, '0', '0', '2', '0', '#', 'system:menu:edit',           3, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL),
(1024, 0, 102, '菜单删除', '', NULL, NULL, '0', '0', '2', '0', '#', 'system:menu:remove',         4, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL)
ON CONFLICT (tenant_id, menu_name, parent_id) DO NOTHING;

-- 部门管理按钮
INSERT INTO sys_menu (id, tenant_id, parent_id, menu_name, path, component, parameters, external, cacheable, menu_type, visible, icon, perms, sort, status, deleted, "version", created_by, created_time, modified_by, modified_time, remark) VALUES
(1031, 0, 103, '部门查询', '', NULL, NULL, '0', '0', '2', '0', '#', 'system:dept:query',          1, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL),
(1032, 0, 103, '部门新增', '', NULL, NULL, '0', '0', '2', '0', '#', 'system:dept:add',            2, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL),
(1033, 0, 103, '部门修改', '', NULL, NULL, '0', '0', '2', '0', '#', 'system:dept:edit',           3, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL),
(1034, 0, 103, '部门删除', '', NULL, NULL, '0', '0', '2', '0', '#', 'system:dept:remove',         4, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL)
ON CONFLICT (tenant_id, menu_name, parent_id) DO NOTHING;

-- 租户管理按钮
INSERT INTO sys_menu (id, tenant_id, parent_id, menu_name, path, component, parameters, external, cacheable, menu_type, visible, icon, perms, sort, status, deleted, "version", created_by, created_time, modified_by, modified_time, remark) VALUES
(1041, 0, 104, '租户查询', '', NULL, NULL, '0', '0', '2', '0', '#', 'system:tenant:query',        1, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL),
(1042, 0, 104, '租户新增', '', NULL, NULL, '0', '0', '2', '0', '#', 'system:tenant:add',          2, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL),
(1043, 0, 104, '租户修改', '', NULL, NULL, '0', '0', '2', '0', '#', 'system:tenant:edit',         3, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL),
(1044, 0, 104, '租户删除', '', NULL, NULL, '0', '0', '2', '0', '#', 'system:tenant:remove',       4, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL)
ON CONFLICT (tenant_id, menu_name, parent_id) DO NOTHING;

-- 字典管理按钮
INSERT INTO sys_menu (id, tenant_id, parent_id, menu_name, path, component, parameters, external, cacheable, menu_type, visible, icon, perms, sort, status, deleted, "version", created_by, created_time, modified_by, modified_time, remark) VALUES
(1051, 0, 105, '字典查询', '', NULL, NULL, '0', '0', '2', '0', '#', 'system:dict:query',          1, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL),
(1052, 0, 105, '字典新增', '', NULL, NULL, '0', '0', '2', '0', '#', 'system:dict:add',            2, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL),
(1053, 0, 105, '字典修改', '', NULL, NULL, '0', '0', '2', '0', '#', 'system:dict:edit',           3, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL),
(1054, 0, 105, '字典删除', '', NULL, NULL, '0', '0', '2', '0', '#', 'system:dict:remove',         4, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL)
ON CONFLICT (tenant_id, menu_name, parent_id) DO NOTHING;

-- 权限管理按钮
INSERT INTO sys_menu (id, tenant_id, parent_id, menu_name, path, component, parameters, external, cacheable, menu_type, visible, icon, perms, sort, status, deleted, "version", created_by, created_time, modified_by, modified_time, remark) VALUES
(1061, 0, 106, '权限查询', '', NULL, NULL, '0', '0', '2', '0', '#', 'system:perm:query',          1, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL),
(1062, 0, 106, '权限新增', '', NULL, NULL, '0', '0', '2', '0', '#', 'system:perm:add',            2, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL),
(1063, 0, 106, '权限修改', '', NULL, NULL, '0', '0', '2', '0', '#', 'system:perm:edit',           3, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL),
(1064, 0, 106, '权限删除', '', NULL, NULL, '0', '0', '2', '0', '#', 'system:perm:remove',         4, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL)
ON CONFLICT (tenant_id, menu_name, parent_id) DO NOTHING;

-- 操作日志按钮
INSERT INTO sys_menu (id, tenant_id, parent_id, menu_name, path, component, parameters, external, cacheable, menu_type, visible, icon, perms, sort, status, deleted, "version", created_by, created_time, modified_by, modified_time, remark) VALUES
(2001, 0, 200, '操作查询', '', NULL, NULL, '0', '0', '2', '0', '#', 'monitor:operlog:query',      1, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL),
(2002, 0, 200, '操作删除', '', NULL, NULL, '0', '0', '2', '0', '#', 'monitor:operlog:remove',     2, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL),
(2003, 0, 200, '日志导出', '', NULL, NULL, '0', '0', '2', '0', '#', 'monitor:operlog:export',     3, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL)
ON CONFLICT (tenant_id, menu_name, parent_id) DO NOTHING;

-- 登录日志按钮
INSERT INTO sys_menu (id, tenant_id, parent_id, menu_name, path, component, parameters, external, cacheable, menu_type, visible, icon, perms, sort, status, deleted, "version", created_by, created_time, modified_by, modified_time, remark) VALUES
(2011, 0, 201, '登录查询', '', NULL, NULL, '0', '0', '2', '0', '#', 'monitor:logininfor:query',   1, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL),
(2012, 0, 201, '登录删除', '', NULL, NULL, '0', '0', '2', '0', '#', 'monitor:logininfor:remove',  2, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL),
(2013, 0, 201, '日志导出', '', NULL, NULL, '0', '0', '2', '0', '#', 'monitor:logininfor:export',  3, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL),
(2014, 0, 201, '账户解锁', '', NULL, NULL, '0', '0', '2', '0', '#', 'monitor:logininfor:unlock',  4, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL)
ON CONFLICT (tenant_id, menu_name, parent_id) DO NOTHING;

-- 在线用户按钮
INSERT INTO sys_menu (id, tenant_id, parent_id, menu_name, path, component, parameters, external, cacheable, menu_type, visible, icon, perms, sort, status, deleted, "version", created_by, created_time, modified_by, modified_time, remark) VALUES
(2021, 0, 202, '在线查询', '', NULL, NULL, '0', '0', '2', '0', '#', 'monitor:online:query',       1, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL),
(2022, 0, 202, '强制下线', '', NULL, NULL, '0', '0', '2', '0', '#', 'monitor:online:forceLogout', 2, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL)
ON CONFLICT (tenant_id, menu_name, parent_id) DO NOTHING;

-- 服务监控 / 缓存监控 查询按钮
-- 对应控制器：ServerInfoController（GET /system/monitor/server）、CacheMonitorController（GET /system/monitor/cache）
-- 菜单 203 服务监控 / 204 缓存监控 原本只有 list 权限，这里补上页面实际校验的 query 权限码
INSERT INTO sys_menu (id, tenant_id, parent_id, menu_name, path, component, parameters, external, cacheable, menu_type, visible, icon, perms, sort, status, deleted, "version", created_by, created_time, modified_by, modified_time, remark) VALUES
(2031, 0, 203, '服务查询', '', NULL, NULL, '0', '0', '2', '0', '#', 'monitor:server:query',   1, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL),
(2041, 0, 204, '缓存查询', '', NULL, NULL, '0', '0', '2', '0', '#', 'monitor:cache:query',    1, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL)
ON CONFLICT (tenant_id, menu_name, parent_id) DO NOTHING;

-- 代码生成按钮
INSERT INTO sys_menu (id, tenant_id, parent_id, menu_name, path, component, parameters, external, cacheable, menu_type, visible, icon, perms, sort, status, deleted, "version", created_by, created_time, modified_by, modified_time, remark) VALUES
(3011, 0, 301, '生成查询', '', NULL, NULL, '0', '0', '2', '0', '#', 'tool:gen:query',             1, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL),
(3012, 0, 301, '生成修改', '', NULL, NULL, '0', '0', '2', '0', '#', 'tool:gen:edit',              2, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL),
(3013, 0, 301, '生成删除', '', NULL, NULL, '0', '0', '2', '0', '#', 'tool:gen:remove',            3, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL),
(3014, 0, 301, '导入代码', '', NULL, NULL, '0', '0', '2', '0', '#', 'tool:gen:import',            4, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL),
(3015, 0, 301, '预览代码', '', NULL, NULL, '0', '0', '2', '0', '#', 'tool:gen:preview',           5, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL),
(3016, 0, 301, '生成代码', '', NULL, NULL, '0', '0', '2', '0', '#', 'tool:gen:code',              6, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL)
ON CONFLICT (tenant_id, menu_name, parent_id) DO NOTHING;


-- ============================================================================
-- 角色数据：超级管理员（tenant_id=0 共享角色）
-- ============================================================================
INSERT INTO sys_role (id, tenant_id, role_name, role_code, data_scope, menu_check_strictly, dept_check_strictly, sort, status, deleted, "version", created_by, created_time, modified_by, modified_time, remark)
VALUES (1, 0, '超级管理员', 'admin', '1', '1', '1', 1, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, '内置超级管理员，拥有全部菜单权限')
ON CONFLICT (id) DO NOTHING;


-- ============================================================================
-- 用户数据：内置超级管理员（tenant_id=0 master租户，dept_id=1 总经理办公室）
-- 默认密码：admin123（BCrypt 加密）
-- ============================================================================
INSERT INTO sys_user (id, tenant_id, dept_id, user_name, nick_name, user_type, email, phone, gender, avatar, "password", login_ip, login_date, sort, status, deleted, "version", created_by, created_time, modified_by, modified_time, remark)
VALUES (1, 0, 1, 'admin', '超级管理员', '00', 'admin@baize.com', '18988888888', '0', '', '$2a$10$JE6rq5b6MkUuUEvmIPt.ruMdcFHmUZjie6LwmxAVfp/LlqsDllwd6', '', NULL, 1, '0', '0', 0, 1, '2025-08-11 17:33:07.797', NULL, NULL, '内置超级管理员，默认密码 admin123')
ON CONFLICT (id) DO NOTHING;


-- ============================================================================
-- 用户-角色绑定：admin 用户绑定超级管理员角色
-- ============================================================================
INSERT INTO sys_user_role (tenant_id, user_id, role_id) VALUES (0, 1, 1)
ON CONFLICT (tenant_id, user_id, role_id) DO NOTHING;


-- ============================================================================
-- 角色-菜单绑定：admin 拥有全部菜单
-- ============================================================================
INSERT INTO sys_role_menu (tenant_id, role_id, menu_id) VALUES
(0, 1, 4),
(0, 1, 1), (0, 1, 2), (0, 1, 3),
(0, 1, 100), (0, 1, 101), (0, 1, 102), (0, 1, 103), (0, 1, 104), (0, 1, 105), (0, 1, 106), (0, 1, 107), (0, 1, 108), (0, 1, 109),
(0, 1, 200), (0, 1, 201), (0, 1, 202), (0, 1, 203), (0, 1, 204),
(0, 1, 2031), (0, 1, 2041),
(0, 1, 300), (0, 1, 301), (0, 1, 302),
(0, 1, 1001), (0, 1, 1002), (0, 1, 1003), (0, 1, 1004), (0, 1, 1005), (0, 1, 1006), (0, 1, 1007), (0, 1, 1008),
(0, 1, 1011), (0, 1, 1012), (0, 1, 1013), (0, 1, 1014), (0, 1, 1015),
(0, 1, 1021), (0, 1, 1022), (0, 1, 1023), (0, 1, 1024),
(0, 1, 1031), (0, 1, 1032), (0, 1, 1033), (0, 1, 1034),
(0, 1, 1041), (0, 1, 1042), (0, 1, 1043), (0, 1, 1044),
(0, 1, 1051), (0, 1, 1052), (0, 1, 1053), (0, 1, 1054),
(0, 1, 1061), (0, 1, 1062), (0, 1, 1063), (0, 1, 1064),
(0, 1, 2001), (0, 1, 2002), (0, 1, 2003),
(0, 1, 2011), (0, 1, 2012), (0, 1, 2013), (0, 1, 2014),
(0, 1, 2021), (0, 1, 2022),
(0, 1, 3011), (0, 1, 3012), (0, 1, 3013), (0, 1, 3014), (0, 1, 3015), (0, 1, 3016)
ON CONFLICT (tenant_id, role_id, menu_id) DO NOTHING;


-- ============================================================================
-- 定时任务（sys_job）菜单与权限码
-- 对应控制器：SysJobController（/system/job），权限前缀 system:job:*
-- 说明：仅初始化脚本，勿直接在生产库执行；执行前请确认 id 110/1101~1104 未被占用
-- ============================================================================
INSERT INTO sys_menu (id, tenant_id, parent_id, menu_name, path, component, parameters, external, cacheable, menu_type, visible, icon, perms, sort, status, deleted, "version", created_by, created_time, modified_by, modified_time, remark) VALUES
(110, 0, 1, '定时任务', 'job', 'system/job/index', NULL, '0', '1', '1', '0', 'job', 'system:job:list', 11, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, '定时任务菜单')
ON CONFLICT (tenant_id, menu_name, parent_id) DO NOTHING;

-- 定时任务按钮
INSERT INTO sys_menu (id, tenant_id, parent_id, menu_name, path, component, parameters, external, cacheable, menu_type, visible, icon, perms, sort, status, deleted, "version", created_by, created_time, modified_by, modified_time, remark) VALUES
(1101, 0, 110, '任务查询', '', NULL, NULL, '0', '0', '2', '0', '#', 'system:job:query',           1, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL),
(1102, 0, 110, '任务新增', '', NULL, NULL, '0', '0', '2', '0', '#', 'system:job:add',             2, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL),
(1103, 0, 110, '任务修改', '', NULL, NULL, '0', '0', '2', '0', '#', 'system:job:edit',            3, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL),
(1104, 0, 110, '任务删除', '', NULL, NULL, '0', '0', '2', '0', '#', 'system:job:remove',          4, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL)
ON CONFLICT (tenant_id, menu_name, parent_id) DO NOTHING;

-- 角色-菜单绑定：超级管理员（role_id=1）授予定时任务全部权限
INSERT INTO sys_role_menu (tenant_id, role_id, menu_id) VALUES
(0, 1, 110), (0, 1, 1101), (0, 1, 1102), (0, 1, 1103), (0, 1, 1104)
ON CONFLICT (tenant_id, role_id, menu_id) DO NOTHING;


-- ============================================================================
-- 补齐岗位/参数设置/通知公告的按钮权限码
-- 原脚本这三项只有菜单行上的 :list 码，缺 query/add/edit/remove 按钮行，
-- 与控制器的 @RequiresPermission 不一致，会导致非超管角色调用必然 403。
-- 对应控制器：SysPostController / SysConfigController / SysNoticeController
-- 说明：仅初始化脚本，勿直接在生产库执行；执行前请确认 id 1071~1074/1081~1084/1091~1094 未被占用
-- ============================================================================

-- 岗位管理按钮（system:post）
INSERT INTO sys_menu (id, tenant_id, parent_id, menu_name, path, component, parameters, external, cacheable, menu_type, visible, icon, perms, sort, status, deleted, "version", created_by, created_time, modified_by, modified_time, remark) VALUES
(1071, 0, 107, '岗位查询', '', NULL, NULL, '0', '0', '2', '0', '#', 'system:post:query',          1, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL),
(1072, 0, 107, '岗位新增', '', NULL, NULL, '0', '0', '2', '0', '#', 'system:post:add',            2, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL),
(1073, 0, 107, '岗位修改', '', NULL, NULL, '0', '0', '2', '0', '#', 'system:post:edit',           3, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL),
(1074, 0, 107, '岗位删除', '', NULL, NULL, '0', '0', '2', '0', '#', 'system:post:remove',         4, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL)
ON CONFLICT (tenant_id, menu_name, parent_id) DO NOTHING;

-- 参数设置按钮（system:config）
INSERT INTO sys_menu (id, tenant_id, parent_id, menu_name, path, component, parameters, external, cacheable, menu_type, visible, icon, perms, sort, status, deleted, "version", created_by, created_time, modified_by, modified_time, remark) VALUES
(1081, 0, 108, '参数查询', '', NULL, NULL, '0', '0', '2', '0', '#', 'system:config:query',        1, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL),
(1082, 0, 108, '参数新增', '', NULL, NULL, '0', '0', '2', '0', '#', 'system:config:add',          2, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL),
(1083, 0, 108, '参数修改', '', NULL, NULL, '0', '0', '2', '0', '#', 'system:config:edit',         3, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL),
(1084, 0, 108, '参数删除', '', NULL, NULL, '0', '0', '2', '0', '#', 'system:config:remove',       4, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL)
ON CONFLICT (tenant_id, menu_name, parent_id) DO NOTHING;

-- 通知公告按钮（system:notice）
INSERT INTO sys_menu (id, tenant_id, parent_id, menu_name, path, component, parameters, external, cacheable, menu_type, visible, icon, perms, sort, status, deleted, "version", created_by, created_time, modified_by, modified_time, remark) VALUES
(1091, 0, 109, '公告查询', '', NULL, NULL, '0', '0', '2', '0', '#', 'system:notice:query',        1, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL),
(1092, 0, 109, '公告新增', '', NULL, NULL, '0', '0', '2', '0', '#', 'system:notice:add',          2, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL),
(1093, 0, 109, '公告修改', '', NULL, NULL, '0', '0', '2', '0', '#', 'system:notice:edit',         3, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL),
(1094, 0, 109, '公告删除', '', NULL, NULL, '0', '0', '2', '0', '#', 'system:notice:remove',       4, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL)
ON CONFLICT (tenant_id, menu_name, parent_id) DO NOTHING;

-- 角色-菜单绑定：超级管理员（role_id=1）授予上述按钮权限
INSERT INTO sys_role_menu (tenant_id, role_id, menu_id) VALUES
(0, 1, 1071), (0, 1, 1072), (0, 1, 1073), (0, 1, 1074),
(0, 1, 1081), (0, 1, 1082), (0, 1, 1083), (0, 1, 1084),
(0, 1, 1091), (0, 1, 1092), (0, 1, 1093), (0, 1, 1094)
ON CONFLICT (tenant_id, role_id, menu_id) DO NOTHING;
