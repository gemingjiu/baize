import { DeleteOutlined, EditOutlined, PlusOutlined } from '@ant-design/icons';
import type { ActionType, ProColumns } from '@ant-design/pro-components';
import {
  ModalForm,
  PageContainer,
  ProFormDatePicker,
  ProFormDigit,
  ProFormSelect,
  ProFormText,
  ProTable,
} from '@ant-design/pro-components';
import { Button, Modal, message, Space } from 'antd';
import React, { useRef, useState } from 'react';
import Authorized from '@/components/Authorized';
import {
  createTenant,
  deleteTenant,
  getTenantPage,
  updateTenant,
} from '@/services/system/tenant';

/**
 * 租户管理页面
 */
const Tenant: React.FC = () => {
  const actionRef = useRef<ActionType | null>(null);
  const [formModalVisible, setFormModalVisible] = useState(false);
  const [editingRecord, setEditingRecord] = useState<
    API.SysTenantDto | undefined
  >();

  /**
   * 编辑租户
   */
  const handleEdit = (record: API.SysTenantDto) => {
    setEditingRecord(record);
    setFormModalVisible(true);
  };

  /**
   * 删除租户
   */
  const handleDelete = (record: API.SysTenantDto) => {
    Modal.confirm({
      title: '确认删除',
      content: `确定要删除租户 "${record.tenantName}" 吗？`,
      okText: '确认',
      cancelText: '取消',
      onOk: async () => {
        const res = await deleteTenant(record.id || '');
        if (res.success) {
          message.success('删除成功');
          actionRef.current?.reload();
        }
      },
    });
  };

  const handleToggleStatus = async (record: API.SysTenantDto) => {
    const newStatus = record.status === '0' ? '1' : '0';
    const actionText = newStatus === '0' ? '启用' : '停用';
    Modal.confirm({
      title: `确认${actionText}`,
      content: `确定要${actionText}租户 "${record.tenantName}" 吗？`,
      okText: '确认',
      cancelText: '取消',
      onOk: async () => {
        const res = await updateTenant(record.id || '', { status: newStatus });
        if (res.success) {
          message.success(`${actionText}成功`);
          actionRef.current?.reload();
        }
      },
    });
  };

  /**
   * 提交表单
   */
  const handleSubmit = async (values: Partial<API.SysTenantDto>) => {
    try {
      let res: API.ApiResult;
      if (editingRecord) {
        res = await updateTenant(editingRecord.id || '', values);
      } else {
        res = await createTenant(values);
      }
      if (res.success) {
        message.success(editingRecord ? '更新成功' : '创建成功');
        setFormModalVisible(false);
        setEditingRecord(undefined);
        actionRef.current?.reload();
        return true;
      }
      return false;
    } catch (_error) {
      message.error('操作失败');
      return false;
    }
  };

  const columns: ProColumns<API.SysTenantDto>[] = [
    {
      title: '租户名称',
      dataIndex: 'tenantName',
      width: 150,
    },
    {
      title: '租户编码',
      dataIndex: 'tenantCode',
      width: 150,
    },
    {
      title: '域名',
      dataIndex: 'domain',
      width: 200,
    },
    {
      title: '最大用户数',
      dataIndex: 'maxUser',
      width: 100,
      search: false,
    },
    {
      title: '排序',
      dataIndex: 'sort',
      width: 80,
      search: false,
    },
    {
      title: '联系人',
      dataIndex: 'contactPerson',
      width: 120,
      search: false,
    },
    {
      title: '联系电话',
      dataIndex: 'contactPhone',
      width: 140,
      search: false,
    },
    {
      title: '联系邮箱',
      dataIndex: 'contactEmail',
      width: 180,
      search: false,
    },
    {
      title: '过期时间',
      dataIndex: 'expireTime',
      valueType: 'dateTime',
      width: 180,
      search: false,
    },
    {
      title: '状态',
      dataIndex: 'status',
      width: 80,
      valueType: 'select',
      valueEnum: {
        '0': { text: '正常', status: 'Success' },
        '1': { text: '停用', status: 'Error' },
      },
      search: false,
    },
    {
      title: '操作',
      dataIndex: 'option',
      valueType: 'option',
      width: 200,
      fixed: 'right',
      render: (_, record) => (
        <Space size="small">
          <Authorized permission="system:tenant:edit">
            <Button
              type="link"
              size="small"
              icon={<EditOutlined />}
              onClick={() => handleEdit(record)}
            >
              编辑
            </Button>
          </Authorized>
          <Authorized permission="system:tenant:status">
            <Button
              type="link"
              size="small"
              danger={record.status === '0'}
              onClick={() => handleToggleStatus(record)}
            >
              {record.status === '0' ? '停用' : '启用'}
            </Button>
          </Authorized>
          <Authorized permission="system:tenant:delete">
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
      <ProTable<API.SysTenantDto>
        headerTitle="租户管理"
        actionRef={actionRef}
        rowKey="id"
        search={{
          labelWidth: 100,
        }}
        toolBarRender={() => [
          <Authorized permission="system:tenant:add" key="add">
            <Button
              type="primary"
              icon={<PlusOutlined />}
              onClick={() => {
                setEditingRecord(undefined);
                setFormModalVisible(true);
              }}
            >
              新增租户
            </Button>
          </Authorized>,
        ]}
        request={async (params) => {
          const { current, pageSize, ...rest } = params;
          const res = await getTenantPage(
            { current: current || 1, size: pageSize || 10 },
            rest as API.SysTenantDto,
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
        title={editingRecord ? '编辑租户' : '新增租户'}
        open={formModalVisible}
        onOpenChange={setFormModalVisible}
        onFinish={handleSubmit}
        initialValues={editingRecord}
        modalProps={{
          destroyOnClose: true,
        }}
      >
        <ProFormText
          name="tenantName"
          label="租户名称"
          rules={[{ required: true, message: '请输入租户名称' }]}
          placeholder="请输入租户名称"
        />
        <ProFormText
          name="tenantCode"
          label="租户编码"
          rules={[{ required: true, message: '请输入租户编码' }]}
          placeholder="请输入租户编码"
          disabled={!!editingRecord}
        />
        <ProFormText name="domain" label="域名" placeholder="请输入域名" />
        <ProFormDigit
          name="maxUser"
          label="最大用户数"
          min={0}
          placeholder="请输入最大用户数"
        />
        <ProFormDigit
          name="sort"
          label="排序"
          min={0}
          placeholder="请输入排序"
        />
        <ProFormDatePicker
          name="expireTime"
          label="过期时间"
          placeholder="请选择过期时间"
        />
        <ProFormText
          name="contactPerson"
          label="联系人"
          placeholder="请输入联系人"
        />
        <ProFormText
          name="contactPhone"
          label="联系电话"
          placeholder="请输入联系电话"
        />
        <ProFormText
          name="contactEmail"
          label="联系邮箱"
          placeholder="请输入联系邮箱"
        />
        <ProFormSelect
          name="status"
          label="状态"
          valueEnum={{
            '0': '正常',
            '1': '停用',
          }}
          initialValue="0"
          placeholder="请选择状态"
        />
      </ModalForm>
    </PageContainer>
  );
};

export default Tenant;
