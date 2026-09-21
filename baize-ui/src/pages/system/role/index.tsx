import {
  DeleteOutlined,
  EditOutlined,
  MenuOutlined,
  PlusOutlined,
  SafetyOutlined,
} from '@ant-design/icons';
import type { ActionType, ProColumns } from '@ant-design/pro-components';
import {
  ModalForm,
  PageContainer,
  ProFormSwitch,
  ProFormText,
  ProTable,
} from '@ant-design/pro-components';
import { Button, Modal, message, Space, Tag, Transfer, Tree } from 'antd';
import React, { useEffect, useRef, useState } from 'react';
import Authorized from '@/components/Authorized';
import { getMenuTreeSelect } from '@/services/system/menu';
import { getPermPage } from '@/services/system/perm';
import {
  assignRoleMenus,
  assignRolePerms,
  createRole,
  deleteRole,
  getRoleMenus,
  getRolePage,
  getRolePerms,
  updateRole,
} from '@/services/system/role';

const Role: React.FC = () => {
  const actionRef = useRef<ActionType | null>(null);
  const [formModalVisible, setFormModalVisible] = useState(false);
  const [menuModalVisible, setMenuModalVisible] = useState(false);
  const [permModalVisible, setPermModalVisible] = useState(false);
  const [editingRecord, setEditingRecord] = useState<
    API.SysRoleDto | undefined
  >();
  const [assignMenuRecord, setAssignMenuRecord] = useState<
    API.SysRoleDto | undefined
  >();
  const [assignPermRecord, setAssignPermRecord] = useState<
    API.SysRoleDto | undefined
  >();
  const [menuTree, setMenuTree] = useState<any[]>([]);
  const [checkedMenuKeys, setCheckedMenuKeys] = useState<string[]>([]);
  const [allPerms, setAllPerms] = useState<any[]>([]);
  const [selectedPermKeys, setSelectedPermKeys] = useState<string[]>([]);

  useEffect(() => {
    if (menuModalVisible && assignMenuRecord) {
      const fetchMenus = async () => {
        const res = await getMenuTreeSelect();
        if (res.success) {
          const transformTree = (list: any[]): any[] => {
            return list.map((item) => ({
              key: item.id,
              title: item.menuName,
              children: item.children
                ? transformTree(item.children)
                : undefined,
            }));
          };
          setMenuTree(transformTree(res.data));
        }
        const roleMenus = await getRoleMenus(assignMenuRecord?.id || '');
        if (roleMenus.success) {
          setCheckedMenuKeys(roleMenus.data || []);
        }
      };
      fetchMenus();
    }
  }, [menuModalVisible, assignMenuRecord]);

  useEffect(() => {
    if (permModalVisible) {
      const fetchPerms = async () => {
        const res = await getPermPage({ current: 1, size: 1000 }, {});
        if (res.success) {
          setAllPerms(
            res.data.records.map((perm: any) => ({
              key: perm.id,
              title: `${perm.permName} (${perm.permCode})`,
            })),
          );
        }
      };
      fetchPerms();

      if (assignPermRecord) {
        const fetchRolePerms = async () => {
          const res = await getRolePerms(assignPermRecord?.id || '');
          if (res.success) {
            setSelectedPermKeys(res.data || []);
          }
        };
        fetchRolePerms();
      }
    }
  }, [permModalVisible, assignPermRecord]);

  const handleEdit = (record: API.SysRoleDto) => {
    setEditingRecord(record);
    setFormModalVisible(true);
  };

  const handleDelete = (record: API.SysRoleDto) => {
    Modal.confirm({
      title: '确认删除',
      content: `确定要删除角色 "${record.roleName}" 吗？`,
      okText: '确认',
      okButtonProps: { danger: true },
      cancelText: '取消',
      onOk: async () => {
        const res = await deleteRole(record.id || '');
        if (res.success) {
          message.success('删除成功');
          actionRef.current?.reload();
        }
      },
    });
  };

  const handleAssignMenus = (record: API.SysRoleDto) => {
    setAssignMenuRecord(record);
    setMenuModalVisible(true);
  };

  const handleAssignPerms = (record: API.SysRoleDto) => {
    setAssignPermRecord(record);
    setPermModalVisible(true);
  };

  const handleMenuSubmit = async () => {
    try {
      const res = await assignRoleMenus(
        assignMenuRecord?.id || '',
        checkedMenuKeys,
      );
      if (res.success) {
        message.success('菜单分配成功');
        setMenuModalVisible(false);
        return true;
      }
      return false;
    } catch {
      message.error('分配失败');
      return false;
    }
  };

  const handlePermSubmit = async () => {
    try {
      const res = await assignRolePerms(
        assignPermRecord?.id || '',
        selectedPermKeys,
      );
      if (res.success) {
        message.success('权限分配成功');
        setPermModalVisible(false);
        return true;
      }
      return false;
    } catch {
      message.error('分配失败');
      return false;
    }
  };

  const handleSubmit = async (values: Partial<API.SysRoleDto>) => {
    try {
      let res: API.ApiResult;
      if (editingRecord) {
        res = await updateRole(editingRecord.id || '', values);
      } else {
        res = await createRole(values);
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

  const columns: ProColumns<API.SysRoleDto>[] = [
    {
      title: '角色名称',
      dataIndex: 'roleName',
      width: 150,
    },
    {
      title: '角色编码',
      dataIndex: 'roleCode',
      width: 150,
    },
    {
      title: '菜单严格模式',
      dataIndex: 'menuCheckStrictly',
      width: 120,
      search: false,
      render: (_, record) => (
        <Tag color={record.menuCheckStrictly ? 'blue' : 'default'}>
          {record.menuCheckStrictly ? '是' : '否'}
        </Tag>
      ),
    },
    {
      title: '部门严格模式',
      dataIndex: 'deptCheckStrictly',
      width: 120,
      search: false,
      render: (_, record) => (
        <Tag color={record.deptCheckStrictly ? 'blue' : 'default'}>
          {record.deptCheckStrictly ? '是' : '否'}
        </Tag>
      ),
    },
    {
      title: '操作',
      dataIndex: 'option',
      valueType: 'option',
      width: 240,
      fixed: 'right',
      render: (_, record) => (
        <Space size="small">
          <Authorized permission="system:role:edit">
            <Button
              type="link"
              size="small"
              icon={<EditOutlined />}
              onClick={() => handleEdit(record)}
            >
              编辑
            </Button>
          </Authorized>
          <Authorized permission="system:role:assign">
            <Button
              type="link"
              size="small"
              icon={<MenuOutlined />}
              onClick={() => handleAssignMenus(record)}
            >
              分配菜单
            </Button>
          </Authorized>
          <Authorized permission="system:role:assign">
            <Button
              type="link"
              size="small"
              icon={<SafetyOutlined />}
              onClick={() => handleAssignPerms(record)}
            >
              分配权限
            </Button>
          </Authorized>
          <Authorized permission="system:role:delete">
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
      <ProTable<API.SysRoleDto>
        headerTitle="角色管理"
        actionRef={actionRef}
        rowKey="id"
        search={{
          labelWidth: 100,
        }}
        toolBarRender={() => [
          <Authorized permission="system:role:add" key="add">
            <Button
              type="primary"
              icon={<PlusOutlined />}
              onClick={() => {
                setEditingRecord(undefined);
                setFormModalVisible(true);
              }}
            >
              新增角色
            </Button>
          </Authorized>,
        ]}
        request={async (params) => {
          const { current, pageSize, ...rest } = params;
          const res = await getRolePage(
            { current: current || 1, size: pageSize || 10 },
            rest as Partial<API.SysRoleDto>,
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
        title={editingRecord ? '编辑角色' : '新增角色'}
        open={formModalVisible}
        onOpenChange={setFormModalVisible}
        onFinish={handleSubmit}
        initialValues={editingRecord}
        modalProps={{
          destroyOnClose: true,
        }}
      >
        <ProFormText
          name="roleName"
          label="角色名称"
          rules={[{ required: true, message: '请输入角色名称' }]}
          placeholder="请输入角色名称"
        />
        <ProFormText
          name="roleCode"
          label="角色编码"
          rules={[{ required: true, message: '请输入角色编码' }]}
          placeholder="请输入角色编码"
          disabled={!!editingRecord}
        />
        <ProFormSwitch
          name="menuCheckStrictly"
          label="菜单严格模式"
          checkedChildren="是"
          unCheckedChildren="否"
          initialValue={true}
        />
        <ProFormSwitch
          name="deptCheckStrictly"
          label="部门严格模式"
          checkedChildren="是"
          unCheckedChildren="否"
          initialValue={true}
        />
      </ModalForm>

      <Modal
        title={`分配菜单 - ${assignMenuRecord?.roleName}`}
        open={menuModalVisible}
        onOk={handleMenuSubmit}
        onCancel={() => setMenuModalVisible(false)}
        width={500}
        destroyOnClose
      >
        <Tree
          checkable
          checkedKeys={checkedMenuKeys}
          onCheck={(keys) => setCheckedMenuKeys(keys as string[])}
          treeData={menuTree}
          defaultExpandAll
        />
      </Modal>

      <Modal
        title={`分配权限 - ${assignPermRecord?.roleName}`}
        open={permModalVisible}
        onOk={handlePermSubmit}
        onCancel={() => setPermModalVisible(false)}
        width={600}
        destroyOnClose
      >
        <Transfer
          titles={['待选权限', '已选权限']}
          dataSource={allPerms}
          targetKeys={selectedPermKeys}
          rowKey={(item) => item.key}
          showSearch
          onChange={(nextTargetKeys) =>
            setSelectedPermKeys(nextTargetKeys as string[])
          }
          render={(item) => item.title}
        />
      </Modal>
    </PageContainer>
  );
};

export default Role;
