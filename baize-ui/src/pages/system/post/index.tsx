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
  createPost,
  deletePost,
  getPostPage,
  updatePost,
} from '@/services/system/post';

/**
 * 岗位管理页面
 */
const Post: React.FC = () => {
  const actionRef = useRef<ActionType | null>(null);
  const [formModalVisible, setFormModalVisible] = useState(false);
  const [editingRecord, setEditingRecord] = useState<
    API.SysPostDto | undefined
  >();

  const handleEdit = (record: API.SysPostDto) => {
    setEditingRecord(record);
    setFormModalVisible(true);
  };

  const handleDelete = (record: API.SysPostDto) => {
    Modal.confirm({
      title: '确认删除',
      content: `确定要删除岗位 "${record.postName}" 吗？`,
      okText: '确认',
      okButtonProps: { danger: true },
      cancelText: '取消',
      onOk: async () => {
        const res = await deletePost(record.id || '');
        if (res.success) {
          message.success('删除成功');
          actionRef.current?.reload();
        }
      },
    });
  };

  const handleSubmit = async (values: Partial<API.SysPostDto>) => {
    try {
      let res: API.ApiResult;
      if (editingRecord) {
        res = await updatePost(editingRecord.id || '', values);
      } else {
        res = await createPost(values);
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

  const columns: ProColumns<API.SysPostDto>[] = [
    {
      title: '岗位编码',
      dataIndex: 'postCode',
      width: 150,
    },
    {
      title: '岗位名称',
      dataIndex: 'postName',
      width: 150,
    },
    {
      title: '操作',
      dataIndex: 'option',
      valueType: 'option',
      width: 150,
      fixed: 'right',
      render: (_, record) => (
        <Space>
          <Authorized permission="system:post:edit">
            <Button
              type="link"
              size="small"
              icon={<EditOutlined />}
              onClick={() => handleEdit(record)}
            >
              编辑
            </Button>
          </Authorized>
          <Authorized permission="system:post:delete">
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
      <ProTable<API.SysPostDto>
        headerTitle="岗位管理"
        actionRef={actionRef}
        rowKey="id"
        search={{
          labelWidth: 100,
        }}
        toolBarRender={() => [
          <Authorized permission="system:post:add" key="add">
            <Button
              type="primary"
              icon={<PlusOutlined />}
              onClick={() => {
                setEditingRecord(undefined);
                setFormModalVisible(true);
              }}
            >
              新增岗位
            </Button>
          </Authorized>,
        ]}
        request={async (params) => {
          const { current, pageSize, ...rest } = params;
          const res = await getPostPage(
            { current: current || 1, size: pageSize || 10 },
            rest as API.SysPostDto,
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
        title={editingRecord ? '编辑岗位' : '新增岗位'}
        open={formModalVisible}
        onOpenChange={setFormModalVisible}
        onFinish={handleSubmit}
        initialValues={editingRecord}
        modalProps={{
          destroyOnClose: true,
        }}
      >
        <ProFormText
          name="postCode"
          label="岗位编码"
          rules={[{ required: true, message: '请输入岗位编码' }]}
          placeholder="请输入岗位编码"
          disabled={!!editingRecord}
        />
        <ProFormText
          name="postName"
          label="岗位名称"
          rules={[{ required: true, message: '请输入岗位名称' }]}
          placeholder="请输入岗位名称"
        />
      </ModalForm>
    </PageContainer>
  );
};

export default Post;
