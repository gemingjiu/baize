import { DeleteOutlined, EditOutlined, PlusOutlined } from '@ant-design/icons';
import type { ActionType, ProColumns } from '@ant-design/pro-components';
import {
  ModalForm,
  PageContainer,
  ProFormDigit,
  ProFormSelect,
  ProFormText,
  ProFormTextArea,
  ProTable,
} from '@ant-design/pro-components';
import { Button, Modal, Space, Tag, message } from 'antd';
import React, { useRef, useState } from 'react';
import Authorized from '@/components/Authorized';
import {
  createConfig,
  deleteConfig,
  getConfigPage,
  updateConfig,
} from '@/services/system/config';

/**
 * 参数配置页面
 */
const Config: React.FC = () => {
  const actionRef = useRef<ActionType | null>(null);
  const [formModalVisible, setFormModalVisible] = useState(false);
  const [editingRecord, setEditingRecord] = useState<API.SysConfigDto | undefined>();

  const handleEdit = (record: API.SysConfigDto) => {
    setEditingRecord(record);
    setFormModalVisible(true);
  };

  const handleDelete = (record: API.SysConfigDto) => {
    Modal.confirm({
      title: '确认删除',
      content: `确定要删除参数 "${record.configName}" 吗？`,
      okText: '确认',
      okButtonProps: { danger: true },
      cancelText: '取消',
      onOk: async () => {
        const res = await deleteConfig(record.id || '');
        if (res.success) {
          message.success('删除成功');
          actionRef.current?.reload();
        }
      },
    });
  };

  const handleSubmit = async (values: Partial<API.SysConfigDto>) => {
    try {
      let res: API.ApiResult;
      if (editingRecord) {
        res = await updateConfig(editingRecord.id || '', values);
      } else {
        res = await createConfig(values);
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

  const columns: ProColumns<API.SysConfigDto>[] = [
    {
      title: '参数名称',
      dataIndex: 'configName',
      width: 160,
    },
    {
      title: '参数键名',
      dataIndex: 'configKey',
      width: 180,
      copyable: true,
    },
    {
      title: '参数键值',
      dataIndex: 'configValue',
      width: 180,
      search: false,
      ellipsis: true,
    },
    {
      title: '系统内置',
      dataIndex: 'configType',
      width: 100,
      valueType: 'select',
      valueEnum: {
        Y: { text: '是', status: 'Success' },
        N: { text: '否', status: 'Default' },
      },
      render: (_, record) =>
        record.configType === 'Y' ? <Tag color="blue">是</Tag> : <Tag>否</Tag>,
    },
    {
      title: '备注',
      dataIndex: 'remark',
      ellipsis: true,
      search: false,
    },
    {
      title: '操作',
      dataIndex: 'option',
      valueType: 'option',
      width: 140,
      fixed: 'right',
      render: (_, record) => (
        <Space>
          <Authorized permission="system:config:edit">
            <Button type="link" size="small" icon={<EditOutlined />} onClick={() => handleEdit(record)}>
              编辑
            </Button>
          </Authorized>
          <Authorized permission="system:config:delete">
            <Button type="link" size="small" danger icon={<DeleteOutlined />} onClick={() => handleDelete(record)}>
              删除
            </Button>
          </Authorized>
        </Space>
      ),
    },
  ];

  return (
    <PageContainer>
      <ProTable<API.SysConfigDto>
        headerTitle="参数配置"
        actionRef={actionRef}
        rowKey="id"
        search={{
          labelWidth: 100,
        }}
        toolBarRender={() => [
          <Authorized permission="system:config:add" key="add">
            <Button
              type="primary"
              icon={<PlusOutlined />}
              onClick={() => {
                setEditingRecord(undefined);
                setFormModalVisible(true);
              }}
            >
              新增参数
            </Button>
          </Authorized>,
        ]}
        request={async (params) => {
          const { current, pageSize, ...rest } = params;
          const res = await getConfigPage(
            { current: current || 1, size: pageSize || 10 },
            rest as API.SysConfigDto,
          );
          if (res.success) {
            return {
              data: res.data.records,
              success: true,
              total: res.data.total,
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
        title={editingRecord ? '编辑参数' : '新增参数'}
        open={formModalVisible}
        onOpenChange={setFormModalVisible}
        onFinish={handleSubmit}
        initialValues={editingRecord}
        modalProps={{
          destroyOnClose: true,
        }}
      >
        <ProFormText
          name="configName"
          label="参数名称"
          rules={[{ required: true, message: '请输入参数名称' }]}
          placeholder="请输入参数名称"
        />
        <ProFormText
          name="configKey"
          label="参数键名"
          rules={[{ required: true, message: '请输入参数键名' }]}
          placeholder="请输入参数键名"
          disabled={!!editingRecord}
        />
        <ProFormText
          name="configValue"
          label="参数键值"
          rules={[{ required: true, message: '请输入参数键值' }]}
          placeholder="请输入参数键值"
        />
        <ProFormSelect
          name="configType"
          label="系统内置"
          initialValue="N"
          options={[
            { label: '是', value: 'Y' },
            { label: '否', value: 'N' },
          ]}
        />
        <ProFormDigit name="sort" label="排序" initialValue={0} min={0} />
        <ProFormTextArea name="remark" label="备注" placeholder="请输入备注" />
      </ModalForm>
    </PageContainer>
  );
};

export default Config;
