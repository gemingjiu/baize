-- 租户数据
INSERT INTO sys_tenant (id, tenant_code, tenant_name, expire_time, contact_person, contact_phone, contact_email, "domain", max_user, sort, status, deleted, "version", created_by, created_time, modified_by, modified_time, remark)
VALUES(1, 'XIAOMI', '小米科技有限责任公司', '2025-07-17 16:51:06.290', '雷军', '18988888888', '18988888888@189.cn', 'xiaomi', 10, 1, '0', '0', 0, 1, '2025-07-17 16:51:06.290', 1, '2025-07-17 16:51:06.290', NULL);

INSERT INTO sys_tenant (id, tenant_code, tenant_name, expire_time, contact_person, contact_phone, contact_email, "domain", max_user, sort, status, deleted, "version", created_by, created_time, modified_by, modified_time, remark)
VALUES(2, 'HUAWEI', '华为科技有限责任公司', '2025-07-17 16:51:06.290', '大嘴', '18944441234', '18944441234@189.cn', 'huawei', 10, 2, '0', '0', 0, 1, '2025-07-17 16:51:06.290', 1, '2025-07-17 16:51:06.290', NULL);

INSERT INTO sys_tenant (id, tenant_code, tenant_name, expire_time, contact_person, contact_phone, contact_email, "domain", max_user, sort, status, deleted, "version", created_by, created_time, modified_by, modified_time, remark)
VALUES(3, 'ZIJIE', '字节跳动有限责任公司', '2025-07-17 16:51:06.290', '跳动', '18912341234', '18912341234@189.cn', 'zijie', 10, 3, '0', '0', 0, 1, '2025-07-17 16:51:06.290', 1, '2025-07-17 16:51:06.290', NULL);

INSERT INTO sys_tenant (id, tenant_code, tenant_name, expire_time, contact_person, contact_phone, contact_email, "domain", max_user, sort, status, deleted, "version", created_by, created_time, modified_by, modified_time, remark)
VALUES(4, 'ZTE', '中兴科技有限责任公司', '2025-07-17 16:51:06.290', '中兴', '18988888888', '18988888888@189.cn', 'zte', 10, 4, '0', '0', 0, 1, '2025-07-17 16:51:06.290', 1, '2025-07-17 16:51:06.290', NULL);

INSERT INTO sys_tenant (id, tenant_code, tenant_name, expire_time, contact_person, contact_phone, contact_email, "domain", max_user, sort, status, deleted, "version", created_by, created_time, modified_by, modified_time, remark)
VALUES(5, 'CXMT', '长鑫科技有限责任公司', '2025-07-25 16:48:10.000', '长鑫', '18988888888', '18988888888@189.cn', 'cxmt', 10, 5, '0', '0', 0, 1, '2025-07-25 16:48:10.654', NULL, '2025-07-25 16:48:10.655', NULL);


-- 部门数据（tenant_id=0 表示 master 租户
INSERT INTO sys_dept (id, tenant_id, parent_id, dept_name, leader, phone, email, sort, status, deleted, "version", created_by, created_time, modified_by, modified_time, remark)
VALUES(1, 0, 0, '总经理办公室', '雷军', '18988888888', '18988888888@189.cn', 1, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL);


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
(1, 0, 0, '系统管理', '/system', NULL, NULL, '0', '0', '0', '0', 'system',  NULL, 1, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, '系统管理目录'),
(2, 0, 0, '系统监控', '/monitor', NULL, NULL, '0', '0', '0', '0', 'monitor', NULL, 2, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, '系统监控目录'),
(3, 0, 0, '系统工具', '/tool', NULL, NULL, '0', '0', '0', '0', 'tool',     NULL, 3, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, '系统工具目录');

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
(109, 0, 1, '通知公告', 'notice', 'system/notice/index', NULL, '0', '1', '1', '0', 'message',    'system:notice:list',10, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, '通知公告菜单');

-- 系统监控子菜单
INSERT INTO sys_menu (id, tenant_id, parent_id, menu_name, path, component, parameters, external, cacheable, menu_type, visible, icon, perms, sort, status, deleted, "version", created_by, created_time, modified_by, modified_time, remark) VALUES
(200, 0, 2, '操作日志', 'operlog', 'monitor/operlog/index', NULL, '0', '1', '1', '0', 'form',      'monitor:operlog:list', 1, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, '操作日志菜单'),
(201, 0, 2, '登录日志', 'logininfor','monitor/logininfor/index', NULL, '0', '1', '1', '0', 'logininfor','monitor:logininfor:list',2,'0','0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, '登录日志菜单'),
(202, 0, 2, '在线用户', 'online',  'monitor/online/index',  NULL, '0', '1', '1', '0', 'online',    'monitor:online:list',  3, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, '在线用户菜单'),
(203, 0, 2, '服务监控', 'server',  'monitor/server/index',  NULL, '0', '1', '1', '0', 'server',    'monitor:server:list',  4, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, '服务监控菜单'),
(204, 0, 2, '缓存监控', 'cache',   'monitor/cache/index',   NULL, '0', '1', '1', '0', 'redis',     'monitor:cache:list',   5, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, '缓存监控菜单');

-- 系统工具子菜单
INSERT INTO sys_menu (id, tenant_id, parent_id, menu_name, path, component, parameters, external, cacheable, menu_type, visible, icon, perms, sort, status, deleted, "version", created_by, created_time, modified_by, modified_time, remark) VALUES
(300, 0, 3, '表单构建', 'build',   'tool/build/index',     NULL, '0', '1', '1', '0', 'build',     'tool:build:list',  1, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, '表单构建菜单'),
(301, 0, 3, '代码生成', 'gen',     'tool/gen/index',       NULL, '0', '1', '1', '0', 'code',      'tool:gen:list',    2, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, '代码生成菜单'),
(302, 0, 3, '系统接口', 'swagger', 'tool/swagger/index',   NULL, '0', '1', '1', '0', 'swagger',   'tool:swagger:list',3, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, '系统接口菜单');

-- 用户管理按钮
INSERT INTO sys_menu (id, tenant_id, parent_id, menu_name, path, component, parameters, external, cacheable, menu_type, visible, icon, perms, sort, status, deleted, "version", created_by, created_time, modified_by, modified_time, remark) VALUES
(1001, 0, 100, '用户查询', '', NULL, NULL, '0', '0', '2', '0', '#', 'system:user:query',          1, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL),
(1002, 0, 100, '用户新增', '', NULL, NULL, '0', '0', '2', '0', '#', 'system:user:add',            2, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL),
(1003, 0, 100, '用户修改', '', NULL, NULL, '0', '0', '2', '0', '#', 'system:user:edit',           3, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL),
(1004, 0, 100, '用户删除', '', NULL, NULL, '0', '0', '2', '0', '#', 'system:user:remove',         4, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL),
(1005, 0, 100, '用户导出', '', NULL, NULL, '0', '0', '2', '0', '#', 'system:user:export',         5, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL),
(1006, 0, 100, '用户导入', '', NULL, NULL, '0', '0', '2', '0', '#', 'system:user:import',         6, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL),
(1007, 0, 100, '重置密码', '', NULL, NULL, '0', '0', '2', '0', '#', 'system:user:resetPwd',       7, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL);

-- 角色管理按钮
INSERT INTO sys_menu (id, tenant_id, parent_id, menu_name, path, component, parameters, external, cacheable, menu_type, visible, icon, perms, sort, status, deleted, "version", created_by, created_time, modified_by, modified_time, remark) VALUES
(1011, 0, 101, '角色查询', '', NULL, NULL, '0', '0', '2', '0', '#', 'system:role:query',          1, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL),
(1012, 0, 101, '角色新增', '', NULL, NULL, '0', '0', '2', '0', '#', 'system:role:add',            2, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL),
(1013, 0, 101, '角色修改', '', NULL, NULL, '0', '0', '2', '0', '#', 'system:role:edit',           3, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL),
(1014, 0, 101, '角色删除', '', NULL, NULL, '0', '0', '2', '0', '#', 'system:role:remove',         4, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL),
(1015, 0, 101, '角色导出', '', NULL, NULL, '0', '0', '2', '0', '#', 'system:role:export',         5, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL);

-- 菜单管理按钮
INSERT INTO sys_menu (id, tenant_id, parent_id, menu_name, path, component, parameters, external, cacheable, menu_type, visible, icon, perms, sort, status, deleted, "version", created_by, created_time, modified_by, modified_time, remark) VALUES
(1021, 0, 102, '菜单查询', '', NULL, NULL, '0', '0', '2', '0', '#', 'system:menu:query',          1, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL),
(1022, 0, 102, '菜单新增', '', NULL, NULL, '0', '0', '2', '0', '#', 'system:menu:add',            2, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL),
(1023, 0, 102, '菜单修改', '', NULL, NULL, '0', '0', '2', '0', '#', 'system:menu:edit',           3, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL),
(1024, 0, 102, '菜单删除', '', NULL, NULL, '0', '0', '2', '0', '#', 'system:menu:remove',         4, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL);

-- 部门管理按钮
INSERT INTO sys_menu (id, tenant_id, parent_id, menu_name, path, component, parameters, external, cacheable, menu_type, visible, icon, perms, sort, status, deleted, "version", created_by, created_time, modified_by, modified_time, remark) VALUES
(1031, 0, 103, '部门查询', '', NULL, NULL, '0', '0', '2', '0', '#', 'system:dept:query',          1, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL),
(1032, 0, 103, '部门新增', '', NULL, NULL, '0', '0', '2', '0', '#', 'system:dept:add',            2, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL),
(1033, 0, 103, '部门修改', '', NULL, NULL, '0', '0', '2', '0', '#', 'system:dept:edit',           3, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL),
(1034, 0, 103, '部门删除', '', NULL, NULL, '0', '0', '2', '0', '#', 'system:dept:remove',         4, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL);

-- 租户管理按钮
INSERT INTO sys_menu (id, tenant_id, parent_id, menu_name, path, component, parameters, external, cacheable, menu_type, visible, icon, perms, sort, status, deleted, "version", created_by, created_time, modified_by, modified_time, remark) VALUES
(1041, 0, 104, '租户查询', '', NULL, NULL, '0', '0', '2', '0', '#', 'system:tenant:query',        1, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL),
(1042, 0, 104, '租户新增', '', NULL, NULL, '0', '0', '2', '0', '#', 'system:tenant:add',          2, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL),
(1043, 0, 104, '租户修改', '', NULL, NULL, '0', '0', '2', '0', '#', 'system:tenant:edit',         3, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL),
(1044, 0, 104, '租户删除', '', NULL, NULL, '0', '0', '2', '0', '#', 'system:tenant:remove',       4, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL);

-- 字典管理按钮
INSERT INTO sys_menu (id, tenant_id, parent_id, menu_name, path, component, parameters, external, cacheable, menu_type, visible, icon, perms, sort, status, deleted, "version", created_by, created_time, modified_by, modified_time, remark) VALUES
(1051, 0, 105, '字典查询', '', NULL, NULL, '0', '0', '2', '0', '#', 'system:dict:query',          1, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL),
(1052, 0, 105, '字典新增', '', NULL, NULL, '0', '0', '2', '0', '#', 'system:dict:add',            2, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL),
(1053, 0, 105, '字典修改', '', NULL, NULL, '0', '0', '2', '0', '#', 'system:dict:edit',           3, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL),
(1054, 0, 105, '字典删除', '', NULL, NULL, '0', '0', '2', '0', '#', 'system:dict:remove',         4, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL);

-- 权限管理按钮
INSERT INTO sys_menu (id, tenant_id, parent_id, menu_name, path, component, parameters, external, cacheable, menu_type, visible, icon, perms, sort, status, deleted, "version", created_by, created_time, modified_by, modified_time, remark) VALUES
(1061, 0, 106, '权限查询', '', NULL, NULL, '0', '0', '2', '0', '#', 'system:perm:query',          1, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL),
(1062, 0, 106, '权限新增', '', NULL, NULL, '0', '0', '2', '0', '#', 'system:perm:add',            2, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL),
(1063, 0, 106, '权限修改', '', NULL, NULL, '0', '0', '2', '0', '#', 'system:perm:edit',           3, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL),
(1064, 0, 106, '权限删除', '', NULL, NULL, '0', '0', '2', '0', '#', 'system:perm:remove',         4, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL);

-- 操作日志按钮
INSERT INTO sys_menu (id, tenant_id, parent_id, menu_name, path, component, parameters, external, cacheable, menu_type, visible, icon, perms, sort, status, deleted, "version", created_by, created_time, modified_by, modified_time, remark) VALUES
(2001, 0, 200, '操作查询', '', NULL, NULL, '0', '0', '2', '0', '#', 'monitor:operlog:query',      1, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL),
(2002, 0, 200, '操作删除', '', NULL, NULL, '0', '0', '2', '0', '#', 'monitor:operlog:remove',     2, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL),
(2003, 0, 200, '日志导出', '', NULL, NULL, '0', '0', '2', '0', '#', 'monitor:operlog:export',     3, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL);

-- 登录日志按钮
INSERT INTO sys_menu (id, tenant_id, parent_id, menu_name, path, component, parameters, external, cacheable, menu_type, visible, icon, perms, sort, status, deleted, "version", created_by, created_time, modified_by, modified_time, remark) VALUES
(2011, 0, 201, '登录查询', '', NULL, NULL, '0', '0', '2', '0', '#', 'monitor:logininfor:query',   1, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL),
(2012, 0, 201, '登录删除', '', NULL, NULL, '0', '0', '2', '0', '#', 'monitor:logininfor:remove',  2, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL),
(2013, 0, 201, '日志导出', '', NULL, NULL, '0', '0', '2', '0', '#', 'monitor:logininfor:export',  3, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL),
(2014, 0, 201, '账户解锁', '', NULL, NULL, '0', '0', '2', '0', '#', 'monitor:logininfor:unlock',  4, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL);

-- 在线用户按钮
INSERT INTO sys_menu (id, tenant_id, parent_id, menu_name, path, component, parameters, external, cacheable, menu_type, visible, icon, perms, sort, status, deleted, "version", created_by, created_time, modified_by, modified_time, remark) VALUES
(2021, 0, 202, '在线查询', '', NULL, NULL, '0', '0', '2', '0', '#', 'monitor:online:query',       1, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL),
(2022, 0, 202, '强制下线', '', NULL, NULL, '0', '0', '2', '0', '#', 'monitor:online:forceLogout', 2, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL);

-- 代码生成按钮
INSERT INTO sys_menu (id, tenant_id, parent_id, menu_name, path, component, parameters, external, cacheable, menu_type, visible, icon, perms, sort, status, deleted, "version", created_by, created_time, modified_by, modified_time, remark) VALUES
(3011, 0, 301, '生成查询', '', NULL, NULL, '0', '0', '2', '0', '#', 'tool:gen:query',             1, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL),
(3012, 0, 301, '生成修改', '', NULL, NULL, '0', '0', '2', '0', '#', 'tool:gen:edit',              2, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL),
(3013, 0, 301, '生成删除', '', NULL, NULL, '0', '0', '2', '0', '#', 'tool:gen:remove',            3, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL),
(3014, 0, 301, '导入代码', '', NULL, NULL, '0', '0', '2', '0', '#', 'tool:gen:import',            4, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL),
(3015, 0, 301, '预览代码', '', NULL, NULL, '0', '0', '2', '0', '#', 'tool:gen:preview',           5, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL),
(3016, 0, 301, '生成代码', '', NULL, NULL, '0', '0', '2', '0', '#', 'tool:gen:code',              6, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, NULL);


-- ============================================================================
-- 角色数据：超级管理员（tenant_id=0 共享角色）
-- ============================================================================
INSERT INTO sys_role (id, tenant_id, role_name, role_code, data_scope, menu_check_strictly, dept_check_strictly, sort, status, deleted, "version", created_by, created_time, modified_by, modified_time, remark)
VALUES (1, 0, '超级管理员', 'admin', '1', '1', '1', 1, '0', '0', 1, 1, '2025-08-11 17:33:07.797', NULL, NULL, '内置超级管理员，拥有全部菜单权限');


-- ============================================================================
-- 角色-菜单绑定：admin 拥有全部菜单
-- ============================================================================
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(1, 1), (1, 2), (1, 3),
(1, 100), (1, 101), (1, 102), (1, 103), (1, 104), (1, 105), (1, 106), (1, 107), (1, 108), (1, 109),
(1, 200), (1, 201), (1, 202), (1, 203), (1, 204),
(1, 300), (1, 301), (1, 302),
(1, 1001), (1, 1002), (1, 1003), (1, 1004), (1, 1005), (1, 1006), (1, 1007),
(1, 1011), (1, 1012), (1, 1013), (1, 1014), (1, 1015),
(1, 1021), (1, 1022), (1, 1023), (1, 1024),
(1, 1031), (1, 1032), (1, 1033), (1, 1034),
(1, 1041), (1, 1042), (1, 1043), (1, 1044),
(1, 1051), (1, 1052), (1, 1053), (1, 1054),
(1, 1061), (1, 1062), (1, 1063), (1, 1064),
(1, 2001), (1, 2002), (1, 2003),
(1, 2011), (1, 2012), (1, 2013), (1, 2014),
(1, 2021), (1, 2022),
(1, 3011), (1, 3012), (1, 3013), (1, 3014), (1, 3015), (1, 3016);
