import {
  ApartmentOutlined,
  DeleteOutlined,
  DownloadOutlined,
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
import type { FormInstance } from 'antd';
import {
  Button,
  Modal,
  message,
  Radio,
  Space,
  Tag,
  Transfer,
  Tree,
} from 'antd';
import React, { useCallback, useEffect, useRef, useState } from 'react';
import Authorized from '@/components/Authorized';
import { useDict } from '@/hooks/useDict';
import { useExportXlsx } from '@/hooks/useExportXlsx';
import { getDeptTreeSelect } from '@/services/system/dept';
import { getMenuTreeSelect } from '@/services/system/menu';
import { getPermPage } from '@/services/system/perm';
import {
  assignRoleDepts,
  assignRoleMenus,
  assignRolePerms,
  createRole,
  deleteRole,
  exportRoles,
  getRoleDepts,
  getRoleMenus,
  getRolePage,
  getRolePerms,
  removeRoleDepts,
  updateRole,
} from '@/services/system/role';
import {
  DEFAULT_PAGE_SIZE,
  fetchAllPages,
  toPageQuery,
} from '@/utils/pagination';

/** 数据权限/角色类型的展示色（仅展示层配色，枚举数据源已改由字典驱动） */
const ROLE_TYPE_COLOR: Record<string, string> = {
  '0': 'geekblue',
  '1': 'cyan',
};

const Role: React.FC = () => {
  const actionRef = useRef<ActionType | null>(null);
  const { getDictOptions, dictLabel } = useDict();
  // 表格查询表单实例，导出时直接取当前查询条件
  const formRef = useRef<FormInstance<API.SysRoleDto> | undefined>(undefined);
  const [exporting, handleExport] = useExportXlsx<API.SysRoleDto>(exportRoles);
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
  const [allPerms, setAllPerms] = useState<{ key: string; title: string }[]>(
    [],
  );
  const [selectedPermKeys, setSelectedPermKeys] = useState<string[]>([]);
  const [dataScopeModalVisible, setDataScopeModalVisible] = useState(false);
  const [assignDataScopeRecord, setAssignDataScopeRecord] = useState<
    API.SysRoleDto | undefined
  >();
  const [deptTree, setDeptTree] = useState<any[]>([]);
  const [dataScopeValue, setDataScopeValue] = useState<string>('1');
  const [checkedDeptKeys, setCheckedDeptKeys] = useState<string[]>([]);
  const [dataScopeSubmitting, setDataScopeSubmitting] = useState(false);

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
        // 全量权限列表：按 total/size 逐页拉取，避免写死 size: 1000 造成静默截断
        const records = await fetchAllPages<API.SysPermDto>((page) =>
          getPermPage(page, {}),
        );
        setAllPerms(
          records
            .filter((perm) => perm.id)
            .map((perm) => ({
              key: String(perm.id),
              title: `${perm.permName ?? '-'} (${perm.permCode ?? '-'})`,
            })),
        );
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

  useEffect(() => {
    if (dataScopeModalVisible && assignDataScopeRecord) {
      const fetchDeptScope = async () => {
        const roleId = assignDataScopeRecord?.id || '';
        // 角色当前的数据权限 + 已授权部门并行拉取
        const [treeRes, deptRes] = await Promise.all([
          getDeptTreeSelect(),
          getRoleDepts(roleId),
        ]);
        if (treeRes.success) {
          const transformDeptTree = (list: any[]): any[] =>
            (list ?? []).map((item) => ({
              key: String(item.id),
              title: item.deptName,
              children: item.children
                ? transformDeptTree(item.children)
                : undefined,
            }));
          setDeptTree(transformDeptTree(treeRes.data));
        }
        if (deptRes.success) {
          setCheckedDeptKeys(deptRes.data || []);
        }
      };
      fetchDeptScope();
    }
  }, [dataScopeModalVisible, assignDataScopeRecord]);

  /** 按当前查询条件导出角色列表（无查询条件即全量导出） */
  const handleExportRoles = useCallback(() => {
    handleExport(formRef.current?.getFieldsValue() ?? {});
  }, [handleExport]);

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

  const handleAssignDataScope = (record: API.SysRoleDto) => {
    setAssignDataScopeRecord(record);
    // 已存值优先，缺省按「仅本人」以外的最宽松值 1（全部）兜底
    setDataScopeValue(record?.dataScope || '1');
    setCheckedDeptKeys([]);
    setDataScopeModalVisible(true);
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

  /** 保存数据权限：先更新角色 dataScope，再按 scope 处理 sys_role_dept 授权 */
  const handleDataScopeSubmit = async () => {
    const roleId = assignDataScopeRecord?.id || '';
    setDataScopeSubmitting(true);
    try {
      const res = await updateRole(roleId, { dataScope: dataScopeValue });
      if (!res.success) {
        message.error('数据权限保存失败');
        return;
      }
      // 仅「自定义」需要落 sys_role_dept；其余 scope 清空关联，避免脏数据
      if (dataScopeValue === '2') {
        const deptRes = await assignRoleDepts(roleId, checkedDeptKeys);
        if (!deptRes.success) {
          message.error('部门授权失败');
          return;
        }
      } else {
        await removeRoleDepts(roleId);
      }
      message.success('数据权限保存成功');
      setDataScopeModalVisible(false);
      actionRef.current?.reload();
    } catch {
      message.error('数据权限保存失败');
    } finally {
      setDataScopeSubmitting(false);
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
      title: '角色类型',
      dataIndex: 'roleType',
      width: 110,
      valueType: 'select',
      fieldProps: {
        options: getDictOptions('sys_role_type'),
      },
      render: (_, record) => (
        <>
          <Tag color={ROLE_TYPE_COLOR[record.roleType ?? ''] ?? 'default'}>
            {dictLabel('sys_role_type', record.roleType)}
          </Tag>
          {record.isBuiltin === '1' && <Tag color="gold">内置</Tag>}
        </>
      ),
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
      width: 340,
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
          <Authorized permission="system:role:edit">
            <Button
              type="link"
              size="small"
              icon={<MenuOutlined />}
              onClick={() => handleAssignMenus(record)}
            >
              分配菜单
            </Button>
          </Authorized>
          <Authorized permission="system:role:edit">
            <Button
              type="link"
              size="small"
              icon={<SafetyOutlined />}
              onClick={() => handleAssignPerms(record)}
            >
              分配权限
            </Button>
          </Authorized>
          <Authorized permission="system:role:edit">
            <Button
              type="link"
              size="small"
              icon={<ApartmentOutlined />}
              onClick={() => handleAssignDataScope(record)}
            >
              数据权限
            </Button>
          </Authorized>
          <Authorized permission="system:role:remove">
            <Button
              type="link"
              size="small"
              danger
              icon={<DeleteOutlined />}
              disabled={record.isBuiltin === '1'}
              title={
                record.isBuiltin === '1' ? '内置角色不允许删除' : undefined
              }
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
        formRef={formRef}
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
          <Authorized permission="system:role:export" key="export">
            <Button
              icon={<DownloadOutlined />}
              loading={exporting}
              onClick={handleExportRoles}
            >
              导出
            </Button>
          </Authorized>,
        ]}
        request={async (params) => {
          const { current, pageSize } = params;
          const res = await getRolePage(
            { current: current || 1, size: pageSize || DEFAULT_PAGE_SIZE },
            toPageQuery<API.SysRoleDto>(params),
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
        title={editingRecord ? '编辑角色' : '新增角色'}
        open={formModalVisible}
        onOpenChange={setFormModalVisible}
        onFinish={handleSubmit}
        initialValues={editingRecord}
        modalProps={{
          destroyOnHidden: true,
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
        destroyOnHidden
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
        title={`数据权限 - ${assignDataScopeRecord?.roleName}`}
        open={dataScopeModalVisible}
        onOk={handleDataScopeSubmit}
        onCancel={() => setDataScopeModalVisible(false)}
        confirmLoading={dataScopeSubmitting}
        width={520}
        destroyOnHidden
      >
        <div style={{ marginBottom: 16 }}>数据范围</div>
        <Radio.Group
          value={dataScopeValue}
          onChange={(e) => setDataScopeValue(e.target.value)}
        >
          <Space orientation="vertical">
            {getDictOptions('sys_data_scope').map((item) => (
              <Radio key={item.value} value={item.value}>
                {item.label}
              </Radio>
            ))}
          </Space>
        </Radio.Group>
        {dataScopeValue === '2' && (
          <div style={{ marginTop: 16 }}>
            <div style={{ marginBottom: 8 }}>选择部门</div>
            <Tree
              checkable
              checkedKeys={checkedDeptKeys}
              onCheck={(keys) => setCheckedDeptKeys(keys as string[])}
              treeData={deptTree}
              defaultExpandAll
            />
          </div>
        )}
      </Modal>

      <Modal
        title={`分配权限 - ${assignPermRecord?.roleName}`}
        open={permModalVisible}
        onOk={handlePermSubmit}
        onCancel={() => setPermModalVisible(false)}
        width={600}
        destroyOnHidden
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
