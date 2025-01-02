DROP TABLE IF EXISTS gen_table;
CREATE TABLE gen_table(
                          id VARCHAR(32) NOT NULL,
                          table_name VARCHAR(200) NOT NULL DEFAULT  '',
                          table_comment VARCHAR(500) NOT NULL DEFAULT  '',
                          relation_table VARCHAR(64),
                          relation_fk_name VARCHAR(64),
                          class_name VARCHAR(100) NOT NULL DEFAULT  '',
                          tpl_category VARCHAR(200) NOT NULL DEFAULT  'crud',
                          vue_type VARCHAR(200) NOT NULL DEFAULT  'crud',
                          package_name VARCHAR(100) NOT NULL,
                          module_name VARCHAR(30) NOT NULL,
                          business_name VARCHAR(30) NOT NULL,
                          function_name VARCHAR(50) NOT NULL,
                          author VARCHAR(50) NOT NULL,
                          gen_type VARCHAR(1) NOT NULL DEFAULT  '0',
                          gen_path VARCHAR(200) NOT NULL DEFAULT  '/',
                          options VARCHAR(500),
                          status VARCHAR(1) NOT NULL DEFAULT  '1',
                          deleted VARCHAR(1) NOT NULL DEFAULT  '1',
                          version VARCHAR(32) NOT NULL DEFAULT  0,
                          created_by VARCHAR(32) NOT NULL DEFAULT  '',
                          created_time TIMESTAMP NOT NULL,
                          modified_by VARCHAR(32) DEFAULT  '',
                          modified_time TIMESTAMP,
                          remark VARCHAR(500),
                          PRIMARY KEY (id)
);

COMMENT ON TABLE gen_table IS '代码生成业务表';
COMMENT ON COLUMN gen_table.id IS '主键ID';
COMMENT ON COLUMN gen_table.table_name IS '表名';
COMMENT ON COLUMN gen_table.table_comment IS '表描述';
COMMENT ON COLUMN gen_table.relation_table IS '关联表名';
COMMENT ON COLUMN gen_table.relation_fk_name IS '外键名';
COMMENT ON COLUMN gen_table.class_name IS '实体类名称';
COMMENT ON COLUMN gen_table.tpl_category IS '使用的模板';
COMMENT ON COLUMN gen_table.vue_type IS 'Vue版本';
COMMENT ON COLUMN gen_table.package_name IS '包路径';
COMMENT ON COLUMN gen_table.module_name IS '模块名';
COMMENT ON COLUMN gen_table.business_name IS '业务名';
COMMENT ON COLUMN gen_table.function_name IS '功能名';
COMMENT ON COLUMN gen_table.author IS '作者';
COMMENT ON COLUMN gen_table.gen_type IS '生成方式;0:gzip 1:自定义';
COMMENT ON COLUMN gen_table.gen_path IS '生成路径';
COMMENT ON COLUMN gen_table.options IS '其他生成选项';
COMMENT ON COLUMN gen_table.status IS '启用状态;0:记录无效,1:记录有效';
COMMENT ON COLUMN gen_table.deleted IS '删除标记;0:删除标记,1:正常标记';
COMMENT ON COLUMN gen_table.version IS '乐观锁';
COMMENT ON COLUMN gen_table.created_by IS '创建人';
COMMENT ON COLUMN gen_table.created_time IS '创建时间';
COMMENT ON COLUMN gen_table.modified_by IS '更新人';
COMMENT ON COLUMN gen_table.modified_time IS '更新时间';
COMMENT ON COLUMN gen_table.remark IS '备注';

DROP TABLE IF EXISTS gen_table_column;
CREATE TABLE gen_table_column(
                                 id VARCHAR(32) NOT NULL,
                                 table_id VARCHAR(32) NOT NULL,
                                 column_name VARCHAR(200) NOT NULL,
                                 column_comment VARCHAR(500) NOT NULL,
                                 column_type VARCHAR(100) NOT NULL,
                                 java_type VARCHAR(500) NOT NULL,
                                 java_field VARCHAR(200) NOT NULL,
                                 pk VARCHAR(1) NOT NULL DEFAULT  '0',
                                 increment VARCHAR(1) NOT NULL DEFAULT  '0',
                                 required VARCHAR(1) NOT NULL DEFAULT  '0',
                                 insert VARCHAR(1) NOT NULL DEFAULT  '0',
                                 edit VARCHAR(1) NOT NULL DEFAULT  '0',
                                 list VARCHAR(1) NOT NULL DEFAULT  '0',
                                 query VARCHAR(1) NOT NULL DEFAULT  '0',
                                 query_type VARCHAR(200) NOT NULL DEFAULT  'EQ',
                                 visible_type VARCHAR(200) NOT NULL DEFAULT  '',
                                 dict_type VARCHAR(200),
                                 sort INT4 NOT NULL,
                                 status VARCHAR(1) NOT NULL DEFAULT  '1',
                                 deleted VARCHAR(1) NOT NULL DEFAULT  '1',
                                 version VARCHAR(32) NOT NULL DEFAULT  '0',
                                 created_by VARCHAR(32) NOT NULL DEFAULT  '',
                                 created_time TIMESTAMP NOT NULL,
                                 modified_by VARCHAR(32) DEFAULT  '',
                                 modified_time TIMESTAMP,
                                 remark VARCHAR(500),
                                 PRIMARY KEY (id)
);

COMMENT ON TABLE gen_table_column IS '代码生成业务表字段';
COMMENT ON COLUMN gen_table_column.id IS '主键ID';
COMMENT ON COLUMN gen_table_column.table_id IS '表编号';
COMMENT ON COLUMN gen_table_column.column_name IS '列名称';
COMMENT ON COLUMN gen_table_column.column_comment IS '列描述';
COMMENT ON COLUMN gen_table_column.column_type IS '列类型';
COMMENT ON COLUMN gen_table_column.java_type IS 'JAVA类型';
COMMENT ON COLUMN gen_table_column.java_field IS 'JAVA字段名';
COMMENT ON COLUMN gen_table_column.pk IS '是否主键字段';
COMMENT ON COLUMN gen_table_column.increment IS '是否自增字段';
COMMENT ON COLUMN gen_table_column.required IS '是否必填字段';
COMMENT ON COLUMN gen_table_column.insert IS '是否插入字段';
COMMENT ON COLUMN gen_table_column.edit IS '是否编辑字段';
COMMENT ON COLUMN gen_table_column.list IS '是否列表字段';
COMMENT ON COLUMN gen_table_column.query IS '是否查询字段';
COMMENT ON COLUMN gen_table_column.query_type IS '查询方式;等于、不等于、大于、小于、范围';
COMMENT ON COLUMN gen_table_column.visible_type IS '显示类型;文本框、文本域、下拉框、复选框、单选框、日期控件';
COMMENT ON COLUMN gen_table_column.dict_type IS '字典类型';
COMMENT ON COLUMN gen_table_column.sort IS '排序';
COMMENT ON COLUMN gen_table_column.status IS '启用状态;0:记录无效,1:记录有效';
COMMENT ON COLUMN gen_table_column.deleted IS '删除标记;0:删除标记,1:正常标记';
COMMENT ON COLUMN gen_table_column.version IS '乐观锁';
COMMENT ON COLUMN gen_table_column.created_by IS '创建人';
COMMENT ON COLUMN gen_table_column.created_time IS '创建时间';
COMMENT ON COLUMN gen_table_column.modified_by IS '更新人';
COMMENT ON COLUMN gen_table_column.modified_time IS '更新时间';
COMMENT ON COLUMN gen_table_column.remark IS '备注';


-- CREATE UNIQUE INDEX gen_table_column_pkey ON gen_table_column(id);