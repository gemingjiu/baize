import {
  DeleteOutlined,
  DownloadOutlined,
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
import type { FormInstance } from 'antd';
import { Button, Form, Input, Modal, message, Space, Transfer } from 'antd';
import React, { useCallback, useEffect, useRef, useState } from 'react';
import Authorized from '@/components/Authorized';
import { useDict } from '@/hooks/useDict';
import { useExportXlsx } from '@/hooks/useExportXlsx';
import { getDeptTreeSelect } from '@/services/system/dept';
import { getPostList, getPostPage } from '@/services/system/post';
import { getRolePage } from '@/services/system/role';
import {
  assignUserRoles,
  changeUserStatus,
  createUser,
  deleteUser,
  exportUsers,
  getUserPage,
  getUserPosts,
  getUserRoles,
  resetPassword,
  updateUser,
} from '@/services/system/user';
import {
  DEFAULT_PAGE_SIZE,
  fetchAllPages,
  toPageQuery,
} from '@/utils/pagination';
import { transformDeptTree } from '@/utils/tree';

const User: React.FC = () => {
  const actionRef = useRef<ActionType | null>(null);
  const { toValueEnum, getDictOptions } = useDict();
  // 表格查询表单实例，导出时直接取当前查询条件
  const formRef = useRef<FormInstance<API.SysUserDto> | undefined>(undefined);
  const [exporting, handleExport] = useExportXlsx<API.SysUserDto>(exportUsers);
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
  // 岗位选项（P6）：用户表单岗位多选数据源（登录即可见的启用岗位）
  const [postOptions, setPostOptions] = useState<
    { label: string; value: string }[]
  >([]);
  const [allRoles, setAllRoles] = useState<{ key: string; title: string }[]>(
    [],
  );
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

  // 岗位选项拉取（P6）：失败时保底空列表，表单岗位项仍可打开只是无选项
  useEffect(() => {
    const fetchPosts = async () => {
      try {
        const res = await getPostList({ skipErrorHandler: true });
        if (res?.success && Array.isArray(res.data)) {
          setPostOptions(
            res.data
              .filter((post) => post.id)
              .map((post) => ({
                label: post.postName || `岗位 ${post.id}`,
                value: post.id as string,
              })),
          );
        }
      } catch (_e) {
        // 忽略：保底空列表
      }
    };
    fetchPosts();
  }, []);

  useEffect(() => {
    if (roleModalVisible) {
      const fetchRoles = async () => {
        // 全量角色列表：按 total/size 逐页拉取，避免写死 size: 1000 造成静默截断
        const records = await fetchAllPages<API.SysRoleDto>((page) =>
          getRolePage(page, {}),
        );
        setAllRoles(
          records
            .filter((role) => role.id)
            .map((role) => ({
              key: String(role.id),
              title: `${role.roleName ?? '-'} (${role.roleCode || '-'})`,
            })),
        );
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

  /** 按当前查询条件导出用户列表（无查询条件即全量导出） */
  const handleExportUsers = useCallback(() => {
    // getFieldsValue() 拿到的是查询表单当前值，空值由 services 层按 {} 处理后传给后端
    handleExport(formRef.current?.getFieldsValue() ?? {});
  }, [handleExport]);

  const handleEdit = (record: API.SysUserDto) => {
    setEditingRecord(record);
    // P6：编辑打开时拉取该用户的岗位绑定回显（独立接口避免污染用户主缓存）
    if (record.id) {
      getUserPosts(record.id, { skipErrorHandler: true })
        .then((res) => {
          if (res?.success && Array.isArray(res.data)) {
            setEditingRecord({ ...record, postIds: res.data });
          }
        })
        .catch(() => {});
    }
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
      valueEnum: toValueEnum('sys_user_sex'),
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
      valueEnum: toValueEnum('sys_normal_disable', {
        '0': 'Success',
        '1': 'Error',
      }),
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
          <Authorized permission="system:user:resetPwd">
            <Button
              type="link"
              size="small"
              icon={<KeyOutlined />}
              onClick={() => handleResetPassword(record)}
            >
              重置密码
            </Button>
          </Authorized>
          <Authorized permission="system:user:edit">
            <Button
              type="link"
              size="small"
              icon={<UserOutlined />}
              onClick={() => handleAssignRoles(record)}
            >
              分配角色
            </Button>
          </Authorized>
          <Authorized permission="system:user:edit">
            <Button
              type="link"
              size="small"
              danger={record.status === '0'}
              onClick={() => handleToggleStatus(record)}
            >
              {record.status === '0' ? '停用' : '启用'}
            </Button>
          </Authorized>
          <Authorized permission="system:user:remove">
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
        formRef={formRef}
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
          <Authorized permission="system:user:export" key="export">
            <Button
              icon={<DownloadOutlined />}
              loading={exporting}
              onClick={handleExportUsers}
            >
              导出
            </Button>
          </Authorized>,
        ]}
        request={async (params) => {
          const { current, pageSize } = params;
          const res = await getUserPage(
            { current: current || 1, size: pageSize || DEFAULT_PAGE_SIZE },
            toPageQuery<API.SysUserDto>(params),
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
        title={editingRecord ? '编辑用户' : '新增用户'}
        open={formModalVisible}
        onOpenChange={setFormModalVisible}
        onFinish={handleSubmit}
        initialValues={editingRecord}
        modalProps={{
          destroyOnHidden: true,
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
            editingRecord
              ? []
              : [{ min: 8, max: 20, message: '密码长度8-20位' }]
          }
          placeholder={
            editingRecord
              ? '不修改请留空'
              : '留空则使用系统初始密码（参数 sys.user.initPassword）'
          }
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
          name="postIds"
          label="岗位"
          placeholder="请选择岗位（可多选）"
          options={postOptions}
          fieldProps={{ mode: 'multiple' }}
        />
        <ProFormSelect
          name="gender"
          label="性别"
          options={getDictOptions('sys_user_sex')}
          initialValue="2"
          placeholder="请选择性别"
        />
      </ModalForm>

      <Modal
        title="重置密码"
        open={passwordModalVisible}
        onOk={handlePasswordSubmit}
        onCancel={() => setPasswordModalVisible(false)}
        destroyOnHidden
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
        destroyOnHidden
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
