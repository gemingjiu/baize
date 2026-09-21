-- "baize-system".sys_config 定义

-- Drop table

-- DROP TABLE "baize-system".sys_config;

CREATE TABLE "baize-system".sys_config (
                                           id int8 NOT NULL, -- 参数主键
                                           tenant_id int8 NOT NULL, -- 租户ID
                                           config_name varchar(50) NULL, -- 参数名称
                                           config_key varchar(64) NULL, -- 参数键名
                                           config_value varchar(200) NULL, -- 参数键值
                                           config_type varchar(1) DEFAULT '0'::character varying NOT NULL, -- 系统内置;系统内置（0否 1是）
                                           sort int4 NULL, -- 排序
                                           status varchar(1) DEFAULT '0'::character varying NOT NULL, -- 启用状态;0:记录有效,1:记录无效
                                           deleted varchar(1) DEFAULT '0'::character varying NOT NULL, -- 删除标记;0:正常标记,1:删除标记
                                           "version" int4 DEFAULT 0 NOT NULL, -- 乐观锁
                                           created_by int8 NOT NULL, -- 创建人
                                           created_time timestamp NOT NULL, -- 创建时间
                                           modified_by int8 NULL, -- 更新人
                                           modified_time timestamp NULL, -- 更新时间
                                           remark varchar(255) NULL -- 备注
);
COMMENT ON TABLE "baize-system".sys_config IS '参数配置表';

-- Column comments

COMMENT ON COLUMN "baize-system".sys_config.id IS '参数主键';
COMMENT ON COLUMN "baize-system".sys_config.tenant_id IS '租户ID';
COMMENT ON COLUMN "baize-system".sys_config.config_name IS '参数名称';
COMMENT ON COLUMN "baize-system".sys_config.config_key IS '参数键名';
COMMENT ON COLUMN "baize-system".sys_config.config_value IS '参数键值';
COMMENT ON COLUMN "baize-system".sys_config.config_type IS '系统内置;系统内置（0否 1是）';
COMMENT ON COLUMN "baize-system".sys_config.sort IS '排序';
COMMENT ON COLUMN "baize-system".sys_config.status IS '启用状态;0:记录有效,1:记录无效';
COMMENT ON COLUMN "baize-system".sys_config.deleted IS '删除标记;0:正常标记,1:删除标记';
COMMENT ON COLUMN "baize-system".sys_config."version" IS '乐观锁';
COMMENT ON COLUMN "baize-system".sys_config.created_by IS '创建人';
COMMENT ON COLUMN "baize-system".sys_config.created_time IS '创建时间';
COMMENT ON COLUMN "baize-system".sys_config.modified_by IS '更新人';
COMMENT ON COLUMN "baize-system".sys_config.modified_time IS '更新时间';
COMMENT ON COLUMN "baize-system".sys_config.remark IS '备注';

-- Permissions

ALTER TABLE "baize-system".sys_config OWNER TO postgres;
GRANT ALL ON TABLE "baize-system".sys_config TO postgres;


-- "baize-system".sys_data_perm 定义

-- Drop table

-- DROP TABLE "baize-system".sys_data_perm;

CREATE TABLE "baize-system".sys_data_perm (
                                              id int8 NOT NULL, -- 主键ID
                                              tenant_id int8 NOT NULL, -- 租户ID
                                              rule_name varchar(50) NULL, -- 规则名称
                                              sort int4 NULL, -- 排序
                                              status varchar(1) DEFAULT '0'::character varying NOT NULL, -- 启用状态;0:启用,1:关闭
                                              deleted varchar(1) DEFAULT '0'::character varying NOT NULL, -- 删除标记;0:正常标记,1:删除标记
                                              "version" int4 NOT NULL, -- 乐观锁
                                              created_by int8 NOT NULL, -- 创建人
                                              created_time timestamp NOT NULL, -- 创建时间
                                              modified_by int8 NULL, -- 更新人
                                              modified_time timestamp NULL, -- 更新时间
                                              remark varchar(255) NULL -- 备注
);
COMMENT ON TABLE "baize-system".sys_data_perm IS '数据权限表';

-- Column comments

COMMENT ON COLUMN "baize-system".sys_data_perm.id IS '主键ID';
COMMENT ON COLUMN "baize-system".sys_data_perm.tenant_id IS '租户ID';
COMMENT ON COLUMN "baize-system".sys_data_perm.rule_name IS '规则名称';
COMMENT ON COLUMN "baize-system".sys_data_perm.sort IS '排序';
COMMENT ON COLUMN "baize-system".sys_data_perm.status IS '启用状态;0:启用,1:关闭';
COMMENT ON COLUMN "baize-system".sys_data_perm.deleted IS '删除标记;0:正常标记,1:删除标记';
COMMENT ON COLUMN "baize-system".sys_data_perm."version" IS '乐观锁';
COMMENT ON COLUMN "baize-system".sys_data_perm.created_by IS '创建人';
COMMENT ON COLUMN "baize-system".sys_data_perm.created_time IS '创建时间';
COMMENT ON COLUMN "baize-system".sys_data_perm.modified_by IS '更新人';
COMMENT ON COLUMN "baize-system".sys_data_perm.modified_time IS '更新时间';
COMMENT ON COLUMN "baize-system".sys_data_perm.remark IS '备注';

-- Permissions

ALTER TABLE "baize-system".sys_data_perm OWNER TO postgres;
GRANT ALL ON TABLE "baize-system".sys_data_perm TO postgres;


-- "baize-system".sys_dept 定义

-- Drop table

-- DROP TABLE "baize-system".sys_dept;

CREATE TABLE "baize-system".sys_dept (
                                         id int8 NOT NULL, -- 主键ID
                                         tenant_id int8 NOT NULL, -- 租户ID
                                         parent_id int8 NOT NULL, -- 父部门id
                                         dept_name varchar(50) NOT NULL, -- 部门名称
                                         leader varchar(50) NOT NULL, -- 负责人
                                         phone varchar(32) NULL, -- 电话
                                         email varchar(254) NULL, -- 邮箱
                                         sort int4 NULL, -- 显示顺序
                                         status varchar(1) DEFAULT '0'::character varying NOT NULL, -- 记录状态;0:记录有效,1:记录无效
                                         deleted varchar(1) DEFAULT '0'::character varying NOT NULL, -- 删除标记;0:正常标记,1:删除标记
                                         "version" int4 NOT NULL, -- 乐观锁
                                         created_by int8 NOT NULL, -- 创建人
                                         created_time timestamp NOT NULL, -- 创建时间
                                         modified_by int8 NULL, -- 更新人
                                         modified_time timestamp NULL, -- 更新时间
                                         remark varchar(255) NULL -- 备注
);
COMMENT ON TABLE "baize-system".sys_dept IS '部门信息表';

-- Column comments

COMMENT ON COLUMN "baize-system".sys_dept.id IS '主键ID';
COMMENT ON COLUMN "baize-system".sys_dept.tenant_id IS '租户ID';
COMMENT ON COLUMN "baize-system".sys_dept.parent_id IS '父部门id';
COMMENT ON COLUMN "baize-system".sys_dept.dept_name IS '部门名称';
COMMENT ON COLUMN "baize-system".sys_dept.leader IS '负责人';
COMMENT ON COLUMN "baize-system".sys_dept.phone IS '电话';
COMMENT ON COLUMN "baize-system".sys_dept.email IS '邮箱';
COMMENT ON COLUMN "baize-system".sys_dept.sort IS '显示顺序';
COMMENT ON COLUMN "baize-system".sys_dept.status IS '记录状态;0:记录有效,1:记录无效';
COMMENT ON COLUMN "baize-system".sys_dept.deleted IS '删除标记;0:正常标记,1:删除标记';
COMMENT ON COLUMN "baize-system".sys_dept."version" IS '乐观锁';
COMMENT ON COLUMN "baize-system".sys_dept.created_by IS '创建人';
COMMENT ON COLUMN "baize-system".sys_dept.created_time IS '创建时间';
COMMENT ON COLUMN "baize-system".sys_dept.modified_by IS '更新人';
COMMENT ON COLUMN "baize-system".sys_dept.modified_time IS '更新时间';
COMMENT ON COLUMN "baize-system".sys_dept.remark IS '备注';

-- Permissions

ALTER TABLE "baize-system".sys_dept OWNER TO postgres;
GRANT ALL ON TABLE "baize-system".sys_dept TO postgres;


-- "baize-system".sys_dict_data 定义

-- Drop table

-- DROP TABLE "baize-system".sys_dict_data;

CREATE TABLE "baize-system".sys_dict_data (
                                              id int8 NOT NULL, -- 主键ID
                                              tenant_id int8 NOT NULL, -- 租户ID
                                              dict_type varchar(32) NULL, -- 字典类型
                                              dict_label varchar(50) DEFAULT ''::character varying NOT NULL, -- 字典标签
                                              dict_value varchar(100) DEFAULT ''::character varying NULL, -- 字典键值
                                              css_class varchar(100) NULL, -- 样式属性
                                              list_class varchar(200) NULL, -- 表格回显样式
                                              defaulted varchar(1) DEFAULT '0'::character varying NOT NULL, -- 是否默认;是否默认: 0:否,1:是
                                              sort int4 NULL, -- 字典排序
                                              status varchar(1) DEFAULT '0'::character varying NOT NULL, -- 启用状态;0:记录有效,1:记录无效
                                              deleted varchar(1) DEFAULT '0'::character varying NOT NULL, -- 删除标记;0:正常标记,1:删除标记
                                              "version" int4 NOT NULL, -- 乐观锁
                                              created_by int8 NOT NULL, -- 创建人
                                              created_time timestamp NOT NULL, -- 创建时间
                                              modified_by int8 NULL, -- 更新人
                                              modified_time timestamp NULL, -- 更新时间
                                              remark varchar(255) NULL -- 备注
);
COMMENT ON TABLE "baize-system".sys_dict_data IS '字典数据表';

-- Column comments

COMMENT ON COLUMN "baize-system".sys_dict_data.id IS '主键ID';
COMMENT ON COLUMN "baize-system".sys_dict_data.tenant_id IS '租户ID';
COMMENT ON COLUMN "baize-system".sys_dict_data.dict_type IS '字典类型';
COMMENT ON COLUMN "baize-system".sys_dict_data.dict_label IS '字典标签';
COMMENT ON COLUMN "baize-system".sys_dict_data.dict_value IS '字典键值';
COMMENT ON COLUMN "baize-system".sys_dict_data.css_class IS '样式属性';
COMMENT ON COLUMN "baize-system".sys_dict_data.list_class IS '表格回显样式';
COMMENT ON COLUMN "baize-system".sys_dict_data.defaulted IS '是否默认;是否默认: 0:否,1:是';
COMMENT ON COLUMN "baize-system".sys_dict_data.sort IS '字典排序';
COMMENT ON COLUMN "baize-system".sys_dict_data.status IS '启用状态;0:记录有效,1:记录无效';
COMMENT ON COLUMN "baize-system".sys_dict_data.deleted IS '删除标记;0:正常标记,1:删除标记';
COMMENT ON COLUMN "baize-system".sys_dict_data."version" IS '乐观锁';
COMMENT ON COLUMN "baize-system".sys_dict_data.created_by IS '创建人';
COMMENT ON COLUMN "baize-system".sys_dict_data.created_time IS '创建时间';
COMMENT ON COLUMN "baize-system".sys_dict_data.modified_by IS '更新人';
COMMENT ON COLUMN "baize-system".sys_dict_data.modified_time IS '更新时间';
COMMENT ON COLUMN "baize-system".sys_dict_data.remark IS '备注';

-- Permissions

ALTER TABLE "baize-system".sys_dict_data OWNER TO postgres;
GRANT ALL ON TABLE "baize-system".sys_dict_data TO postgres;


-- "baize-system".sys_dict_type 定义

-- Drop table

-- DROP TABLE "baize-system".sys_dict_type;

CREATE TABLE "baize-system".sys_dict_type (
                                              id int8 NOT NULL, -- 主键ID
                                              tenant_id int8 NOT NULL, -- 租户ID
                                              dict_name varchar(50) NULL, -- 字典名称
                                              dict_type varchar(32) NULL, -- 字典类型
                                              sort int4 NULL, -- 记录排序
                                              status varchar(1) DEFAULT '0'::character varying NOT NULL, -- 记录状态;0:记录有效,1:记录无效
                                              deleted varchar(1) DEFAULT '0'::character varying NOT NULL, -- 删除标记;0:正常标记,1:删除标记
                                              "version" int4 NOT NULL, -- 乐观锁
                                              created_by int8 NOT NULL, -- 创建人
                                              created_time timestamp NOT NULL, -- 创建时间
                                              modified_by int8 NULL, -- 更新人
                                              modified_time timestamp NULL, -- 更新时间
                                              remark varchar(255) NULL -- 备注
);
COMMENT ON TABLE "baize-system".sys_dict_type IS '字典类型表';

-- Column comments

COMMENT ON COLUMN "baize-system".sys_dict_type.id IS '主键ID';
COMMENT ON COLUMN "baize-system".sys_dict_type.tenant_id IS '租户ID';
COMMENT ON COLUMN "baize-system".sys_dict_type.dict_name IS '字典名称';
COMMENT ON COLUMN "baize-system".sys_dict_type.dict_type IS '字典类型';
COMMENT ON COLUMN "baize-system".sys_dict_type.sort IS '记录排序';
COMMENT ON COLUMN "baize-system".sys_dict_type.status IS '记录状态;0:记录有效,1:记录无效';
COMMENT ON COLUMN "baize-system".sys_dict_type.deleted IS '删除标记;0:正常标记,1:删除标记';
COMMENT ON COLUMN "baize-system".sys_dict_type."version" IS '乐观锁';
COMMENT ON COLUMN "baize-system".sys_dict_type.created_by IS '创建人';
COMMENT ON COLUMN "baize-system".sys_dict_type.created_time IS '创建时间';
COMMENT ON COLUMN "baize-system".sys_dict_type.modified_by IS '更新人';
COMMENT ON COLUMN "baize-system".sys_dict_type.modified_time IS '更新时间';
COMMENT ON COLUMN "baize-system".sys_dict_type.remark IS '备注';

-- Permissions

ALTER TABLE "baize-system".sys_dict_type OWNER TO postgres;
GRANT ALL ON TABLE "baize-system".sys_dict_type TO postgres;


-- "baize-system".sys_menu 定义

-- Drop table

-- DROP TABLE "baize-system".sys_menu;

CREATE TABLE "baize-system".sys_menu (
                                         id int8 NOT NULL, -- 主键ID
                                         tenant_id int8 NOT NULL, -- 租户ID
                                         parent_id int8 NOT NULL, -- 父菜单ID
                                         menu_name varchar(50) NOT NULL, -- 菜单名称
                                         "path" varchar(255) DEFAULT ''::character varying NULL, -- 路由地址
                                         component varchar(255) NULL, -- 组件路径
                                         parameters varchar(255) NULL, -- 路由参数
                                         "external" varchar(1) DEFAULT '0'::character varying NOT NULL, -- 是否外链;0:内链,1:外链
                                         cacheable varchar(1) DEFAULT '0'::character varying NOT NULL, -- 是否缓存;0:不缓存,1:缓存
                                         menu_type varchar(1) DEFAULT ''::character varying NOT NULL, -- 菜单类型;0:目录,1:菜单，2:按钮
                                         visible varchar(1) DEFAULT '0'::character varying NOT NULL, -- 菜单状态;0:显示1:隐藏
                                         icon varchar(90) DEFAULT '#'::character varying NULL, -- 菜单图标
                                         perms varchar(255) NULL, -- 权限字段
                                         sort int4 NULL, -- 显示顺序
                                         status varchar(1) DEFAULT '0'::character varying NOT NULL, -- 记录状态;0:记录有效,1:记录无效
                                         deleted varchar(1) DEFAULT '0'::character varying NOT NULL, -- 删除标记;0:正常标记,1:删除标记
                                         "version" int4 NOT NULL, -- 乐观锁
                                         created_by int8 NOT NULL, -- 创建人
                                         created_time timestamp NOT NULL, -- 创建时间
                                         modified_by int8 NULL, -- 更新人
                                         modified_time timestamp NULL, -- 更新时间
                                         remark varchar(255) NULL -- 备注
);
COMMENT ON TABLE "baize-system".sys_menu IS '菜单信息表';

-- Column comments

COMMENT ON COLUMN "baize-system".sys_menu.id IS '主键ID';
COMMENT ON COLUMN "baize-system".sys_menu.tenant_id IS '租户ID';
COMMENT ON COLUMN "baize-system".sys_menu.parent_id IS '父菜单ID';
COMMENT ON COLUMN "baize-system".sys_menu.menu_name IS '菜单名称';
COMMENT ON COLUMN "baize-system".sys_menu."path" IS '路由地址';
COMMENT ON COLUMN "baize-system".sys_menu.component IS '组件路径';
COMMENT ON COLUMN "baize-system".sys_menu.parameters IS '路由参数';
COMMENT ON COLUMN "baize-system".sys_menu."external" IS '是否外链;0:内链,1:外链';
COMMENT ON COLUMN "baize-system".sys_menu.cacheable IS '是否缓存;0:不缓存,1:缓存';
COMMENT ON COLUMN "baize-system".sys_menu.menu_type IS '菜单类型;0:目录,1:菜单，2:按钮';
COMMENT ON COLUMN "baize-system".sys_menu.visible IS '菜单状态;0:显示1:隐藏';
COMMENT ON COLUMN "baize-system".sys_menu.icon IS '菜单图标';
COMMENT ON COLUMN "baize-system".sys_menu.perms IS '权限字段';
COMMENT ON COLUMN "baize-system".sys_menu.sort IS '显示顺序';
COMMENT ON COLUMN "baize-system".sys_menu.status IS '记录状态;0:记录有效,1:记录无效';
COMMENT ON COLUMN "baize-system".sys_menu.deleted IS '删除标记;0:正常标记,1:删除标记';
COMMENT ON COLUMN "baize-system".sys_menu."version" IS '乐观锁';
COMMENT ON COLUMN "baize-system".sys_menu.created_by IS '创建人';
COMMENT ON COLUMN "baize-system".sys_menu.created_time IS '创建时间';
COMMENT ON COLUMN "baize-system".sys_menu.modified_by IS '更新人';
COMMENT ON COLUMN "baize-system".sys_menu.modified_time IS '更新时间';
COMMENT ON COLUMN "baize-system".sys_menu.remark IS '备注';

-- Permissions

ALTER TABLE "baize-system".sys_menu OWNER TO postgres;
GRANT ALL ON TABLE "baize-system".sys_menu TO postgres;


-- "baize-system".sys_perm 定义

-- Drop table

-- DROP TABLE "baize-system".sys_perm;

CREATE TABLE "baize-system".sys_perm (
                                         id int8 NOT NULL, -- 主键ID
                                         tenant_id int8 NOT NULL, -- 租户ID
                                         parent_id int8 NOT NULL, -- 父级ID
                                         perm_code varchar(70) NOT NULL, -- 权限编码
                                         perm_name varchar(90) NOT NULL, -- 权限名称
                                         sort int4 NULL, -- 排序
                                         status varchar(1) DEFAULT '0'::character varying NOT NULL, -- 启用状态;0:启用,1:关闭
                                         deleted varchar(1) DEFAULT '0'::character varying NOT NULL, -- 删除标记;0:正常标记,1:删除标记
                                         "version" int4 DEFAULT 0 NOT NULL, -- 乐观锁
                                         created_by int8 NOT NULL, -- 创建人
                                         created_time timestamp NOT NULL, -- 创建时间
                                         modified_by int8 NULL, -- 更新人
                                         modified_time timestamp NULL, -- 更新时间
                                         remark varchar(255) NULL -- 备注
);
COMMENT ON TABLE "baize-system".sys_perm IS '权限信息表';

-- Column comments

COMMENT ON COLUMN "baize-system".sys_perm.id IS '主键ID';
COMMENT ON COLUMN "baize-system".sys_perm.tenant_id IS '租户ID';
COMMENT ON COLUMN "baize-system".sys_perm.parent_id IS '父级ID';
COMMENT ON COLUMN "baize-system".sys_perm.perm_code IS '权限编码';
COMMENT ON COLUMN "baize-system".sys_perm.perm_name IS '权限名称';
COMMENT ON COLUMN "baize-system".sys_perm.sort IS '排序';
COMMENT ON COLUMN "baize-system".sys_perm.status IS '启用状态;0:启用,1:关闭';
COMMENT ON COLUMN "baize-system".sys_perm.deleted IS '删除标记;0:正常标记,1:删除标记';
COMMENT ON COLUMN "baize-system".sys_perm."version" IS '乐观锁';
COMMENT ON COLUMN "baize-system".sys_perm.created_by IS '创建人';
COMMENT ON COLUMN "baize-system".sys_perm.created_time IS '创建时间';
COMMENT ON COLUMN "baize-system".sys_perm.modified_by IS '更新人';
COMMENT ON COLUMN "baize-system".sys_perm.modified_time IS '更新时间';
COMMENT ON COLUMN "baize-system".sys_perm.remark IS '备注';

-- Permissions

ALTER TABLE "baize-system".sys_perm OWNER TO postgres;
GRANT ALL ON TABLE "baize-system".sys_perm TO postgres;


-- "baize-system".sys_post 定义

-- Drop table

-- DROP TABLE "baize-system".sys_post;

CREATE TABLE "baize-system".sys_post (
                                         id int8 NOT NULL, -- 岗位ID
                                         tenant_id int8 NOT NULL, -- 租户ID
                                         post_code varchar(70) NOT NULL, -- 岗位编码
                                         post_name varchar(90) NOT NULL, -- 岗位名称
                                         sort int4 NULL, -- 岗位顺序
                                         status varchar(1) DEFAULT '0'::character varying NOT NULL, -- 记录状态;0:记录有效,1:记录无效
                                         deleted varchar(1) DEFAULT '0'::character varying NOT NULL, -- 删除标记;0:正常标记,1:删除标记
                                         "version" int4 DEFAULT 0 NOT NULL, -- 乐观锁
                                         created_by int8 NOT NULL, -- 创建人
                                         created_time timestamp NOT NULL, -- 创建时间
                                         modified_by int8 NULL, -- 更新人
                                         modified_time timestamp NULL, -- 更新时间
                                         remark varchar(255) NULL -- 备注
);
COMMENT ON TABLE "baize-system".sys_post IS '岗位信息表';

-- Column comments

COMMENT ON COLUMN "baize-system".sys_post.id IS '岗位ID';
COMMENT ON COLUMN "baize-system".sys_post.tenant_id IS '租户ID';
COMMENT ON COLUMN "baize-system".sys_post.post_code IS '岗位编码';
COMMENT ON COLUMN "baize-system".sys_post.post_name IS '岗位名称';
COMMENT ON COLUMN "baize-system".sys_post.sort IS '岗位顺序';
COMMENT ON COLUMN "baize-system".sys_post.status IS '记录状态;0:记录有效,1:记录无效';
COMMENT ON COLUMN "baize-system".sys_post.deleted IS '删除标记;0:正常标记,1:删除标记';
COMMENT ON COLUMN "baize-system".sys_post."version" IS '乐观锁';
COMMENT ON COLUMN "baize-system".sys_post.created_by IS '创建人';
COMMENT ON COLUMN "baize-system".sys_post.created_time IS '创建时间';
COMMENT ON COLUMN "baize-system".sys_post.modified_by IS '更新人';
COMMENT ON COLUMN "baize-system".sys_post.modified_time IS '更新时间';
COMMENT ON COLUMN "baize-system".sys_post.remark IS '备注';

-- Permissions

ALTER TABLE "baize-system".sys_post OWNER TO postgres;
GRANT ALL ON TABLE "baize-system".sys_post TO postgres;


-- "baize-system".sys_role 定义

-- Drop table

-- DROP TABLE "baize-system".sys_role;

CREATE TABLE "baize-system".sys_role (
                                         id int8 NOT NULL, -- 主键ID
                                         tenant_id int8 NOT NULL, -- 租户ID
                                         role_name varchar(50) NOT NULL, -- 角色名称
                                         role_code varchar(64) NULL, -- 角色权限字符串
                                         data_scope varchar(1) DEFAULT '1'::character varying NOT NULL, -- 数据范围;1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限
                                         menu_check_strictly varchar(1) DEFAULT '1'::character varying NOT NULL, -- 菜单树选择项是否关联显示
                                         dept_check_strictly varchar(1) DEFAULT '1'::character varying NOT NULL, -- 部门树选择项是否关联显示
                                         sort int4 NULL, -- 显示顺序
                                         status varchar(1) DEFAULT '0'::character varying NOT NULL, -- 记录状态;0:记录有效,1:记录无效
                                         deleted varchar(1) DEFAULT '0'::character varying NOT NULL, -- 删除标记;0:正常标记,1:删除标记
                                         "version" int4 NOT NULL, -- 乐观锁
                                         created_by int8 NOT NULL, -- 创建人
                                         created_time timestamp NOT NULL, -- 创建时间
                                         modified_by int8 NULL, -- 更新人
                                         modified_time timestamp NULL, -- 更新时间
                                         remark varchar(255) NULL -- 备注
);
COMMENT ON TABLE "baize-system".sys_role IS '角色信息表';

-- Column comments

COMMENT ON COLUMN "baize-system".sys_role.id IS '主键ID';
COMMENT ON COLUMN "baize-system".sys_role.tenant_id IS '租户ID';
COMMENT ON COLUMN "baize-system".sys_role.role_name IS '角色名称';
COMMENT ON COLUMN "baize-system".sys_role.role_code IS '角色权限字符串';
COMMENT ON COLUMN "baize-system".sys_role.data_scope IS '数据范围;1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限';
COMMENT ON COLUMN "baize-system".sys_role.menu_check_strictly IS '菜单树选择项是否关联显示';
COMMENT ON COLUMN "baize-system".sys_role.dept_check_strictly IS '部门树选择项是否关联显示';
COMMENT ON COLUMN "baize-system".sys_role.sort IS '显示顺序';
COMMENT ON COLUMN "baize-system".sys_role.status IS '记录状态;0:记录有效,1:记录无效';
COMMENT ON COLUMN "baize-system".sys_role.deleted IS '删除标记;0:正常标记,1:删除标记';
COMMENT ON COLUMN "baize-system".sys_role."version" IS '乐观锁';
COMMENT ON COLUMN "baize-system".sys_role.created_by IS '创建人';
COMMENT ON COLUMN "baize-system".sys_role.created_time IS '创建时间';
COMMENT ON COLUMN "baize-system".sys_role.modified_by IS '更新人';
COMMENT ON COLUMN "baize-system".sys_role.modified_time IS '更新时间';
COMMENT ON COLUMN "baize-system".sys_role.remark IS '备注';

-- Permissions

ALTER TABLE "baize-system".sys_role OWNER TO postgres;
GRANT ALL ON TABLE "baize-system".sys_role TO postgres;


-- "baize-system".sys_role_dept 定义

-- Drop table

-- DROP TABLE "baize-system".sys_role_dept;

CREATE TABLE "baize-system".sys_role_dept (
                                              role_id int8 NOT NULL, -- 角色ID
                                              dept_id int8 NOT NULL -- 部门ID
);
COMMENT ON TABLE "baize-system".sys_role_dept IS '角色和部门关联表1-N';

-- Column comments

COMMENT ON COLUMN "baize-system".sys_role_dept.role_id IS '角色ID';
COMMENT ON COLUMN "baize-system".sys_role_dept.dept_id IS '部门ID';

-- Permissions

ALTER TABLE "baize-system".sys_role_dept OWNER TO postgres;
GRANT ALL ON TABLE "baize-system".sys_role_dept TO postgres;


-- "baize-system".sys_role_menu 定义

-- Drop table

-- DROP TABLE "baize-system".sys_role_menu;

CREATE TABLE "baize-system".sys_role_menu (
                                              role_id int8 NOT NULL, -- 角色ID
                                              menu_id int8 NOT NULL -- 菜单ID
);
COMMENT ON TABLE "baize-system".sys_role_menu IS '角色和菜单关联表1-N';

-- Column comments

COMMENT ON COLUMN "baize-system".sys_role_menu.role_id IS '角色ID';
COMMENT ON COLUMN "baize-system".sys_role_menu.menu_id IS '菜单ID';

-- Permissions

ALTER TABLE "baize-system".sys_role_menu OWNER TO postgres;
GRANT ALL ON TABLE "baize-system".sys_role_menu TO postgres;


-- "baize-system".sys_role_perm 定义

-- Drop table

-- DROP TABLE "baize-system".sys_role_perm;

CREATE TABLE "baize-system".sys_role_perm (
                                              role_id int8 NOT NULL, -- 角色ID
                                              perm_id int8 NOT NULL -- 权限ID
);
COMMENT ON TABLE "baize-system".sys_role_perm IS '角色和权限关联表1-N';

-- Column comments

COMMENT ON COLUMN "baize-system".sys_role_perm.role_id IS '角色ID';
COMMENT ON COLUMN "baize-system".sys_role_perm.perm_id IS '权限ID';

-- Permissions

ALTER TABLE "baize-system".sys_role_perm OWNER TO postgres;
GRANT ALL ON TABLE "baize-system".sys_role_perm TO postgres;


-- "baize-system".sys_tenant 定义

-- Drop table

-- DROP TABLE "baize-system".sys_tenant;

CREATE TABLE "baize-system".sys_tenant (
                                           id int8 NOT NULL, -- 主键ID
                                           tenant_code varchar(64) NULL, -- 租户编码
                                           tenant_name varchar(90) NULL, -- 租户名称
                                           expire_time timestamp NULL, -- 过期时间
                                           contact_person varchar(32) NULL, -- 联系人
                                           contact_phone varchar(32) NULL, -- 联系人号码
                                           contact_email varchar(90) NULL, -- 联系人邮箱
                                           "domain" varchar(90) NULL, -- 唯一域名
                                           max_user int4 DEFAULT 10 NULL, -- 最大允许用户数
                                           sort int4 NULL, -- 显示顺序
                                           status varchar(1) DEFAULT '0'::character varying NOT NULL, -- 记录状态;0:记录有效,1:记录无效
                                           deleted varchar(1) DEFAULT '0'::character varying NOT NULL, -- 删除标记;0:正常标记,1:删除标记
                                           "version" int4 DEFAULT 0 NOT NULL, -- 乐观锁
                                           created_by int8 NOT NULL, -- 创建人
                                           created_time timestamp NOT NULL, -- 创建时间
                                           modified_by int8 NULL, -- 更新人
                                           modified_time timestamp NULL, -- 更新时间
                                           remark varchar(255) NULL -- 备注
);
COMMENT ON TABLE "baize-system".sys_tenant IS '租户信息表';

-- Column comments

COMMENT ON COLUMN "baize-system".sys_tenant.id IS '主键ID';
COMMENT ON COLUMN "baize-system".sys_tenant.tenant_code IS '租户编码';
COMMENT ON COLUMN "baize-system".sys_tenant.tenant_name IS '租户名称';
COMMENT ON COLUMN "baize-system".sys_tenant.expire_time IS '过期时间';
COMMENT ON COLUMN "baize-system".sys_tenant.contact_person IS '联系人';
COMMENT ON COLUMN "baize-system".sys_tenant.contact_phone IS '联系人号码';
COMMENT ON COLUMN "baize-system".sys_tenant.contact_email IS '联系人邮箱';
COMMENT ON COLUMN "baize-system".sys_tenant."domain" IS '唯一域名';
COMMENT ON COLUMN "baize-system".sys_tenant.max_user IS '最大允许用户数';
COMMENT ON COLUMN "baize-system".sys_tenant.sort IS '显示顺序';
COMMENT ON COLUMN "baize-system".sys_tenant.status IS '记录状态;0:记录有效,1:记录无效';
COMMENT ON COLUMN "baize-system".sys_tenant.deleted IS '删除标记;0:正常标记,1:删除标记';
COMMENT ON COLUMN "baize-system".sys_tenant."version" IS '乐观锁';
COMMENT ON COLUMN "baize-system".sys_tenant.created_by IS '创建人';
COMMENT ON COLUMN "baize-system".sys_tenant.created_time IS '创建时间';
COMMENT ON COLUMN "baize-system".sys_tenant.modified_by IS '更新人';
COMMENT ON COLUMN "baize-system".sys_tenant.modified_time IS '更新时间';
COMMENT ON COLUMN "baize-system".sys_tenant.remark IS '备注';

-- Permissions

ALTER TABLE "baize-system".sys_tenant OWNER TO postgres;
GRANT ALL ON TABLE "baize-system".sys_tenant TO postgres;


-- "baize-system".sys_user 定义

-- Drop table

-- DROP TABLE "baize-system".sys_user;

CREATE TABLE "baize-system".sys_user (
                                         id int8 NOT NULL, -- 主键ID
                                         tenant_id int8 NOT NULL, -- 租户ID
                                         dept_id int8 NOT NULL, -- 部门ID
                                         user_name varchar(90) NULL, -- 用户账号
                                         nick_name varchar(90) NULL, -- 用户昵称
                                         user_type varchar(32) NULL, -- 用户类型
                                         email varchar(90) DEFAULT ''::character varying NULL, -- 用户邮箱
                                         phone varchar(90) DEFAULT ''::character varying NULL, -- 手机号码
                                         gender varchar(1) DEFAULT '0'::character varying NOT NULL, -- 用户性别
                                         avatar varchar(90) DEFAULT ''::character varying NULL, -- 用户头像
                                         "password" varchar(90) DEFAULT ''::character varying NULL, -- 密码
                                         login_ip varchar(90) DEFAULT ''::character varying NULL, -- 最后登录IP
                                         login_date timestamp NULL, -- 最后登录时间
                                         sort int4 NULL, -- 显示顺序
                                         status varchar(1) DEFAULT '0'::character varying NOT NULL, -- 记录状态;0:记录有效,1:记录无效
                                         deleted varchar(1) DEFAULT '0'::character varying NOT NULL, -- 删除标记;0:正常标记,1:删除标记
                                         "version" int4 NOT NULL, -- 乐观锁
                                         created_by int8 NOT NULL, -- 创建人
                                         created_time timestamp NOT NULL, -- 创建时间
                                         modified_by int8 NULL, -- 更新人
                                         modified_time timestamp NULL, -- 更新时间
                                         remark varchar(255) NULL -- 备注
);
COMMENT ON TABLE "baize-system".sys_user IS '用户信息表';

-- Column comments

COMMENT ON COLUMN "baize-system".sys_user.id IS '主键ID';
COMMENT ON COLUMN "baize-system".sys_user.tenant_id IS '租户ID';
COMMENT ON COLUMN "baize-system".sys_user.dept_id IS '部门ID';
COMMENT ON COLUMN "baize-system".sys_user.user_name IS '用户账号';
COMMENT ON COLUMN "baize-system".sys_user.nick_name IS '用户昵称';
COMMENT ON COLUMN "baize-system".sys_user.user_type IS '用户类型';
COMMENT ON COLUMN "baize-system".sys_user.email IS '用户邮箱';
COMMENT ON COLUMN "baize-system".sys_user.phone IS '手机号码';
COMMENT ON COLUMN "baize-system".sys_user.gender IS '用户性别';
COMMENT ON COLUMN "baize-system".sys_user.avatar IS '用户头像';
COMMENT ON COLUMN "baize-system".sys_user."password" IS '密码';
COMMENT ON COLUMN "baize-system".sys_user.login_ip IS '最后登录IP';
COMMENT ON COLUMN "baize-system".sys_user.login_date IS '最后登录时间';
COMMENT ON COLUMN "baize-system".sys_user.sort IS '显示顺序';
COMMENT ON COLUMN "baize-system".sys_user.status IS '记录状态;0:记录有效,1:记录无效';
COMMENT ON COLUMN "baize-system".sys_user.deleted IS '删除标记;0:正常标记,1:删除标记';
COMMENT ON COLUMN "baize-system".sys_user."version" IS '乐观锁';
COMMENT ON COLUMN "baize-system".sys_user.created_by IS '创建人';
COMMENT ON COLUMN "baize-system".sys_user.created_time IS '创建时间';
COMMENT ON COLUMN "baize-system".sys_user.modified_by IS '更新人';
COMMENT ON COLUMN "baize-system".sys_user.modified_time IS '更新时间';
COMMENT ON COLUMN "baize-system".sys_user.remark IS '备注';

-- Permissions

ALTER TABLE "baize-system".sys_user OWNER TO postgres;
GRANT ALL ON TABLE "baize-system".sys_user TO postgres;


-- "baize-system".sys_user_post 定义

-- Drop table

-- DROP TABLE "baize-system".sys_user_post;

CREATE TABLE "baize-system".sys_user_post (
                                              user_id int8 NOT NULL, -- 用户ID
                                              post_id int8 NOT NULL -- 岗位ID
);
COMMENT ON TABLE "baize-system".sys_user_post IS '用户和岗位关联表1-N';

-- Column comments

COMMENT ON COLUMN "baize-system".sys_user_post.user_id IS '用户ID';
COMMENT ON COLUMN "baize-system".sys_user_post.post_id IS '岗位ID';

-- Permissions

ALTER TABLE "baize-system".sys_user_post OWNER TO postgres;
GRANT ALL ON TABLE "baize-system".sys_user_post TO postgres;


-- "baize-system".sys_user_role 定义

-- Drop table

-- DROP TABLE "baize-system".sys_user_role;

CREATE TABLE "baize-system".sys_user_role (
                                              user_id int8 NOT NULL, -- 用户ID
                                              role_id int8 NOT NULL -- 角色ID
);
COMMENT ON TABLE "baize-system".sys_user_role IS '用户和角色关联表N-1';

-- Column comments

COMMENT ON COLUMN "baize-system".sys_user_role.user_id IS '用户ID';
COMMENT ON COLUMN "baize-system".sys_user_role.role_id IS '角色ID';

-- Permissions

ALTER TABLE "baize-system".sys_user_role OWNER TO postgres;
GRANT ALL ON TABLE "baize-system".sys_user_role TO postgres;


-- "baize-system".sys_oper_log 定义

-- Drop table

-- DROP TABLE "baize-system".sys_oper_log;

CREATE TABLE "baize-system".sys_oper_log (
                                              id int8 NOT NULL, -- 日志主键
                                              tenant_id int8 NOT NULL, -- 租户ID
                                              title varchar(50) NULL, -- 模块标题
                                              business_type int4 DEFAULT 0 NULL, -- 业务类型
                                              method varchar(100) NULL, -- 方法名称
                                              request_method varchar(10) NULL, -- 请求方式
                                              operator_type int4 DEFAULT 0 NULL, -- 操作类别
                                              user_name varchar(50) NULL, -- 操作人员
                                              dept_name varchar(50) NULL, -- 部门名称
                                              oper_url varchar(255) NULL, -- 请求URL
                                              oper_ip varchar(128) NULL, -- 主机地址
                                              oper_location varchar(255) NULL, -- 操作地点
                                              oper_param text NULL, -- 请求参数
                                              json_result text NULL, -- 返回参数
                                              status int4 DEFAULT 0 NULL, -- 操作状态
                                              error_msg text NULL, -- 错误消息
                                              oper_time timestamp NULL, -- 操作时间
                                              cost_time int8 DEFAULT 0 NULL -- 消耗时间（毫秒）
);
COMMENT ON TABLE "baize-system".sys_oper_log IS '操作日志记录表';

-- Column comments

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

-- Permissions

ALTER TABLE "baize-system".sys_oper_log OWNER TO postgres;
GRANT ALL ON TABLE "baize-system".sys_oper_log TO postgres;

-- Indexes

CREATE INDEX idx_sys_oper_log_tenant_id ON "baize-system".sys_oper_log(tenant_id);
CREATE INDEX idx_sys_oper_log_business_type ON "baize-system".sys_oper_log(business_type);
CREATE INDEX idx_sys_oper_log_status ON "baize-system".sys_oper_log(status);
CREATE INDEX idx_sys_oper_log_oper_time ON "baize-system".sys_oper_log(oper_time);


-- "baize-system".sys_login_log 定义

-- Drop table

-- DROP TABLE "baize-system".sys_login_log;

CREATE TABLE "baize-system".sys_login_log (
                                              id int8 NOT NULL, -- 主键ID
                                              tenant_id int8 NOT NULL, -- 租户ID
                                              user_id int8 NULL, -- 用户ID
                                              user_name varchar(50) NULL, -- 用户名
                                              ipaddr varchar(128) NULL, -- IP地址
                                              login_location varchar(255) NULL, -- 登录地点
                                              browser varchar(50) NULL, -- 浏览器
                                              os varchar(50) NULL, -- 操作系统
                                              status varchar(1) DEFAULT '0'::character varying NOT NULL, -- 登录状态;0:成功,1:失败
                                              msg varchar(255) NULL, -- 提示消息
                                              login_time timestamp NULL -- 登录时间
);
COMMENT ON TABLE "baize-system".sys_login_log IS '登录日志表';

-- Column comments

COMMENT ON COLUMN "baize-system".sys_login_log.id IS '主键ID';
COMMENT ON COLUMN "baize-system".sys_login_log.tenant_id IS '租户ID';
COMMENT ON COLUMN "baize-system".sys_login_log.user_id IS '用户ID';
COMMENT ON COLUMN "baize-system".sys_login_log.user_name IS '用户名';
COMMENT ON COLUMN "baize-system".sys_login_log.ipaddr IS 'IP地址';
COMMENT ON COLUMN "baize-system".sys_login_log.login_location IS '登录地点';
COMMENT ON COLUMN "baize-system".sys_login_log.browser IS '浏览器';
COMMENT ON COLUMN "baize-system".sys_login_log.os IS '操作系统';
COMMENT ON COLUMN "baize-system".sys_login_log.status IS '登录状态;0:成功,1:失败';
COMMENT ON COLUMN "baize-system".sys_login_log.msg IS '提示消息';
COMMENT ON COLUMN "baize-system".sys_login_log.login_time IS '登录时间';

-- Permissions

ALTER TABLE "baize-system".sys_login_log OWNER TO postgres;
GRANT ALL ON TABLE "baize-system".sys_login_log TO postgres;

-- Indexes

CREATE INDEX idx_sys_login_log_user_id ON "baize-system".sys_login_log(user_id);
CREATE INDEX idx_sys_login_log_login_time ON "baize-system".sys_login_log(login_time);


-- "baize-system".sys_notice 定义

-- Drop table

-- DROP TABLE "baize-system".sys_notice;

CREATE TABLE "baize-system".sys_notice (
                                              id int8 NOT NULL, -- 主键ID
                                              tenant_id int8 NOT NULL, -- 租户ID
                                              notice_title varchar(200) NOT NULL, -- 公告标题
                                              notice_content text NULL, -- 公告内容
                                              notice_type varchar(1) DEFAULT '1'::character varying NOT NULL, -- 公告类型;1:通知,2:公告
                                              status varchar(1) DEFAULT '0'::character varying NOT NULL, -- 状态;0:正常,1:停用
                                              deleted varchar(1) DEFAULT '0'::character varying NOT NULL, -- 删除标记;0:正常标记,1:删除标记
                                              "version" int4 DEFAULT 0 NOT NULL, -- 乐观锁
                                              created_by int8 NOT NULL, -- 创建人
                                              created_time timestamp NOT NULL, -- 创建时间
                                              modified_by int8 NULL, -- 更新人
                                              modified_time timestamp NULL, -- 更新时间
                                              remark varchar(255) NULL -- 备注
);
COMMENT ON TABLE "baize-system".sys_notice IS '通知公告表';

-- Column comments

COMMENT ON COLUMN "baize-system".sys_notice.id IS '主键ID';
COMMENT ON COLUMN "baize-system".sys_notice.tenant_id IS '租户ID';
COMMENT ON COLUMN "baize-system".sys_notice.notice_title IS '公告标题';
COMMENT ON COLUMN "baize-system".sys_notice.notice_content IS '公告内容';
COMMENT ON COLUMN "baize-system".sys_notice.notice_type IS '公告类型;1:通知,2:公告';
COMMENT ON COLUMN "baize-system".sys_notice.status IS '状态;0:正常,1:停用';
COMMENT ON COLUMN "baize-system".sys_notice.deleted IS '删除标记;0:正常标记,1:删除标记';
COMMENT ON COLUMN "baize-system".sys_notice."version" IS '乐观锁';
COMMENT ON COLUMN "baize-system".sys_notice.created_by IS '创建人';
COMMENT ON COLUMN "baize-system".sys_notice.created_time IS '创建时间';
COMMENT ON COLUMN "baize-system".sys_notice.modified_by IS '更新人';
COMMENT ON COLUMN "baize-system".sys_notice.modified_time IS '更新时间';
COMMENT ON COLUMN "baize-system".sys_notice.remark IS '备注';

-- Permissions

ALTER TABLE "baize-system".sys_notice OWNER TO postgres;
GRANT ALL ON TABLE "baize-system".sys_notice TO postgres;

-- Indexes

CREATE INDEX idx_sys_notice_tenant_id ON "baize-system".sys_notice(tenant_id);


-- "baize-system".sys_job 定义

-- Drop table

-- DROP TABLE "baize-system".sys_job;

CREATE TABLE "baize-system".sys_job (
                                              id int8 NOT NULL, -- 主键ID
                                              tenant_id int8 NOT NULL, -- 租户ID
                                              job_name varchar(100) NOT NULL, -- 任务名称
                                              job_group varchar(50) NOT NULL, -- 任务组名
                                              invoke_target varchar(255) NULL, -- 调用目标字符串
                                              cron_expression varchar(50) NULL, -- cron执行表达式
                                              misfire_policy varchar(1) DEFAULT '0'::character varying NOT NULL, -- 计划执行策略;0:立即执行,1:执行一次
                                              concurrent varchar(1) DEFAULT '0'::character varying NOT NULL, -- 是否并发执行;0:允许,1:禁止
                                              status varchar(1) DEFAULT '0'::character varying NOT NULL, -- 状态;0:正常,1:暂停
                                              deleted varchar(1) DEFAULT '0'::character varying NOT NULL, -- 删除标记;0:正常标记,1:删除标记
                                              "version" int4 DEFAULT 0 NOT NULL, -- 乐观锁
                                              created_by int8 NOT NULL, -- 创建人
                                              created_time timestamp NOT NULL, -- 创建时间
                                              modified_by int8 NULL, -- 更新人
                                              modified_time timestamp NULL, -- 更新时间
                                              remark varchar(255) NULL -- 备注
);
COMMENT ON TABLE "baize-system".sys_job IS '定时任务表';

-- Column comments

COMMENT ON COLUMN "baize-system".sys_job.id IS '主键ID';
COMMENT ON COLUMN "baize-system".sys_job.tenant_id IS '租户ID';
COMMENT ON COLUMN "baize-system".sys_job.job_name IS '任务名称';
COMMENT ON COLUMN "baize-system".sys_job.job_group IS '任务组名';
COMMENT ON COLUMN "baize-system".sys_job.invoke_target IS '调用目标字符串（类全路径#方法名）';
COMMENT ON COLUMN "baize-system".sys_job.cron_expression IS 'cron执行表达式';
COMMENT ON COLUMN "baize-system".sys_job.misfire_policy IS '计划执行策略;0:立即执行,1:执行一次';
COMMENT ON COLUMN "baize-system".sys_job.concurrent IS '是否并发执行;0:允许,1:禁止';
COMMENT ON COLUMN "baize-system".sys_job.status IS '状态;0:正常,1:暂停';
COMMENT ON COLUMN "baize-system".sys_job.deleted IS '删除标记;0:正常标记,1:删除标记';
COMMENT ON COLUMN "baize-system".sys_job."version" IS '乐观锁';
COMMENT ON COLUMN "baize-system".sys_job.created_by IS '创建人';
COMMENT ON COLUMN "baize-system".sys_job.created_time IS '创建时间';
COMMENT ON COLUMN "baize-system".sys_job.modified_by IS '更新人';
COMMENT ON COLUMN "baize-system".sys_job.modified_time IS '更新时间';
COMMENT ON COLUMN "baize-system".sys_job.remark IS '备注';

-- Permissions

ALTER TABLE "baize-system".sys_job OWNER TO postgres;
GRANT ALL ON TABLE "baize-system".sys_job TO postgres;

-- Indexes

CREATE INDEX idx_sys_job_tenant_id ON "baize-system".sys_job(tenant_id);
CREATE INDEX idx_sys_job_job_group ON "baize-system".sys_job(job_group);