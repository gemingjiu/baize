DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user(
                         id VARCHAR(32) NOT NULL,
                         dept_id VARCHAR(32) NOT NULL,
                         user_name VARCHAR(50) NOT NULL,
                         nick_name VARCHAR(50),
                         user_type INTEGER NOT NULL DEFAULT  '00',
                         email VARCHAR(50) NOT NULL DEFAULT  '',
                         phone VARCHAR(11) NOT NULL DEFAULT  '',
                         gender VARCHAR(1) NOT NULL DEFAULT  '0',
                         avatar VARCHAR(100) DEFAULT  '',
                         password VARCHAR(100) NOT NULL DEFAULT  '',
                         sort INTEGER NOT NULL,
                         login_ip VARCHAR(128) DEFAULT  '',
                         login_date TIMESTAMP,
                         status VARCHAR(1) NOT NULL DEFAULT  '0',
                         deleted VARCHAR(1) NOT NULL DEFAULT  '0',
                         version INTEGER NOT NULL DEFAULT  0,
                         created_by VARCHAR(50) NOT NULL,
                         created_time TIMESTAMP NOT NULL,
                         modified_by VARCHAR(50),
                         modified_time TIMESTAMP,
                         remark VARCHAR(500),
                         PRIMARY KEY (id)
);

COMMENT ON TABLE sys_user IS '用户信息表';
COMMENT ON COLUMN sys_user.id IS '主键ID';
COMMENT ON COLUMN sys_user.dept_id IS '部门ID';
COMMENT ON COLUMN sys_user.user_name IS '用户账号';
COMMENT ON COLUMN sys_user.nick_name IS '用户昵称';
COMMENT ON COLUMN sys_user.user_type IS '用户类型';
COMMENT ON COLUMN sys_user.email IS '用户邮箱';
COMMENT ON COLUMN sys_user.phone IS '手机号码';
COMMENT ON COLUMN sys_user.gender IS '用户性别';
COMMENT ON COLUMN sys_user.avatar IS '用户头像';
COMMENT ON COLUMN sys_user.password IS '密码';
COMMENT ON COLUMN sys_user.sort IS '显示顺序';
COMMENT ON COLUMN sys_user.login_ip IS '最后登录IP';
COMMENT ON COLUMN sys_user.login_date IS '最后登录时间';
COMMENT ON COLUMN sys_user.status IS '记录状态;0:记录有效,1:记录无效';
COMMENT ON COLUMN sys_user.deleted IS '删除标记;0:正常标记,1:删除标记';
COMMENT ON COLUMN sys_user.version IS '乐观锁';
COMMENT ON COLUMN sys_user.created_by IS '创建人';
COMMENT ON COLUMN sys_user.created_time IS '创建时间';
COMMENT ON COLUMN sys_user.modified_by IS '更新人';
COMMENT ON COLUMN sys_user.modified_time IS '更新时间';
COMMENT ON COLUMN sys_user.remark IS '备注';


-- CREATE UNIQUE INDEX sys_user_pkey ON sys_user(id);

DROP TABLE IF EXISTS sys_post;
CREATE TABLE sys_post(
                         id VARCHAR(32) NOT NULL,
                         post_code VARCHAR(64) NOT NULL,
                         post_name VARCHAR(50) NOT NULL,
                         sort INTEGER NOT NULL,
                         status VARCHAR(1) NOT NULL DEFAULT  '0',
                         deleted VARCHAR(1) NOT NULL DEFAULT  '0',
                         version INTEGER NOT NULL DEFAULT  0,
                         created_by VARCHAR(50) NOT NULL DEFAULT  '',
                         created_time TIMESTAMP NOT NULL,
                         modified_by VARCHAR(50) DEFAULT  '',
                         modified_time TIMESTAMP,
                         remark VARCHAR(500),
                         PRIMARY KEY (id)
);

COMMENT ON TABLE sys_post IS '岗位信息表';
COMMENT ON COLUMN sys_post.id IS '岗位ID';
COMMENT ON COLUMN sys_post.post_code IS '岗位编码';
COMMENT ON COLUMN sys_post.post_name IS '岗位名称';
COMMENT ON COLUMN sys_post.sort IS '岗位顺序';
COMMENT ON COLUMN sys_post.status IS '记录状态;0:记录有效,1:记录无效';
COMMENT ON COLUMN sys_post.deleted IS '删除标记;0:正常标记,1:删除标记';
COMMENT ON COLUMN sys_post.version IS '乐观锁';
COMMENT ON COLUMN sys_post.created_by IS '创建人';
COMMENT ON COLUMN sys_post.created_time IS '创建时间';
COMMENT ON COLUMN sys_post.modified_by IS '更新人';
COMMENT ON COLUMN sys_post.modified_time IS '更新时间';
COMMENT ON COLUMN sys_post.remark IS '备注';


-- CREATE UNIQUE INDEX sys_post_pkey ON sys_post(id);

DROP TABLE IF EXISTS sys_role;
CREATE TABLE sys_role(
                         id VARCHAR(32) NOT NULL,
                         role_name VARCHAR(50) NOT NULL,
                         role_code VARCHAR(100),
                         data_scope VARCHAR(1) DEFAULT  '1',
                         menu_check_strictly VARCHAR(1) DEFAULT  '1',
                         dept_check_strictly VARCHAR(1) DEFAULT  '1',
                         sort INTEGER NOT NULL,
                         status VARCHAR(1) NOT NULL DEFAULT  '0',
                         deleted VARCHAR(1) NOT NULL DEFAULT  '0',
                         version INTEGER NOT NULL DEFAULT  0,
                         created_by VARCHAR(50) NOT NULL DEFAULT  '',
                         created_time TIMESTAMP NOT NULL,
                         modified_by VARCHAR(50) DEFAULT  '',
                         modified_time TIMESTAMP,
                         remark VARCHAR(500),
                         PRIMARY KEY (id)
);

COMMENT ON TABLE sys_role IS '角色信息表';
COMMENT ON COLUMN sys_role.id IS '主键ID';
COMMENT ON COLUMN sys_role.role_name IS '角色名称';
COMMENT ON COLUMN sys_role.role_code IS '角色编码;全局唯一标识slug';
COMMENT ON COLUMN sys_role.data_scope IS '数据范围;1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限';
COMMENT ON COLUMN sys_role.menu_check_strictly IS '菜单树选择项是否关联显示';
COMMENT ON COLUMN sys_role.dept_check_strictly IS '部门树选择项是否关联显示';
COMMENT ON COLUMN sys_role.sort IS '显示顺序';
COMMENT ON COLUMN sys_role.status IS '记录状态;0:记录有效,1:记录无效';
COMMENT ON COLUMN sys_role.deleted IS '删除标记;0:正常标记,1:删除标记';
COMMENT ON COLUMN sys_role.version IS '乐观锁';
COMMENT ON COLUMN sys_role.created_by IS '创建人';
COMMENT ON COLUMN sys_role.created_time IS '创建时间';
COMMENT ON COLUMN sys_role.modified_by IS '更新人';
COMMENT ON COLUMN sys_role.modified_time IS '更新时间';
COMMENT ON COLUMN sys_role.remark IS '备注';


-- CREATE UNIQUE INDEX sys_role_pkey ON sys_role(id);

DROP TABLE IF EXISTS sys_menu;
CREATE TABLE sys_menu(
                         id VARCHAR(32) NOT NULL,
                         menu_name VARCHAR(50) NOT NULL,
                         parent_id VARCHAR(32) NOT NULL DEFAULT  '',
                         path VARCHAR(200) NOT NULL DEFAULT  '',
                         component VARCHAR(255),
                         parameters VARCHAR(255),
                         external VARCHAR(1) NOT NULL DEFAULT  '0',
                         cacheable VARCHAR(1) NOT NULL DEFAULT  '0',
                         menu_type VARCHAR(1) NOT NULL DEFAULT  '',
                         visible VARCHAR(1) NOT NULL DEFAULT  '0',
                         icon VARCHAR(100) DEFAULT  '#',
                         sort INTEGER NOT NULL DEFAULT  0,
                         status VARCHAR(1) NOT NULL DEFAULT  '0',
                         deleted VARCHAR(1) NOT NULL DEFAULT  '0',
                         version INTEGER NOT NULL DEFAULT  0,
                         created_by VARCHAR(50) NOT NULL DEFAULT  '',
                         created_time TIMESTAMP NOT NULL,
                         modified_by VARCHAR(50) DEFAULT  '',
                         modified_time TIMESTAMP,
                         remark VARCHAR(500),
                         PRIMARY KEY (id)
);

COMMENT ON TABLE sys_menu IS '菜单信息表';
COMMENT ON COLUMN sys_menu.id IS '主键ID';
COMMENT ON COLUMN sys_menu.menu_name IS '菜单名称';
COMMENT ON COLUMN sys_menu.parent_id IS '父菜单ID';
COMMENT ON COLUMN sys_menu.path IS '路由地址';
COMMENT ON COLUMN sys_menu.component IS '组件路径';
COMMENT ON COLUMN sys_menu.parameters IS '路由参数';
COMMENT ON COLUMN sys_menu.external IS '是否外链;0:内链,1:外链';
COMMENT ON COLUMN sys_menu.cacheable IS '是否缓存;0:不缓存,1:缓存';
COMMENT ON COLUMN sys_menu.menu_type IS '菜单类型;0:目录,1:菜单，2:按钮';
COMMENT ON COLUMN sys_menu.visible IS '菜单状态;0:显示1:隐藏';
COMMENT ON COLUMN sys_menu.icon IS '菜单图标';
COMMENT ON COLUMN sys_menu.sort IS '显示顺序';
COMMENT ON COLUMN sys_menu.status IS '记录状态;0:记录有效,1:记录无效';
COMMENT ON COLUMN sys_menu.deleted IS '删除标记;0:正常标记,1:删除标记';
COMMENT ON COLUMN sys_menu.version IS '乐观锁';
COMMENT ON COLUMN sys_menu.created_by IS '创建人';
COMMENT ON COLUMN sys_menu.created_time IS '创建时间';
COMMENT ON COLUMN sys_menu.modified_by IS '更新人';
COMMENT ON COLUMN sys_menu.modified_time IS '更新时间';
COMMENT ON COLUMN sys_menu.remark IS '备注';


-- CREATE UNIQUE INDEX sys_menu_pkey ON sys_menu(id);

DROP TABLE IF EXISTS sys_dept;
CREATE TABLE sys_dept(
                         id VARCHAR(32) NOT NULL,
                         parent_id VARCHAR(32) NOT NULL DEFAULT  '',
                         ancestors VARCHAR(64) DEFAULT  '',
                         dept_name VARCHAR(255),
                         leader VARCHAR(32),
                         phone VARCHAR(11),
                         email VARCHAR(50),
                         sort INTEGER NOT NULL,
                         status VARCHAR(1) NOT NULL DEFAULT  '0',
                         deleted VARCHAR(1) NOT NULL DEFAULT  '0',
                         version INTEGER NOT NULL DEFAULT  0,
                         created_by VARCHAR(50) NOT NULL DEFAULT  '',
                         created_time TIMESTAMP NOT NULL,
                         modified_by VARCHAR(50) DEFAULT  '',
                         modified_time TIMESTAMP,
                         remark VARCHAR(500),
                         PRIMARY KEY (id)
);

COMMENT ON TABLE sys_dept IS '部门信息表';
COMMENT ON COLUMN sys_dept.id IS '主键ID';
COMMENT ON COLUMN sys_dept.parent_id IS '父部门id';
COMMENT ON COLUMN sys_dept.ancestors IS '祖级列表';
COMMENT ON COLUMN sys_dept.dept_name IS '部门名称';
COMMENT ON COLUMN sys_dept.leader IS '负责人';
COMMENT ON COLUMN sys_dept.phone IS '电话';
COMMENT ON COLUMN sys_dept.email IS '邮箱';
COMMENT ON COLUMN sys_dept.sort IS '显示顺序';
COMMENT ON COLUMN sys_dept.status IS '记录状态;0:记录有效,1:记录无效';
COMMENT ON COLUMN sys_dept.deleted IS '删除标记;0:正常标记,1:删除标记';
COMMENT ON COLUMN sys_dept.version IS '乐观锁';
COMMENT ON COLUMN sys_dept.created_by IS '创建人';
COMMENT ON COLUMN sys_dept.created_time IS '创建时间';
COMMENT ON COLUMN sys_dept.modified_by IS '更新人';
COMMENT ON COLUMN sys_dept.modified_time IS '更新时间';
COMMENT ON COLUMN sys_dept.remark IS '备注';


-- CREATE UNIQUE INDEX sys_dept_pkey ON sys_dept(id);

DROP TABLE IF EXISTS sys_user_role;
CREATE TABLE sys_user_role(
                              user_id VARCHAR(32) NOT NULL,
                              role_id VARCHAR(32) NOT NULL
);

COMMENT ON TABLE sys_user_role IS '用户和角色关联表N-1';
COMMENT ON COLUMN sys_user_role.user_id IS '用户ID';
COMMENT ON COLUMN sys_user_role.role_id IS '角色ID';

DROP TABLE IF EXISTS sys_role_dept;
CREATE TABLE sys_role_dept(
                              role_id VARCHAR(32) NOT NULL,
                              dept_id VARCHAR(32) NOT NULL
);

COMMENT ON TABLE sys_role_dept IS '角色和部门关联表1-N';
COMMENT ON COLUMN sys_role_dept.role_id IS '角色ID';
COMMENT ON COLUMN sys_role_dept.dept_id IS '部门ID';

DROP TABLE IF EXISTS sys_role_menu;
CREATE TABLE sys_role_menu(
                              role_id VARCHAR(32) NOT NULL,
                              menu_id VARCHAR(32) NOT NULL
);

COMMENT ON TABLE sys_role_menu IS '角色和菜单N-N';
COMMENT ON COLUMN sys_role_menu.role_id IS '角色ID';
COMMENT ON COLUMN sys_role_menu.menu_id IS '菜单ID';

DROP TABLE IF EXISTS sys_role_permission;
CREATE TABLE sys_role_permission(
                                    role_id VARCHAR(32) NOT NULL,
                                    permission_id VARCHAR(32) NOT NULL
);

COMMENT ON TABLE sys_role_permission IS '角色和权限关联表N-N';
COMMENT ON COLUMN sys_role_permission.role_id IS '角色ID';
COMMENT ON COLUMN sys_role_permission.permission_id IS '权限ID';

DROP TABLE IF EXISTS sys_role_permission_data;
CREATE TABLE sys_role_permission_data(
                                         role_id VARCHAR(32) NOT NULL,
                                         permission_data_id VARCHAR(32) NOT NULL
);

COMMENT ON TABLE sys_role_permission_data IS '角色和数据权限关联表N-N';
COMMENT ON COLUMN sys_role_permission_data.role_id IS '角色ID';
COMMENT ON COLUMN sys_role_permission_data.permission_data_id IS '数据权限ID';

DROP TABLE IF EXISTS sys_user_post;
CREATE TABLE sys_user_post(
                              user_id VARCHAR(32) NOT NULL,
                              post_id VARCHAR(32) NOT NULL
);

COMMENT ON TABLE sys_user_post IS '用户和岗位关联表1-N';
COMMENT ON COLUMN sys_user_post.user_id IS '用户ID';
COMMENT ON COLUMN sys_user_post.post_id IS '岗位ID';

DROP TABLE IF EXISTS sys_dict_type;
CREATE TABLE sys_dict_type(
                              id VARCHAR(32) NOT NULL,
                              dict_name VARCHAR(50) NOT NULL,
                              dict_type VARCHAR(64) NOT NULL,
                              sort INTEGER NOT NULL,
                              status VARCHAR(1) NOT NULL DEFAULT  '0',
                              deleted VARCHAR(1) NOT NULL DEFAULT  '0',
                              version INTEGER NOT NULL DEFAULT  0,
                              created_by VARCHAR(50) NOT NULL DEFAULT  '',
                              created_time TIMESTAMP NOT NULL,
                              modified_by VARCHAR(50) DEFAULT  '',
                              modified_time TIMESTAMP,
                              remark VARCHAR(500),
                              PRIMARY KEY (id)
);

COMMENT ON TABLE sys_dict_type IS '字典类型表';
COMMENT ON COLUMN sys_dict_type.id IS '主键ID';
COMMENT ON COLUMN sys_dict_type.dict_name IS '字典名称';
COMMENT ON COLUMN sys_dict_type.dict_type IS '字典类型';
COMMENT ON COLUMN sys_dict_type.sort IS '记录排序';
COMMENT ON COLUMN sys_dict_type.status IS '记录状态;0:记录有效,1:记录无效';
COMMENT ON COLUMN sys_dict_type.deleted IS '删除标记;0:正常标记,1:删除标记';
COMMENT ON COLUMN sys_dict_type.version IS '乐观锁';
COMMENT ON COLUMN sys_dict_type.created_by IS '创建人';
COMMENT ON COLUMN sys_dict_type.created_time IS '创建时间';
COMMENT ON COLUMN sys_dict_type.modified_by IS '更新人';
COMMENT ON COLUMN sys_dict_type.modified_time IS '更新时间';
COMMENT ON COLUMN sys_dict_type.remark IS '备注';


-- CREATE UNIQUE INDEX sys_dict_type_pkey ON sys_dict_type(id);

DROP TABLE IF EXISTS sys_dict_data;
CREATE TABLE sys_dict_data(
                              id VARCHAR(32) NOT NULL,
                              dict_type VARCHAR(64) NOT NULL,
                              dict_label VARCHAR(64) NOT NULL DEFAULT  '',
                              dict_value VARCHAR(100) NOT NULL DEFAULT  '',
                              css_class VARCHAR(100),
                              list_class VARCHAR(255),
                              defaulted VARCHAR(1) NOT NULL DEFAULT  '0',
                              sort INTEGER NOT NULL DEFAULT  0,
                              status VARCHAR(1) NOT NULL DEFAULT  '0',
                              deleted VARCHAR(1) NOT NULL DEFAULT  '0',
                              version INTEGER NOT NULL DEFAULT  0,
                              created_by VARCHAR(50) NOT NULL DEFAULT  '',
                              created_time TIMESTAMP NOT NULL,
                              modified_by VARCHAR(50) DEFAULT  '',
                              modified_time TIMESTAMP,
                              remark VARCHAR(500),
                              PRIMARY KEY (id)
);

COMMENT ON TABLE sys_dict_data IS '字典数据表';
COMMENT ON COLUMN sys_dict_data.id IS '主键ID';
COMMENT ON COLUMN sys_dict_data.dict_type IS '字典类型';
COMMENT ON COLUMN sys_dict_data.dict_label IS '字典标签';
COMMENT ON COLUMN sys_dict_data.dict_value IS '字典键值';
COMMENT ON COLUMN sys_dict_data.css_class IS '样式属性';
COMMENT ON COLUMN sys_dict_data.list_class IS '表格回显样式';
COMMENT ON COLUMN sys_dict_data.defaulted IS '是否默认;是否默认: 0:否,1:是';
COMMENT ON COLUMN sys_dict_data.sort IS '字典排序';
COMMENT ON COLUMN sys_dict_data.status IS '启用状态;0:记录有效,1:记录无效';
COMMENT ON COLUMN sys_dict_data.deleted IS '删除标记;0:正常标记,1:删除标记';
COMMENT ON COLUMN sys_dict_data.version IS '乐观锁';
COMMENT ON COLUMN sys_dict_data.created_by IS '创建人';
COMMENT ON COLUMN sys_dict_data.created_time IS '创建时间';
COMMENT ON COLUMN sys_dict_data.modified_by IS '更新人';
COMMENT ON COLUMN sys_dict_data.modified_time IS '更新时间';
COMMENT ON COLUMN sys_dict_data.remark IS '备注';


-- CREATE UNIQUE INDEX sys_dict_data_pkey ON sys_dict_data(id);

DROP TABLE IF EXISTS sys_config;
CREATE TABLE sys_config(
                           id VARCHAR(32) NOT NULL,
                           config_name VARCHAR(100) NOT NULL,
                           config_key VARCHAR(100) NOT NULL,
                           config_value VARCHAR(500) NOT NULL DEFAULT  '0',
                           config_type VARCHAR(1) NOT NULL,
                           sort INTEGER NOT NULL,
                           status VARCHAR(1) NOT NULL DEFAULT  '0',
                           deleted VARCHAR(1) NOT NULL DEFAULT  '0',
                           version INTEGER NOT NULL DEFAULT  0,
                           created_by VARCHAR(50) NOT NULL DEFAULT  '',
                           created_time TIMESTAMP NOT NULL,
                           modified_by VARCHAR(50) DEFAULT  '',
                           modified_time TIMESTAMP,
                           remark VARCHAR(500),
                           PRIMARY KEY (id)
);

COMMENT ON TABLE sys_config IS '参数配置表';
COMMENT ON COLUMN sys_config.id IS '参数主键';
COMMENT ON COLUMN sys_config.config_name IS '参数名称';
COMMENT ON COLUMN sys_config.config_key IS '参数键名';
COMMENT ON COLUMN sys_config.config_value IS '参数键值;系统内置（0否 1是）';
COMMENT ON COLUMN sys_config.config_type IS '系统内置';
COMMENT ON COLUMN sys_config.sort IS '排序';
COMMENT ON COLUMN sys_config.status IS '启用状态;0:记录有效,1:记录无效';
COMMENT ON COLUMN sys_config.deleted IS '删除标记;0:正常标记,1:删除标记';
COMMENT ON COLUMN sys_config.version IS '乐观锁';
COMMENT ON COLUMN sys_config.created_by IS '创建人';
COMMENT ON COLUMN sys_config.created_time IS '创建时间';
COMMENT ON COLUMN sys_config.modified_by IS '更新人';
COMMENT ON COLUMN sys_config.modified_time IS '更新时间';
COMMENT ON COLUMN sys_config.remark IS '备注';


-- CREATE UNIQUE INDEX sys_config_pkey ON sys_config(id);



DROP TABLE IF EXISTS sys_permission_data;
CREATE TABLE sys_permission_data(
                                    id VARCHAR(32) NOT NULL,
                                    rule_name VARCHAR(255) NOT NULL,
                                    sort INTEGER NOT NULL,
                                    status VARCHAR(1) NOT NULL DEFAULT  '0',
                                    deleted VARCHAR(1) NOT NULL DEFAULT  '0',
                                    version INTEGER NOT NULL DEFAULT  0,
                                    created_by VARCHAR(50) NOT NULL DEFAULT  '',
                                    created_time TIMESTAMP NOT NULL,
                                    modified_by VARCHAR(50) DEFAULT  '',
                                    modified_time TIMESTAMP,
                                    remark VARCHAR(500),
                                    PRIMARY KEY (id)
);

COMMENT ON TABLE sys_permission_data IS '数据权限表';
COMMENT ON COLUMN sys_permission_data.id IS '主键ID';
COMMENT ON COLUMN sys_permission_data.rule_name IS '规则名称';
COMMENT ON COLUMN sys_permission_data.sort IS '排序';
COMMENT ON COLUMN sys_permission_data.status IS '启用状态;0:启用,1:关闭';
COMMENT ON COLUMN sys_permission_data.deleted IS '删除标记;0:正常标记,1:删除标记';
COMMENT ON COLUMN sys_permission_data.version IS '乐观锁';
COMMENT ON COLUMN sys_permission_data.created_by IS '创建人';
COMMENT ON COLUMN sys_permission_data.created_time IS '创建时间';
COMMENT ON COLUMN sys_permission_data.modified_by IS '更新人';
COMMENT ON COLUMN sys_permission_data.modified_time IS '更新时间';
COMMENT ON COLUMN sys_permission_data.remark IS '备注';


-- CREATE UNIQUE INDEX sys_data_perm_pkey ON sys_permission_data(id);

DROP TABLE IF EXISTS sys_permission;
CREATE TABLE sys_permission(
                               id VARCHAR(32) NOT NULL,
                               parent_id VARCHAR(32) NOT NULL,
                               permission_code VARCHAR(64) NOT NULL,
                               permission_name VARCHAR(50) NOT NULL,
                               sort INTEGER NOT NULL,
                               status VARCHAR(1) NOT NULL DEFAULT  '0',
                               deleted VARCHAR(1) NOT NULL DEFAULT  '0',
                               version INTEGER NOT NULL DEFAULT  0,
                               created_by VARCHAR(32) NOT NULL DEFAULT  '',
                               created_time TIMESTAMP NOT NULL,
                               modified_by VARCHAR(32) DEFAULT  '',
                               modified_time TIMESTAMP,
                               remark VARCHAR(500),
                               PRIMARY KEY (id)
);

COMMENT ON TABLE sys_permission IS '权限信息表';
COMMENT ON COLUMN sys_permission.id IS '主键ID';
COMMENT ON COLUMN sys_permission.parent_id IS '父级ID';
COMMENT ON COLUMN sys_permission.permission_code IS '权限编码;全局唯一';
COMMENT ON COLUMN sys_permission.permission_name IS '权限名称';
COMMENT ON COLUMN sys_permission.sort IS '排序';
COMMENT ON COLUMN sys_permission.status IS '启用状态;0:启用,1:关闭';
COMMENT ON COLUMN sys_permission.deleted IS '删除标记;0:正常标记,1:删除标记';
COMMENT ON COLUMN sys_permission.version IS '乐观锁';
COMMENT ON COLUMN sys_permission.created_by IS '创建人';
COMMENT ON COLUMN sys_permission.created_time IS '创建时间';
COMMENT ON COLUMN sys_permission.modified_by IS '更新人';
COMMENT ON COLUMN sys_permission.modified_time IS '更新时间';
COMMENT ON COLUMN sys_permission.remark IS '备注';


-- CREATE UNIQUE INDEX sys_perm_pkey ON sys_permission(id);

DROP TABLE IF EXISTS sys_id;
CREATE TABLE sys_id(
                       id VARCHAR(32) NOT NULL,
                       biz_type VARCHAR(64) NOT NULL,
                       biz_desc VARCHAR(500),
                       gen_type VARCHAR(32) NOT NULL,
                       max_id BIGINT NOT NULL DEFAULT  0,
                       step INTEGER NOT NULL,
                       sort INTEGER NOT NULL DEFAULT  0,
                       status VARCHAR(1) NOT NULL DEFAULT  '0',
                       deleted VARCHAR(1) NOT NULL DEFAULT  '0',
                       version INTEGER NOT NULL DEFAULT  0,
                       created_by VARCHAR(50) NOT NULL DEFAULT  '',
                       created_time TIMESTAMP NOT NULL,
                       modified_by VARCHAR(50) DEFAULT  '',
                       modified_time TIMESTAMP,
                       remark VARCHAR(500),
                       PRIMARY KEY (id)
);

COMMENT ON TABLE sys_id IS '系统ID表';
COMMENT ON COLUMN sys_id.id IS '主键ID';
COMMENT ON COLUMN sys_id.biz_type IS '业务类型';
COMMENT ON COLUMN sys_id.biz_desc IS '业务描述';
COMMENT ON COLUMN sys_id.gen_type IS '生成类型';
COMMENT ON COLUMN sys_id.max_id IS '当前最大id';
COMMENT ON COLUMN sys_id.step IS '步长';
COMMENT ON COLUMN sys_id.sort IS '排序';
COMMENT ON COLUMN sys_id.status IS '启用状态;0: 启用 , 1: 关闭';
COMMENT ON COLUMN sys_id.deleted IS '删除标记;0: 正常标记 , 1: 删除标记';
COMMENT ON COLUMN sys_id.version IS '乐观锁';
COMMENT ON COLUMN sys_id.created_by IS '创建人';
COMMENT ON COLUMN sys_id.created_time IS '创建时间';
COMMENT ON COLUMN sys_id.modified_by IS '更新人';
COMMENT ON COLUMN sys_id.modified_time IS '更新时间';
COMMENT ON COLUMN sys_id.remark IS '备注';

DROP TABLE IF EXISTS sys_group;
CREATE TABLE sys_group(
                          id VARCHAR(32) NOT NULL,
                          group_name VARCHAR(50) NOT NULL,
                          sort INTEGER NOT NULL DEFAULT  0,
                          status VARCHAR(1) NOT NULL DEFAULT  '0',
                          deleted VARCHAR(1) NOT NULL DEFAULT  '0',
                          version INTEGER NOT NULL DEFAULT  0,
                          created_by VARCHAR(50) NOT NULL DEFAULT  '',
                          created_time TIMESTAMP NOT NULL,
                          modified_by VARCHAR(50) DEFAULT  '',
                          modified_time TIMESTAMP,
                          remark VARCHAR(500),
                          PRIMARY KEY (id)
);

COMMENT ON TABLE sys_group IS '用户组信息';
COMMENT ON COLUMN sys_group.id IS '主键ID';
COMMENT ON COLUMN sys_group.group_name IS '用户组名称';
COMMENT ON COLUMN sys_group.sort IS '排序';
COMMENT ON COLUMN sys_group.status IS '启用状态;0: 启用 , 1: 关闭';
COMMENT ON COLUMN sys_group.deleted IS '删除标记;0: 正常标记 , 1: 删除标记';
COMMENT ON COLUMN sys_group.version IS '乐观锁';
COMMENT ON COLUMN sys_group.created_by IS '创建人';
COMMENT ON COLUMN sys_group.created_time IS '创建时间';
COMMENT ON COLUMN sys_group.modified_by IS '更新人';
COMMENT ON COLUMN sys_group.modified_time IS '更新时间';
COMMENT ON COLUMN sys_group.remark IS '备注';

DROP TABLE IF EXISTS sys_user_group;
CREATE TABLE sys_user_group(
                               user_id VARCHAR(32) NOT NULL,
                               group_id VARCHAR(32) NOT NULL
);

COMMENT ON TABLE sys_user_group IS '用户和用户组关联表N-N';
COMMENT ON COLUMN sys_user_group.user_id IS '用户ID';
COMMENT ON COLUMN sys_user_group.group_id IS '用户组ID';

DROP TABLE IF EXISTS sys_group_role;
CREATE TABLE sys_group_role(
                               group_id VARCHAR(32) NOT NULL,
                               role_id VARCHAR(32) NOT NULL
);

COMMENT ON TABLE sys_group_role IS '用户组和角色关联表1-N';
COMMENT ON COLUMN sys_group_role.group_id IS '用户组ID';
COMMENT ON COLUMN sys_group_role.role_id IS '角色ID';

