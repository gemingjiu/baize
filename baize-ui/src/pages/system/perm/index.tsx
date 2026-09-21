import {
  DeleteOutlined,
  EditOutlined,
  PlusOutlined,
  PlusCircleOutlined,
} from '@ant-design/icons';
import type { ActionType, ProColumns } from '@ant-design/pro-components';
import {
  ModalForm,
  PageContainer,
  ProFormText,
  ProFormTreeSelect,
  ProTable,
} from '@ant-design/pro-components';
import { Button, Modal, message, Space } from 'antd';
import React, { useEffect, useRef, useState } from 'react';
import Authorized from '@/components/Authorized';
import {
  createPerm,
  deletePerm,
  getPermPage,
  updatePerm,
} from '@/services/system/perm';

/**
 * 权限管理页面
 */
/**
 * 将扁平权限列表转为树形结构
 */
function buildPermTree(perms: API.SysPermDto[]): API.SysPermDto[] {
  const map = new Map<string, API.SysPermDto>();
  perms.forEach((perm) => {
    map.set(perm.id!, { ...perm, children: [] });
  });
  const roots: API.SysPermDto[] = [];
  perms.forEach((perm) => {
    const node = map.get(perm.id!);
    if (perm.parentId && map.has(perm.parentId)) {
      const parent = map.get(perm.parentId)!;
      if (!parent.children) parent.children = [];
      parent.children.push(node!);
    } else {
      roots.push(node!);
    }
  });
  return roots;
}

const Perm: React.FC = () => {
  const actionRef = useRef<ActionType | null>(null);
  const [formModalVisible, setFormModalVisible] = useState(false);
  const [editingRecord, setEditingRecord] = useState<
    API.SysPermDto | undefined
  >();
  const [parentId, setParentId] = useState<string | undefined>();
  const [permTree, setPermTree] = useState<any[]>([]);

  // 获取权限树选择数据
  useEffect(() => {
    const fetchPermTree = async () => {
      const res = await getPermPage({ current: 1, size: 1000 }, {});
      if (res.success) {
        const transformTree = (list: any[]): any[] => {
          return list.map((item) => ({
            label: `${item.permName} (${item.permCode})`,
            value: item.id,
            children: item.children ? transformTree(item.children) : undefined,
          }));
        };
        setPermTree(transformTree(res.data.records));
      }
    };
    fetchPermTree();
  }, [formModalVisible]);

  const handleEdit = (record: API.SysPermDto) => {
    setEditingRecord(record);
    setFormModalVisible(true);
  };

  const handleAddChild = (record: API.SysPermDto) => {
    setParentId(record.id);
    setEditingRecord(undefined);
    setFormModalVisible(true);
  };

  const handleDelete = (record: API.SysPermDto) => {
    Modal.confirm({
      title: '确认删除',
      content: `确定要删除权限 "${record.permName}" 吗？`,
      okText: '确认',
      okButtonProps: { danger: true },
      cancelText: '取消',
      onOk: async () => {
        const res = await deletePerm(record.id || '');
        if (res.success) {
          message.success('删除成功');
          actionRef.current?.reload();
        }
      },
    });
  };

  const handleSubmit = async (values: Partial<API.SysPermDto>) => {
    try {
      const submitData = {
        ...values,
        parentId: values.parentId || parentId,
      };
      let res: API.ApiResult;
      if (editingRecord) {
        res = await updatePerm(editingRecord.id || '', submitData);
      } else {
        res = await createPerm(submitData);
      }
      if (res.success) {
        message.success(editingRecord ? '更新成功' : '创建成功');
        setFormModalVisible(false);
        setEditingRecord(undefined);
        setParentId(undefined);
        actionRef.current?.reload();
        return true;
      }
      return false;
    } catch {
      message.error('操作失败');
      return false;
    }
  };

  const columns: ProColumns<API.SysPermDto>[] = [
    {
      title: '权限名称',
      dataIndex: 'permName',
      width: 200,
    },
    {
      title: '权限编码',
      dataIndex: 'permCode',
      width: 200,
    },
    {
      title: '操作',
      dataIndex: 'option',
      valueType: 'option',
      width: 200,
      fixed: 'right',
      render: (_, record) => (
        <Space size="small">
          <Authorized permission="system:perm:add">
            <Button
              type="link"
              size="small"
              icon={<PlusCircleOutlined />}
              onClick={() => handleAddChild(record)}
            >
              新增
            </Button>
          </Authorized>
          <Authorized permission="system:perm:edit">
            <Button
              type="link"
              size="small"
              icon={<EditOutlined />}
              onClick={() => handleEdit(record)}
            >
              编辑
            </Button>
          </Authorized>
          <Authorized permission="system:perm:delete">
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
      <ProTable<API.SysPermDto>
        headerTitle="权限管理"
        actionRef={actionRef}
        rowKey="id"
        search={{
          labelWidth: 100,
        }}
        toolBarRender={() => [
          <Authorized permission="system:perm:add" key="add">
            <Button
              type="primary"
              icon={<PlusOutlined />}
              onClick={() => {
                setEditingRecord(undefined);
                setFormModalVisible(true);
              }}
            >
              新增权限
            </Button>
          </Authorized>,
        ]}
        request={async (params) => {
          const { current, pageSize, ...rest } = params;
          const res = await getPermPage(
            { current: 1, size: 1000 },
            rest as API.SysPermDto,
          );
          if (res.success) {
            const treeData = buildPermTree(res.data.records);
            return {
              data: treeData,
              success: true,
              total: treeData.length,
            };
          }
          return {
            data: [],
            success: false,
          };
        }}
        columns={columns}
        pagination={false}
        childrenColumnName="children"
        defaultExpandAllRows
      />

      <ModalForm
        title={editingRecord ? '编辑权限' : '新增权限'}
        open={formModalVisible}
        onOpenChange={setFormModalVisible}
        onFinish={handleSubmit}
        initialValues={editingRecord}
        modalProps={{
          destroyOnClose: true,
        }}
      >
        <ProFormTreeSelect
          name="parentId"
          label="上级权限"
          placeholder="请选择上级权限"
          fieldProps={{
            treeDefaultExpandAll: true,
            treeData: [
              { label: '主类目', value: undefined },
              ...permTree,
            ],
          }}
          initialValue={parentId}
        />
        <ProFormText
          name="permName"
          label="权限名称"
          rules={[{ required: true, message: '请输入权限名称' }]}
          placeholder="请输入权限名称"
        />
        <ProFormText
          name="permCode"
          label="权限编码"
          rules={[{ required: true, message: '请输入权限编码' }]}
          placeholder="请输入权限编码，如：system:user:list"
          disabled={!!editingRecord}
        />
      </ModalForm>
    </PageContainer>
  );
};

export default Perm;
