import { DeleteOutlined, EditOutlined, PlusOutlined } from '@ant-design/icons';
import type { ActionType, ProColumns } from '@ant-design/pro-components';
import {
  ModalForm,
  PageContainer,
  ProFormText,
  ProTable,
} from '@ant-design/pro-components';
import { Button, Modal, message, Space } from 'antd';
import React, { useRef, useState } from 'react';
import Authorized from '@/components/Authorized';
import {
  create${dto.className},
  delete${dto.className},
  get${dto.className}Page,
  update${dto.className},
} from '@/services/${dto.moduleName}/${dto.businessName}';
import { DEFAULT_PAGE_SIZE, toPageQuery } from '@/utils/pagination';

/**
 * ${dto.functionName!dto.tableComment!dto.tableName}管理页面
 */
const ${dto.className}: React.FC = () => {
  const actionRef = useRef<ActionType | null>(null);
  const [formModalVisible, setFormModalVisible] = useState(false);
  const [editingRecord, setEditingRecord] = useState<
    API.${dto.className}Dto | undefined
  >();

  const handleEdit = (record: API.${dto.className}Dto) => {
    setEditingRecord(record);
    setFormModalVisible(true);
  };

  const handleDelete = (record: API.${dto.className}Dto) => {
    Modal.confirm({
      title: '确认删除',
      content: \`确定要删除这条${dto.functionName!dto.tableComment!dto.tableName}记录吗？\`,
      okText: '确认',
      okButtonProps: { danger: true },
      cancelText: '取消',
      onOk: async () => {
        const res = await delete${dto.className}(record.id || '');
        if (res.success) {
          message.success('删除成功');
          actionRef.current?.reload();
        }
      },
    });
  };

  const handleSubmit = async (values: Partial<API.${dto.className}Dto>) => {
    try {
      let res: API.ApiResult;
      if (editingRecord) {
        res = await update${dto.className}(editingRecord.id || '', values);
      } else {
        res = await create${dto.className}(values);
      }
      if (res.success) {
        message.success(editingRecord ? '更新成功' : '创建成功');
        setFormModalVisible(false);
        setEditingRecord(undefined);
        actionRef.current?.reload();
        return true;
      }
      return false;
    } catch {
      message.error('操作失败');
      return false;
    }
  };

  const columns: ProColumns<API.${dto.className}Dto>[] = [
<#list table.columns as column>
<#if !column.baseColumn>
    {
      title: '${column.columnComment!column.columnName}',
      dataIndex: '${column.javaField}',
      width: 150,
    },
</#if>
</#list>
    {
      title: '操作',
      dataIndex: 'option',
      valueType: 'option',
      width: 150,
      fixed: 'right',
      render: (_, record) => (
        <Space>
          <Authorized permission="${dto.moduleName}:${dto.businessName}:edit">
            <Button
              type="link"
              size="small"
              icon={<EditOutlined />}
              onClick={() => handleEdit(record)}
            >
              编辑
            </Button>
          </Authorized>
          <Authorized permission="${dto.moduleName}:${dto.businessName}:remove">
            <Button
              type="link"
              size="small"
              danger
              icon={<DeleteOutlined />}
              onClick={() => handleDelete(record)}
            >
              删除
            </Button>
          </Authorized>
        </Space>
      ),
    },
  ];

  return (
    <PageContainer>
      <ProTable<API.${dto.className}Dto>
        headerTitle="${dto.functionName!dto.tableComment!dto.tableName}管理"
        actionRef={actionRef}
        rowKey="id"
        search={{
          labelWidth: 100,
        }}
        toolBarRender={() => [
          <Authorized permission="${dto.moduleName}:${dto.businessName}:add" key="add">
            <Button
              type="primary"
              icon={<PlusOutlined />}
              onClick={() => {
                setEditingRecord(undefined);
                setFormModalVisible(true);
              }}
            >
              新增${dto.functionName!dto.tableComment!dto.tableName}
            </Button>
          </Authorized>,
        ]}
        request={async (params) => {
          const { current, pageSize } = params;
          const res = await get${dto.className}Page(
            { current: current || 1, size: pageSize || DEFAULT_PAGE_SIZE },
            toPageQuery<API.${dto.className}Dto>(params),
          );
          if (res.success) {
            return {
              data: res.data.records ?? [],
              success: true,
              total: res.data.total ?? 0,
            };
          }
          return {
            data: [],
            success: false,
          };
        }}
        columns={columns}
        pagination={{
          pageSize: 10,
        }}
      />

      <ModalForm
        title={editingRecord ? '编辑${dto.functionName!dto.tableComment!dto.tableName}' : '新增${dto.functionName!dto.tableComment!dto.tableName}'}
        open={formModalVisible}
        onOpenChange={setFormModalVisible}
        onFinish={handleSubmit}
        initialValues={editingRecord}
        modalProps={{
          destroyOnHidden: true,
        }}
      >
<#list table.columns as column>
<#if !column.baseColumn>
        <ProFormText
          name="${column.javaField}"
          label="${column.columnComment!column.columnName}"
          placeholder="请输入${column.columnComment!column.columnName}"
        />
</#if>
</#list>
      </ModalForm>
    </PageContainer>
  );
};

export default ${dto.className};
