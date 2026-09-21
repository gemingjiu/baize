import {
  DeleteOutlined,
  EditOutlined,
  KeyOutlined,
  PlusOutlined,
  UserOutlined,
} from '@ant-design/icons';
import type { ActionType, ProColumns } from '@ant-design/pro-components';
import {
  ModalForm,
  PageContainer,
  ProFormSelect,
  ProFormText,
  ProFormTreeSelect,
  ProTable,
} from '@ant-design/pro-components';
import { Button, Form, Input, Modal, message, Space, Transfer } from 'antd';
import React, { useEffect, useRef, useState } from 'react';
import Authorized from '@/components/Authorized';
import { getDeptTreeSelect } from '@/services/system/dept';
import { transformDeptTree } from '@/utils/tree';
import { getRolePage } from '@/services/system/role';
import {
  assignUserRoles,
  changeUserStatus,
  createUser,
  deleteUser,
  getUserPage,
  getUserRoles,
  resetPassword,
  updateUser,
} from '@/services/system/user';

const User: React.FC = () => {
  const actionRef = useRef<ActionType | null>(null);
  const [formModalVisible, setFormModalVisible] = useState(false);
  const [passwordModalVisible, setPasswordModalVisible] = useState(false);
  const [roleModalVisible, setRoleModalVisible] = useState(false);
  const [editingRecord, setEditingRecord] = useState<
    API.SysUserDto | undefined
  >();
  const [resetUserRecord, setResetUserRecord] = useState<
    API.SysUserDto | undefined
  >();
  const [assignRoleRecord, setAssignRoleRecord] = useState<
    API.SysUserDto | undefined
  >();
  const [deptTree, setDeptTree] = useState<any[]>([]);
  const [allRoles, setAllRoles] = useState<any[]>([]);
  const [selectedRoles, setSelectedRoles] = useState<string[]>([]);
  const [passwordForm] = Form.useForm();

  useEffect(() => {
    const fetchDeptTree = async () => {
      const res = await getDeptTreeSelect();
      if (res.success) {
        setDeptTree(transformDeptTree(res.data));
      }
    };
    fetchDeptTree();
  }, []);

  useEffect(() => {
    if (roleModalVisible) {
      const fetchRoles = async () => {
        const res = await getRolePage({ current: 1, size: 1000 }, {});
        if (res.success) {
          setAllRoles(
            res.data.records.map((role: any) => ({
              key: role.id,
              title: `${role.roleName} (${role.roleCode || '-'})`,
            })),
          );
        }
      };
      fetchRoles();

      if (assignRoleRecord?.id) {
        const userId = assignRoleRecord.id;
        const fetchUserRoles = async () => {
          const res = await getUserRoles(userId);
          if (res.success) {
            setSelectedRoles(res.data || []);
          }
        };
        fetchUserRoles();
      }
    }
  }, [roleModalVisible, assignRoleRecord]);

  const handleEdit = (record: API.SysUserDto) => {
    setEditingRecord(record);
    setFormModalVisible(true);
  };

  const handleDelete = (record: API.SysUserDto) => {
    Modal.confirm({
      title: '确认删除',
      content: `确定要删除用户 "${record.userName}" 吗？`,
      okText: '确认',
      okButtonProps: { danger: true },
      cancelText: '取消',
      onOk: async () => {
        const res = await deleteUser(record.id || '');
        if (res.success) {
          message.success('删除成功');
          actionRef.current?.reload();
        }
      },
    });
  };

  const handleToggleStatus = async (record: API.SysUserDto) => {
    const newStatus = record.status === '0' ? '1' : '0';
    const actionText = newStatus === '0' ? '启用' : '停用';
    Modal.confirm({
      title: `确认${actionText}`,
      content: `确定要${actionText}用户 "${record.userName}" 吗？`,
      okText: '确认',
      cancelText: '取消',
      onOk: async () => {
        const res = await changeUserStatus(record.id || '', newStatus);
        if (res.success) {
          message.success(`${actionText}成功`);
          actionRef.current?.reload();
        }
      },
    });
  };

  const handleResetPassword = (record: API.SysUserDto) => {
    setResetUserRecord(record);
    passwordForm.resetFields();
    setPasswordModalVisible(true);
  };

  const handlePasswordSubmit = async () => {
    try {
      const values = await passwordForm.validateFields();
      const res = await resetPassword(
        resetUserRecord?.id || '',
        values.newPassword,
      );
      if (res.success) {
        message.success('密码重置成功');
        setPasswordModalVisible(false);
        return true;
      }
      return false;
    } catch {
      return false;
    }
  };

  const handleAssignRoles = (record: API.SysUserDto) => {
    setAssignRoleRecord(record);
    setRoleModalVisible(true);
  };

  const handleAssignSubmit = async () => {
    try {
      const res = await assignUserRoles(
        assignRoleRecord?.id || '',
        selectedRoles,
      );
      if (res.success) {
        message.success('角色分配成功');
        setRoleModalVisible(false);
        return true;
      }
      return false;
    } catch {
      message.error('分配失败');
      return false;
    }
  };

  const handleSubmit = async (values: any) => {
    try {
      let res: API.ApiResult;
      if (editingRecord) {
        const { password, ...rest } = values;
        res = await updateUser(
          editingRecord.id || '',
          password ? { ...rest, password } : rest,
        );
      } else {
        res = await createUser(values);
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

  const columns: ProColumns<API.SysUserDto>[] = [
    {
      title: '用户名',
      dataIndex: 'userName',
      width: 120,
    },
    {
      title: '昵称',
      dataIndex: 'nickName',
      width: 120,
    },
    {
      title: '邮箱',
      dataIndex: 'email',
      width: 180,
    },
    {
      title: '手机号',
      dataIndex: 'phone',
      width: 130,
    },
    {
      title: '性别',
      dataIndex: 'gender',
      width: 80,
      valueType: 'select',
      valueEnum: {
        '0': '男',
        '1': '女',
        '2': '未知',
      },
      search: false,
    },
    {
      title: '用户类型',
      dataIndex: 'userType',
      width: 100,
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
      width: 320,
      fixed: 'right',
      render: (_, record) => (
        <Space size="small">
          <Authorized permission="system:user:edit">
            <Button
              type="link"
              size="small"
              icon={<EditOutlined />}
              onClick={() => handleEdit(record)}
            >
              编辑
            </Button>
          </Authorized>
          <Authorized permission="system:user:reset">
            <Button
              type="link"
              size="small"
              icon={<KeyOutlined />}
              onClick={() => handleResetPassword(record)}
            >
              重置密码
            </Button>
          </Authorized>
          <Authorized permission="system:user:assign">
            <Button
              type="link"
              size="small"
              icon={<UserOutlined />}
              onClick={() => handleAssignRoles(record)}
            >
              分配角色
            </Button>
          </Authorized>
          <Authorized permission="system:user:status">
            <Button
              type="link"
              size="small"
              danger={record.status === '0'}
              onClick={() => handleToggleStatus(record)}
            >
              {record.status === '0' ? '停用' : '启用'}
            </Button>
          </Authorized>
          <Authorized permission="system:user:delete">
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
      <ProTable<API.SysUserDto>
        headerTitle="用户管理"
        actionRef={actionRef}
        rowKey="id"
        search={{
          labelWidth: 100,
        }}
        toolBarRender={() => [
          <Authorized permission="system:user:add" key="add">
            <Button
              type="primary"
              icon={<PlusOutlined />}
              onClick={() => {
                setEditingRecord(undefined);
                setFormModalVisible(true);
              }}
            >
              新增用户
            </Button>
          </Authorized>,
        ]}
        request={async (params) => {
          const { current, pageSize, ...rest } = params;
          const res = await getUserPage(
            { current: current || 1, size: pageSize || 10 },
            rest as Partial<API.SysUserDto>,
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
        title={editingRecord ? '编辑用户' : '新增用户'}
        open={formModalVisible}
        onOpenChange={setFormModalVisible}
        onFinish={handleSubmit}
        initialValues={editingRecord}
        modalProps={{
          destroyOnClose: true,
        }}
      >
        <ProFormText
          name="userName"
          label="用户名"
          rules={[{ required: true, message: '请输入用户名' }]}
          placeholder="请输入用户名"
          disabled={!!editingRecord}
        />
        <ProFormText name="nickName" label="昵称" placeholder="请输入昵称" />
        <ProFormText.Password
          name="password"
          label="密码"
          rules={
            editingRecord ? [] : [{ required: true, message: '请输入密码' }]
          }
          placeholder={editingRecord ? '不修改请留空' : '请输入密码'}
        />
        <ProFormText
          name="email"
          label="邮箱"
          placeholder="请输入邮箱"
          rules={[
            {
              pattern: /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/,
              message: '请输入有效的邮箱地址',
            },
          ]}
        />
        <ProFormText
          name="phone"
          label="手机号"
          placeholder="请输入手机号"
          rules={[
            {
              pattern: /^1[3-9]\d{9}$/,
              message: '请输入有效的手机号码',
            },
          ]}
        />
        <ProFormTreeSelect
          name="deptId"
          label="部门"
          placeholder="请选择部门"
          fieldProps={{
            treeDefaultExpandAll: true,
            treeData: deptTree,
          }}
        />
        <ProFormSelect
          name="gender"
          label="性别"
          valueEnum={{
            '0': '男',
            '1': '女',
            '2': '未知',
          }}
          initialValue="2"
          placeholder="请选择性别"
        />
      </ModalForm>

      <Modal
        title="重置密码"
        open={passwordModalVisible}
        onOk={handlePasswordSubmit}
        onCancel={() => setPasswordModalVisible(false)}
        destroyOnClose
      >
        <Form form={passwordForm} layout="vertical">
          <Form.Item
            name="newPassword"
            label="新密码"
            rules={[
              { required: true, message: '请输入新密码' },
              { min: 6, message: '密码长度不能少于6位' },
            ]}
          >
            <Input.Password placeholder="请输入新密码" />
          </Form.Item>
          <Form.Item
            name="confirmPassword"
            label="确认密码"
            dependencies={['newPassword']}
            rules={[
              { required: true, message: '请再次输入密码' },
              ({ getFieldValue }) => ({
                validator(_, value) {
                  if (!value || getFieldValue('newPassword') === value) {
                    return Promise.resolve();
                  }
                  return Promise.reject(new Error('两次输入的密码不一致'));
                },
              }),
            ]}
          >
            <Input.Password placeholder="请再次输入密码" />
          </Form.Item>
        </Form>
      </Modal>

      <Modal
        title={`分配角色 - ${assignRoleRecord?.userName}`}
        open={roleModalVisible}
        onOk={handleAssignSubmit}
        onCancel={() => setRoleModalVisible(false)}
        width={600}
        destroyOnClose
      >
        <Transfer
          titles={['待选角色', '已选角色']}
          dataSource={allRoles}
          targetKeys={selectedRoles}
          rowKey={(item) => item.key}
          showSearch
          onChange={(nextTargetKeys) =>
            setSelectedRoles(nextTargetKeys as string[])
          }
          render={(item) => item.title}
        />
      </Modal>
    </PageContainer>
  );
};

export default User;
