-- 用户表
INSERT INTO sys_user VALUES ('1', '1', 'admin', '超级管理员', 0, '18888888888@baize.com', '18888888888', '0', '','$2a$10$Fk/AFdAuCBNpUYG4uNLAoOE5GbIIA8joG4CPJMJ7LWaxgMDgHHJoe', 1, '127.0.0.1', '2024-01-29 12:15:21.195', '0','0', 0, 'admin', '2024-01-29 12:15:21.195', 'admin', '2024-01-29 12:15:21.195', '系统管理员');
INSERT INTO sys_user VALUES ('2', '2', 'baize', '白泽', 0, '', '19999999999', '0', '','$2a$10$SvvP38qptFjx8PlJsezRQez50XRSJLwdT5yhFVHyC9NUR.cBJNu/.', 2, '', NULL, '0', '0', 0, 'admin','2024-03-05 15:59:31.148', 'admin', '2024-03-05 16:35:54.254', NULL);

-- 部门表
INSERT INTO sys_dept VALUES ('1', '0', '0', '白泽管理系统', '白泽', '18888888888', '1888888888@qq.com', 1, '0', '0', 0, 'admin','2024-01-29 12:17:06.830', 'admin', '2024-03-05 11:04:08.557', '系统管理员');
INSERT INTO sys_dept VALUES ('2', '1', '0,1', '研发部', NULL, NULL, NULL, 1, '0', '0', 0, 'admin', '2024-03-05 11:05:44.524', 'admin','2024-03-05 11:06:01.894', NULL);
INSERT INTO sys_dept VALUES ('3', '1', '0,1', '产品部', NULL, NULL, NULL, 2, '0', '0', 0, 'admin', '2024-03-05 11:05:56.743', '', NULL,NULL);

-- 角色表
INSERT INTO sys_role VALUES ('1', '管理员', 'admin', '1', '1', '1', 1, '0', '0', 0, 'admin', '2024-01-29 12:17:11.540', 'admin','2024-03-05 16:59:35.924', '系统管理员');
INSERT INTO sys_role VALUES ('2', '普通角色', 'common', '1', '1', '1', 2, '0', '0', 0, 'admin', '2024-01-29 13:24:43.542', 'admin','2024-03-05 16:58:51.471', '普通角色');
INSERT INTO sys_role VALUES ('3', '开发者', 'develop', '1', '1', '1', 3, '0', '0', 0, 'admin', '2024-03-06 20:20:57.705', 'admin','2024-03-06 20:21:18.534', NULL);

-- 岗位表
INSERT INTO sys_post VALUES ('1', 'ceo', '董事长', 1, '0', '0', 0, 'admin', '2024-01-29 13:22:17.932', 'admin', '2024-01-29 13:22:17.932','');
INSERT INTO sys_post VALUES ('2', 'se', '项目经理', 2, '0', '0', 0, 'admin', '2024-01-29 13:22:17.990', 'admin', '2024-01-29 13:22:17.990','');
INSERT INTO sys_post VALUES ('3', 'hr', '人力资源', 3, '0', '0', 0, 'admin', '2024-01-29 13:22:18.016', 'admin', '2024-01-29 13:22:18.016','');
INSERT INTO sys_post VALUES ('4', 'user', '普通员工', 4, '0', '0', 0, 'admin', '2024-01-29 13:22:18.062', 'admin', '2024-01-29 13:22:18.062','');

-- 用户角色关系表
insert into sys_user_role VALUES ('1', '1');
insert into sys_user_role VALUES ('2', '2');

-- 用户岗位关系表
insert into sys_user_post VALUES ('1', '1');

-- 用户菜单关系表
insert into sys_role_menu VALUES ('2', '1');
insert into sys_role_menu VALUES ('2', '2');
insert into sys_role_menu VALUES ('2', '3');
insert into sys_role_menu VALUES ('2', '4');

-- 一级菜单
INSERT INTO sys_menu VALUES ('1', '系统管理', '0', 'system', NULL, '', '1', '0', 'M', '0', 'system', '', 1, '0', '0', 0, 'admin','2024-01-31 09:37:31.560', 'admin', '2024-03-05 11:10:40.548', '系统管理目录');
INSERT INTO sys_menu VALUES ('2', '系统监控', '0', 'monitor', NULL, '', '1', '0', 'M', '0', 'monitor', '', 2, '0', '0', 0, 'admin','2024-01-31 09:37:31.592', 'admin', '2024-03-04 19:25:45.492', '系统监控目录');
INSERT INTO sys_menu VALUES ('3', '系统工具', '0', 'tool', NULL, '', '1', '0', 'M', '0', 'tool', '', 3, '0', '0', 0, 'admin','2024-01-31 09:37:31.625', 'admin', '2024-03-04 19:25:50.153', '系统工具目录');
INSERT INTO sys_menu VALUES ('4', '流程引擎', '0', 'flowEngine', NULL, NULL, '1', '0', 'M', '0', 'server', NULL, 6, '0', '0', 0, 'admin','2024-03-05 18:15:47.604', '', NULL, NULL);
INSERT INTO sys_menu VALUES ('5', '规则引擎', '0', 'ruleEngine', NULL, NULL, '1', '0', 'M', '0', 'client', '', 4, '0', '0', 0, 'admin','2024-03-05 11:12:56.902', 'admin', '2024-03-05 16:13:35.884', NULL);
INSERT INTO sys_menu VALUES ('6', '数据模转', '0', 'dataEtl', NULL, NULL, '1', '0', 'M', '0', 'money', NULL, 5, '0', '0', 0, 'admin','2024-03-05 18:08:52.556', '', NULL, NULL);

-- 二级菜单
INSERT INTO sys_menu VALUES ('100', '用户管理', '1', 'user', 'system/user/index', '', '1', '0', 'C', '0', 'user', 'system:user:list', 1, '0','0', 0, 'admin', '2024-01-31 09:37:31.687', '', NULL, '用户管理菜单');
INSERT INTO sys_menu VALUES ('101', '角色管理', '1', 'role', 'system/role/index', '', '1', '0', 'C', '0', 'peoples', 'system:role:list', 2,'0', '0', 0, 'admin', '2024-01-31 09:37:31.735', '', NULL, '角色管理菜单');
INSERT INTO sys_menu VALUES ('102', '菜单管理', '1', 'menu', 'system/menu/index', '', '1', '0', 'C', '0', 'tree-table', 'system:menu:list',3, '0', '0', 0, 'admin', '2024-01-31 09:37:31.765', '', NULL, '菜单管理菜单');
INSERT INTO sys_menu VALUES ('103', '部门管理', '1', 'dept', 'system/dept/index', '', '1', '0', 'C', '0', 'tree', 'system:dept:list', 4, '0','0', 0, 'admin', '2024-01-31 09:37:31.825', '', NULL, '部门管理菜单');
INSERT INTO sys_menu VALUES ('104', '岗位管理', '1', 'post', 'system/post/index', '', '1', '0', 'C', '0', 'post', 'system:post:list', 5, '0','0', 0, 'admin', '2024-01-31 09:37:31.862', '', NULL, '岗位管理菜单');
INSERT INTO sys_menu VALUES ('105', '字典管理', '1', 'dict', 'system/dict/index', '', '1', '0', 'C', '0', 'dict', 'system:dict:list', 6, '0','0', 0, 'admin', '2024-01-31 09:37:31.925', '', NULL, '字典管理菜单');
INSERT INTO sys_menu VALUES ('106', '参数设置', '1', 'config', 'system/config/index', '', '1', '0', 'C', '0', 'edit', 'system:config:list',7, '0', '0', 0, 'admin', '2024-01-31 09:37:31.964', '', NULL, '参数设置菜单');
INSERT INTO sys_menu VALUES ('107', '通知公告', '1', 'notice', 'system/notice/index', '', '1', '0', 'C', '0', 'message','system:notice:list', 8, '0', '0', 0, 'admin', '2024-01-31 09:37:31.981', '', NULL, '通知公告菜单');
INSERT INTO sys_menu VALUES ('108', '日志管理', '1', 'log', '', '', '1', '0', 'M', '0', 'log', '', 9, '0', '0', 0, 'admin','2024-01-31 09:37:31.998', '', NULL, '日志管理菜单');

INSERT INTO sys_menu VALUES ('115',  '代码生成','3', 'gen', 'tool/gen/index',     '', '1', '0', 'C', '0', 'code' ,'tool:gen:list', 1, '0','0', 0, 'admin', '2024-01-31 09:37:31.687', '', NULL, '代码生成菜单');

-- 三级菜单
INSERT INTO sys_menu VALUES ('500', '操作日志', '108', 'operlog', 'system/operlog/index', '', '1', '0', 'C', '0', 'form','system:operlog:list', 1, '0', '0', 0, 'admin', '2024-01-31 09:37:32.211', '', NULL, '操作日志菜单');
INSERT INTO sys_menu VALUES ('501', '登录日志', '108', 'logininfor', 'system/logininfor/index', '', '1', '0', 'C', '0', 'logininfor','system:logininfor:list', 2, '0', '0', 0, 'admin', '2024-01-31 09:37:32.244', '', NULL, '登录日志菜单');

-- 用户管理按钮
INSERT INTO sys_menu VALUES ('1000', '用户查询', '100', '', '', '', '1', '0', 'F', '0', '#', 'system:user:query', 1, '0', '0', 0, 'admin','2024-01-31 09:37:32.277', '', NULL, '');
INSERT INTO sys_menu VALUES ('1001', '用户新增', '100', '', '', '', '1', '0', 'F', '0', '#', 'system:user:add', 2, '0', '0', 0, 'admin','2024-01-31 09:37:32.306', '', NULL, '');
INSERT INTO sys_menu VALUES ('1002', '用户修改', '100', '', '', '', '1', '0', 'F', '0', '#', 'system:user:edit', 3, '0', '0', 0, 'admin','2024-01-31 09:37:32.331', '', NULL, '');
INSERT INTO sys_menu VALUES ('1003', '用户删除', '100', '', '', '', '1', '0', 'F', '0', '#', 'system:user:remove', 4, '0', '0', 0, 'admin','2024-01-31 09:37:32.360', '', NULL, '');
INSERT INTO sys_menu VALUES ('1004', '用户导出', '100', '', '', '', '1', '0', 'F', '0', '#', 'system:user:export', 5, '0', '0', 0, 'admin','2024-01-31 09:37:32.389', '', NULL, '');
INSERT INTO sys_menu VALUES ('1005', '用户导入', '100', '', '', '', '1', '0', 'F', '0', '#', 'system:user:import', 6, '0', '0', 0, 'admin','2024-01-31 09:37:32.415', '', NULL, '');
INSERT INTO sys_menu VALUES ('1006', '重置密码', '100', '', '', '', '1', '0', 'F', '0', '#', 'system:user:resetPwd', 7, '0', '0', 0, 'admin','2024-01-31 09:37:32.433', '', NULL, '');

-- 角色管理按钮
INSERT INTO sys_menu VALUES ('1007', '角色查询', '101', '', '', '', '1', '0', 'F', '0', '#', 'system:role:query', 1, '0', '0', 0, 'admin','2024-01-31 09:37:32.455', '', NULL, '');
INSERT INTO sys_menu VALUES ('1008', '角色新增', '101', '', '', '', '1', '0', 'F', '0', '#', 'system:role:add', 2, '0', '0', 0, 'admin','2024-01-31 09:37:32.483', '', NULL, '');
INSERT INTO sys_menu VALUES ('1009', '角色修改', '101', '', '', '', '1', '0', 'F', '0', '#', 'system:role:edit', 3, '0', '0', 0, 'admin','2024-01-31 09:37:32.503', '', NULL, '');
INSERT INTO sys_menu VALUES ('1010', '角色删除', '101', '', '', '', '1', '0', 'F', '0', '#', 'system:role:remove', 4, '0', '0', 0, 'admin','2024-01-31 09:37:32.528', '', NULL, '');
INSERT INTO sys_menu VALUES ('1011', '角色导出', '101', '', '', '', '1', '0', 'F', '0', '#', 'system:role:export', 5, '0', '0', 0, 'admin','2024-01-31 09:37:32.556', '', NULL, '');

-- 菜单管理按钮
INSERT INTO sys_menu VALUES ('1012', '菜单查询', '102', '', '', '', '1', '0', 'F', '0', '#', 'system:menu:query', 1, '0', '0', 0, 'admin','2024-01-31 09:37:32.578', '', NULL, '');
INSERT INTO sys_menu VALUES ('1013', '菜单新增', '102', '', '', '', '1', '0', 'F', '0', '#', 'system:menu:add', 2, '0', '0', 0, 'admin','2024-01-31 09:37:32.596', '', NULL, '');
INSERT INTO sys_menu VALUES ('1014', '菜单修改', '102', '', '', '', '1', '0', 'F', '0', '#', 'system:menu:edit', 3, '0', '0', 0, 'admin','2024-01-31 09:37:32.624', '', NULL, '');
INSERT INTO sys_menu VALUES ('1015', '菜单删除', '102', '', '', '', '1', '0', 'F', '0', '#', 'system:menu:remove', 4, '0', '0', 0, 'admin','2024-01-31 09:37:32.650', '', NULL, '');

-- 部门管理按钮
INSERT INTO sys_menu VALUES ('1016', '部门查询', '103', '', '', '', '1', '0', 'F', '0', '#', 'system:dept:query', 1, '0', '0', 0, 'admin','2024-01-31 09:37:32.682', '', NULL, '');
INSERT INTO sys_menu VALUES ('1017', '部门新增', '103', '', '', '', '1', '0', 'F', '0', '#', 'system:dept:add', 2, '0', '0', 0, 'admin','2024-01-31 09:37:32.700', '', NULL, '');
INSERT INTO sys_menu VALUES ('1018', '部门修改', '103', '', '', '', '1', '0', 'F', '0', '#', 'system:dept:edit', 3, '0', '0', 0, 'admin','2024-01-31 09:37:32.728', '', NULL, '');
INSERT INTO sys_menu VALUES ('1019', '部门删除', '103', '', '', '', '1', '0', 'F', '0', '#', 'system:dept:remove', 4, '0', '0', 0, 'admin','2024-01-31 09:37:32.808', '', NULL, '');

-- 岗位管理按钮
INSERT INTO sys_menu VALUES ('1020', '岗位查询', '104', '', '', '', '1', '0', 'F', '0', '#', 'system:post:query', 1, '0', '0', 0, 'admin','2024-01-31 09:37:32.829', '', NULL, '');
INSERT INTO sys_menu VALUES ('1021', '岗位新增', '104', '', '', '', '1', '0', 'F', '0', '#', 'system:post:add', 2, '0', '0', 0, 'admin','2024-01-31 09:37:32.856', '', NULL, '');
INSERT INTO sys_menu VALUES ('1022', '岗位修改', '104', '', '', '', '1', '0', 'F', '0', '#', 'system:post:edit', 3, '0', '0', 0, 'admin','2024-01-31 09:37:32.880', '', NULL, '');
INSERT INTO sys_menu VALUES ('1023', '岗位删除', '104', '', '', '', '1', '0', 'F', '0', '#', 'system:post:remove', 4, '0', '0', 0, 'admin','2024-01-31 09:37:32.905', '', NULL, '');
INSERT INTO sys_menu VALUES ('1024', '岗位导出', '104', '', '', '', '1', '0', 'F', '0', '#', 'system:post:export', 5, '0', '0', 0, 'admin','2024-01-31 09:37:32.931', '', NULL, '');

-- 字典管理按钮
INSERT INTO sys_menu VALUES ('1025', '字典查询', '105', '#', '', '', '1', '0', 'F', '0', '#', 'system:dict:query', 1, '0', '0', 0, 'admin','2024-01-31 09:37:32.955', '', NULL, '');
INSERT INTO sys_menu VALUES ('1026', '字典新增', '105', '#', '', '', '1', '0', 'F', '0', '#', 'system:dict:add', 2, '0', '0', 0, 'admin','2024-01-31 09:37:32.973', '', NULL, '');
INSERT INTO sys_menu VALUES ('1027', '字典修改', '105', '#', '', '', '1', '0', 'F', '0', '#', 'system:dict:edit', 3, '0', '0', 0, 'admin','2024-01-31 09:37:33.004', '', NULL, '');
INSERT INTO sys_menu VALUES ('1028', '字典删除', '105', '#', '', '', '1', '0', 'F', '0', '#', 'system:dict:remove', 4, '0', '0', 0, 'admin','2024-01-31 09:37:33.036', '', NULL, '');
INSERT INTO sys_menu VALUES ('1029', '字典导出', '105', '#', '', '', '1', '0', 'F', '0', '#', 'system:dict:export', 5, '0', '0', 0, 'admin','2024-01-31 09:37:33.051', '', NULL, '');

-- 参数设置按钮
INSERT INTO sys_menu VALUES ('1030', '参数查询', '106', '#', '', '', '1', '0', 'F', '0', '#', 'system:config:query', 1, '0', '0', 0, 'admin','2024-01-31 09:37:33.086', '', NULL, '');
INSERT INTO sys_menu VALUES ('1031', '参数新增', '106', '#', '', '', '1', '0', 'F', '0', '#', 'system:config:add', 2, '0', '0', 0, 'admin','2024-01-31 09:37:33.116', '', NULL, '');
INSERT INTO sys_menu VALUES ('1032', '参数修改', '106', '#', '', '', '1', '0', 'F', '0', '#', 'system:config:edit', 3, '0', '0', 0, 'admin','2024-01-31 09:37:33.131', '', NULL, '');
INSERT INTO sys_menu VALUES ('1033', '参数删除', '106', '#', '', '', '1', '0', 'F', '0', '#', 'system:config:remove', 4, '0', '0', 0,'admin', '2024-01-31 09:37:33.148', '', NULL, '');
INSERT INTO sys_menu VALUES ('1034', '参数导出', '106', '#', '', '', '1', '0', 'F', '0', '#', 'system:config:export', 5, '0', '0', 0,'admin', '2024-01-31 09:37:33.175', '', NULL, '');

-- 通知公告按钮
INSERT INTO sys_menu VALUES ('1035', '公告查询', '107', '#', '', '', '1', '0', 'F', '0', '#', 'system:notice:query', 1, '0', '0', 0, 'admin','2024-01-31 09:37:33.203', '', NULL, '');
INSERT INTO sys_menu VALUES ('1036', '公告新增', '107', '#', '', '', '1', '0', 'F', '0', '#', 'system:notice:add', 2, '0', '0', 0, 'admin','2024-01-31 09:37:33.227', '', NULL, '');
INSERT INTO sys_menu VALUES ('1037', '公告修改', '107', '#', '', '', '1', '0', 'F', '0', '#', 'system:notice:edit', 3, '0', '0', 0, 'admin','2024-01-31 09:37:33.244', '', NULL, '');
INSERT INTO sys_menu VALUES ('1038', '公告删除', '107', '#', '', '', '1', '0', 'F', '0', '#', 'system:notice:remove', 4, '0', '0', 0,'admin', '2024-01-31 09:37:33.260', '', NULL, '');

-- 操作日志按钮
INSERT INTO sys_menu VALUES ('1039', '操作查询', '500', '#', '', '', '1', '0', 'F', '0', '#', 'system:operlog:query', 1, '0', '0', 0,'admin', '2024-01-31 09:37:33.280', '', NULL, '');
INSERT INTO sys_menu VALUES ('1040', '操作删除', '500', '#', '', '', '1', '0', 'F', '0', '#', 'system:operlog:remove', 2, '0', '0', 0,'admin', '2024-01-31 09:37:33.304', '', NULL, '');
INSERT INTO sys_menu VALUES ('1041', '日志导出', '500', '#', '', '', '1', '0', 'F', '0', '#', 'system:operlog:export', 3, '0', '0', 0,'admin', '2024-01-31 09:37:33.325', '', NULL, '');

-- 登录日志按钮
INSERT INTO sys_menu VALUES ('1042', '登录查询', '501', '#', '', '', '1', '0', 'F', '0', '#', 'system:logininfor:query', 1, '0', '0', 0,'admin', '2024-01-31 09:37:33.345', '', NULL, '');
INSERT INTO sys_menu VALUES ('1043', '登录删除', '501', '#', '', '', '1', '0', 'F', '0', '#', 'system:logininfor:remove', 2, '0', '0', 0,'admin', '2024-01-31 09:37:33.369', '', NULL, '');
INSERT INTO sys_menu VALUES ('1044', '日志导出', '501', '#', '', '', '1', '0', 'F', '0', '#', 'system:logininfor:export', 3, '0', '0', 0,'admin', '2024-01-31 09:37:33.401', '', NULL, '');
INSERT INTO sys_menu VALUES ('1045', '账户解锁', '501', '#', '', '', '1', '0', 'F', '0', '#', 'system:logininfor:unlock', 4, '0', '0', 0,'admin', '2024-01-31 09:37:33.434', '', NULL, '');


-- 定时任务按钮
INSERT INTO sys_menu VALUES ('1049', '任务查询', '110', '#', '', '', '1', '0', 'F', '0', '#', 'monitor:job:query', 1, '0', '0', 0, 'admin','2024-01-31 09:37:33.538', '', NULL, '');
INSERT INTO sys_menu VALUES ('1050', '任务新增', '110', '#', '', '', '1', '0', 'F', '0', '#', 'monitor:job:add', 2, '0', '0', 0, 'admin','2024-01-31 09:37:33.574', '', NULL, '');
INSERT INTO sys_menu VALUES ('1051', '任务修改', '110', '#', '', '', '1', '0', 'F', '0', '#', 'monitor:job:edit', 3, '0', '0', 0, 'admin','2024-01-31 09:37:33.613', '', NULL, '');
INSERT INTO sys_menu VALUES ('1052', '任务删除', '110', '#', '', '', '1', '0', 'F', '0', '#', 'monitor:job:remove', 4, '0', '0', 0, 'admin','2024-01-31 09:37:33.641', '', NULL, '');
INSERT INTO sys_menu VALUES ('1053', '状态修改', '110', '#', '', '', '1', '0', 'F', '0', '#', 'monitor:job:changeStatus', 5, '0', '0', 0,'admin', '2024-01-31 09:37:33.672', '', NULL, '');
INSERT INTO sys_menu VALUES ('1054', '任务导出', '110', '#', '', '', '1', '0', 'F', '0', '#', 'monitor:job:export', 6, '0', '0', 0, 'admin','2024-01-31 09:37:33.717', '', NULL, '');

-- 代码生成按钮
INSERT INTO sys_menu VALUES ('1055', '生成查询', '115', '#', '', '', '1', '0', 'F', '0', '#', 'tool:gen:query', 1, '0', '0', 0, 'admin','2024-01-31 09:37:33.752', '', NULL, '');
INSERT INTO sys_menu VALUES ('1056', '生成修改', '115', '#', '', '', '1', '0', 'F', '0', '#', 'tool:gen:edit', 2, '0', '0', 0, 'admin','2024-01-31 09:37:33.770', '', NULL, '');
INSERT INTO sys_menu VALUES ('1057', '生成删除', '115', '#', '', '', '1', '0', 'F', '0', '#', 'tool:gen:remove', 3, '0', '0', 0, 'admin','2024-01-31 09:37:33.785', '', NULL, '');
INSERT INTO sys_menu VALUES ('1058', '导入代码', '115', '#', '', '', '1', '0', 'F', '0', '#', 'tool:gen:import', 2, '0', '0', 0, 'admin','2024-01-31 09:37:33.802', '', NULL, '');
INSERT INTO sys_menu VALUES ('1059', '预览代码', '115', '#', '', '', '1', '0', 'F', '0', '#', 'tool:gen:preview', 4, '0', '0', 0, 'admin','2024-01-31 09:37:33.829', '', NULL, '');
INSERT INTO sys_menu VALUES ('1060', '生成代码', '115', '#', '', '', '1', '0', 'F', '0', '#', 'tool:gen:code', 5, '0', '0', 0, 'admin','2024-01-31 09:37:33.868', '', NULL, '');

-- 字典类型表
INSERT INTO sys_dict_type VALUES ('1', '用户性别', 'sys_user_gender', 1, '0', '0', 0, 'admin', '2024-02-27 12:55:21.967', 'admin','2024-03-05 15:31:09.817', '用户性别列表');
INSERT INTO sys_dict_type VALUES ('2', '菜单状态', 'sys_menu_status', 2, '0', '0', 0, 'admin', '2024-02-27 12:55:49.238', 'admin','2024-03-05 16:48:15.191', '菜单状态列表');
INSERT INTO sys_dict_type VALUES ('3', '系统开关', 'sys_switch', 3, '0', '0', 0, 'admin', '2024-02-27 12:55:49.277', 'admin','2024-03-05 16:56:30.584', '系统开关列表');
INSERT INTO sys_dict_type VALUES ('4', '任务状态', 'sys_job_status', 4, '0', '0', 0, 'admin', '2024-02-27 12:55:49.317', '', NULL,'任务状态列表');
INSERT INTO sys_dict_type VALUES ('5', '任务分组', 'sys_job_group', 5, '0', '0', 0, 'admin', '2024-02-27 12:55:49.341', '', NULL, '任务分组列表');
INSERT INTO sys_dict_type VALUES ('6', '系统是否', 'sys_whether', 6, '0', '0', 0, 'admin', '2024-02-27 12:55:49.368', 'admin','2024-03-05 17:03:26.534', '系统是否列表');
INSERT INTO sys_dict_type VALUES ('7', '通知类型', 'sys_notice_type', 7, '0', '0', 0, 'admin', '2024-02-27 12:55:49.395', '', NULL,'通知类型列表');
INSERT INTO sys_dict_type VALUES ('8', '通知状态', 'sys_notice_status', 8, '0', '0', 0, 'admin', '2024-02-27 12:55:49.424', '', NULL,'通知状态列表');
INSERT INTO sys_dict_type VALUES ('9', '操作类型', 'sys_oper_type', 9, '0', '0', 0, 'admin', '2024-02-27 12:55:49.483', '', NULL, '操作类型列表');
INSERT INTO sys_dict_type VALUES ('10', '系统状态', 'sys_common_status', 10, '0', '0', 0, 'admin', '2024-02-27 12:55:49.502', '', NULL,'登录状态列表');


-- 字典数据表
INSERT INTO sys_dict_data VALUES ('1', 'sys_user_gender', '男', '0', '', '', 'Y', 1, '0', '0', 0, 'admin', '2024-02-28 11:37:53.248', '', NULL,'性别男');
INSERT INTO sys_dict_data VALUES ('2', 'sys_user_gender', '女', '1', '', '', 'N', 2, '0', '0', 0, 'admin', '2024-02-28 11:37:53.295', '', NULL,'性别女');
INSERT INTO sys_dict_data VALUES ('5', 'sys_menu_status', '隐藏', '1', '', 'danger', 'N', 2, '0', '0', 0, 'admin', '2024-02-28 11:37:53.438', '',NULL, '隐藏菜单');
INSERT INTO sys_dict_data VALUES ('3', 'sys_user_gender', '未知', '2', '', '', 'N', 3, '0', '0', 0, 'admin', '2024-02-28 11:37:53.349', '', NULL,'性别未知');
INSERT INTO sys_dict_data VALUES ('4', 'sys_menu_status', '显示', '0', '', 'primary', 'Y', 4, '0', '0', 0, 'admin', '2024-02-28 11:37:53.390', '',NULL, '显示菜单');
INSERT INTO sys_dict_data VALUES ('6', 'sys_switch', '正常', '0', '', 'primary', 'Y', 6, '0', '0', 0, 'admin', '2024-02-28 11:37:53.511', '',NULL, '正常状态');
INSERT INTO sys_dict_data VALUES ('7', 'sys_switch', '停用', '1', '', 'danger', 'N', 7, '0', '0', 0, 'admin', '2024-02-28 11:37:53.565', '', NULL,'停用状态');
INSERT INTO sys_dict_data VALUES ('8', 'sys_job_status', '正常', '0', '', 'primary', 'Y', 8, '0', '0', 0, 'admin', '2024-02-28 11:37:53.629', '',NULL, '正常状态');
INSERT INTO sys_dict_data VALUES ('9', 'sys_job_status', '暂停', '1', '', 'danger', 'N', 9, '0', '0', 0, 'admin', '2024-02-28 11:37:53.700', '',NULL, '停用状态');
INSERT INTO sys_dict_data VALUES ('10', 'sys_job_group', '默认', 'DEFAULT', '', '', 'Y', 10, '0', '0', 0, 'admin', '2024-02-28 11:37:53.767', '',NULL, '默认分组');
INSERT INTO sys_dict_data VALUES ('11', 'sys_job_group', '系统', 'SYSTEM', '', '', 'N', 11, '0', '0', 0, 'admin', '2024-02-28 11:37:53.854', '',NULL, '系统分组');
INSERT INTO sys_dict_data VALUES ('12', 'sys_whether', '是', 'Y', '', 'primary', 'Y', 12, '0', '0', 0, 'admin', '2024-02-28 11:37:53.905', '',NULL, '系统默认是');
INSERT INTO sys_dict_data VALUES ('13', 'sys_whether', '否', 'N', '', 'danger', 'N', 13, '0', '0', 0, 'admin', '2024-02-28 11:37:53.979', '',NULL, '系统默认否');
INSERT INTO sys_dict_data VALUES ('14', 'sys_notice_type', '通知', '1', '', 'warning', 'Y', 14, '0', '0', 0, 'admin', '2024-02-28 11:37:54.075','', NULL, '通知');
INSERT INTO sys_dict_data VALUES ('15', 'sys_notice_type', '公告', '2', '', 'success', 'N', 15, '0', '0', 0, 'admin', '2024-02-28 11:37:54.172','', NULL, '公告');
INSERT INTO sys_dict_data VALUES ('16', 'sys_notice_status', '正常', '0', '', 'primary', 'Y', 16, '0', '0', 0, 'admin', '2024-02-28 11:37:54.273','', NULL, '正常状态');
INSERT INTO sys_dict_data VALUES ('17', 'sys_notice_status', '关闭', '1', '', 'danger', 'N', 17, '0', '0', 0, 'admin', '2024-02-28 11:37:54.309','', NULL, '关闭状态');
INSERT INTO sys_dict_data VALUES ('18', 'sys_oper_type', '其他', '0', '', 'info', 'N', 18, '0', '0', 0, 'admin', '2024-02-28 11:37:54.355', '',NULL, '其他操作');
INSERT INTO sys_dict_data VALUES ('19', 'sys_oper_type', '新增', '1', '', 'info', 'N', 19, '0', '0', 0, 'admin', '2024-02-28 11:37:54.372', '',NULL, '新增操作');
INSERT INTO sys_dict_data VALUES ('20', 'sys_oper_type', '修改', '2', '', 'info', 'N', 20, '0', '0', 0, 'admin', '2024-02-28 11:37:54.414', '',NULL, '修改操作');
INSERT INTO sys_dict_data VALUES ('21', 'sys_oper_type', '删除', '3', '', 'danger', 'N', 21, '0', '0', 0, 'admin', '2024-02-28 11:37:54.445', '',NULL, '删除操作');
INSERT INTO sys_dict_data VALUES ('22', 'sys_oper_type', '授权', '4', '', 'primary', 'N', 22, '0', '0', 0, 'admin', '2024-02-28 11:37:54.462', '',NULL, '授权操作');
INSERT INTO sys_dict_data VALUES ('23', 'sys_oper_type', '导出', '5', '', 'warning', 'N', 23, '0', '0', 0, 'admin', '2024-02-28 11:37:54.484', '',NULL, '导出操作');
INSERT INTO sys_dict_data VALUES ('24', 'sys_oper_type', '导入', '6', '', 'warning', 'N', 24, '0', '0', 0, 'admin', '2024-02-28 11:37:54.512', '',NULL, '导入操作');
INSERT INTO sys_dict_data VALUES ('25', 'sys_oper_type', '强退', '7', '', 'danger', 'N', 25, '0', '0', 0, 'admin', '2024-02-28 11:37:54.534', '',NULL, '强退操作');
INSERT INTO sys_dict_data VALUES ('26', 'sys_oper_type', '生成代码', '8', '', 'warning', 'N', 26, '0', '0', 0, 'admin', '2024-02-28 11:37:54.555','', NULL, '生成操作');
INSERT INTO sys_dict_data VALUES ('27', 'sys_oper_type', '清空数据', '9', '', 'danger', 'N', 27, '0', '0', 0, 'admin', '2024-02-28 11:37:54.579','', NULL, '清空操作');
INSERT INTO sys_dict_data VALUES ('28', 'sys_common_status', '成功', '0', '', 'primary', 'N', 28, '0', '0', 0, 'admin', '2024-02-28 11:37:54.637','', NULL, '正常状态');
INSERT INTO sys_dict_data VALUES ('29', 'sys_common_status', '失败', '1', '', 'danger', 'N', 29, '0', '0', 0, 'admin', '2024-02-28 11:37:54.685','', NULL, '停用状态');

-- 系统配置
INSERT INTO sys_config VALUES ('1', '主框架页-默认皮肤样式名称', 'sys.index.skinName', 'skin-blue', 'Y', 1, '0', '0', 0, 'admin','2024-02-28 11:59:14.610', '', NULL,'蓝色 skin-blue、绿色 skin-green、紫色 skin-purple、红色 skin-red、黄色 skin-yellow');
INSERT INTO sys_config VALUES ('2', '用户管理-账号初始密码', 'sys.user.initPassword', '123456', 'Y', 1, '0', '0', 0, 'admin','2024-02-28 11:59:41.708', '', NULL, '初始化密码 123456');
INSERT INTO sys_config VALUES ('3', '主框架页-侧边栏主题', 'sys.index.sideTheme', 'theme-dark', 'Y', 1, '0', '0', 0, 'admin','2024-02-28 11:59:41.727', '', NULL, '深色主题theme-dark，浅色主题theme-light');
INSERT INTO sys_config VALUES ('4', '账号自助-是否开启用户注册功能', 'sys.account.registerUser', 'false', 'Y', 1, '0', '0', 0, 'admin','2024-02-28 11:59:41.776', '', NULL, '是否开启注册用户功能（true开启，false关闭）');
INSERT INTO sys_config VALUES ('5', '用户登录-黑名单列表', 'sys.login.blackIPList', '', 'Y', 1, '0', '0', 0, 'admin','2024-02-28 11:59:41.822', '', NULL, '设置登录IP黑名单限制，多个匹配项以;分隔，支持匹配（*通配、网段）');