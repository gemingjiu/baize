import {
  DeleteOutlined,
  EditOutlined,
  PlusCircleOutlined,
  PlusOutlined,
} from '@ant-design/icons';
import type { ActionType, ProColumns } from '@ant-design/pro-components';
import {
  ModalForm,
  PageContainer,
  ProFormDigit,
  ProFormSelect,
  ProFormSwitch,
  ProFormText,
  ProFormTextArea,
  ProFormTreeSelect,
  ProTable,
} from '@ant-design/pro-components';
import { Button, Modal, message, Space, Tag } from 'antd';
import React, { useEffect, useRef, useState } from 'react';
import Authorized from '@/components/Authorized';
import {
  createMenu,
  deleteMenu,
  getMenuTree,
  getMenuTreeSelect,
  updateMenu,
} from '@/services/system/menu';
import { transformMenuTree } from '@/utils/tree';

/**
 * 菜单类型映射
 */
const MenuTypeTag: React.FC<{ type?: string }> = ({ type }) => {
  const typeMap: Record<string, { text: string; color: string }> = {
    '0': { text: '目录', color: 'blue' },
    '1': { text: '菜单', color: 'green' },
    '2': { text: '按钮', color: 'orange' },
  };
  const info = typeMap[type || '1'];
  return <Tag color={info.color}>{info.text}</Tag>;
};

/**
 * 菜单管理页面
 */
const Menu: React.FC = () => {
  const actionRef = useRef<ActionType | null>(null);
  const [formModalVisible, setFormModalVisible] = useState(false);
  const [editingRecord, setEditingRecord] = useState<
    API.SysMenuDto | undefined
  >();
  const [menuTree, setMenuTree] = useState<any[]>([]);
  const [parentId, setParentId] = useState<string | undefined>();
  const [menuType, setMenuType] = useState<string>('1');

  // 获取菜单树选择数据
  useEffect(() => {
    const fetchMenuTreeSelect = async () => {
      const res = await getMenuTreeSelect();
      if (res.success) {
        setMenuTree([
          { label: '主类目', value: undefined },
          ...transformMenuTree(res.data),
        ]);
      }
    };
    fetchMenuTreeSelect();
  }, [formModalVisible]);

  // 监听菜单类型变化
  useEffect(() => {
    if (editingRecord?.menuType) {
      setMenuType(editingRecord.menuType);
    } else if (!formModalVisible) {
      setMenuType('1');
    }
  }, [editingRecord, formModalVisible]);

  /**
   * 编辑菜单
   */
  const handleEdit = (record: API.SysMenuDto) => {
    setEditingRecord(record);
    setFormModalVisible(true);
  };

  /**
   * 新增子菜单
   */
  const handleAddChild = (record: API.SysMenuDto) => {
    // 如果当前是按钮，则不能新增子菜单
    if (record.menuType === '2') {
      message.warning('按钮类型不能新增子菜单');
      return;
    }
    setParentId(record.id);
    setEditingRecord(undefined);
    setFormModalVisible(true);
  };

  /**
   * 删除菜单
   */
  const handleDelete = (record: API.SysMenuDto) => {
    if (record.children && record.children.length > 0) {
      message.warning('该菜单下有子菜单，无法删除');
      return;
    }
    Modal.confirm({
      title: '确认删除',
      content: `确定要删除菜单 "${record.menuName}" 吗？`,
      okText: '确认',
      okButtonProps: { danger: true },
      cancelText: '取消',
      onOk: async () => {
        const res = await deleteMenu(record.id!);
        if (res.success) {
          message.success('删除成功');
          actionRef.current?.reload();
        }
      },
    });
  };

  /**
   * 提交菜单表单
   */
  const handleSubmit = async (values: any) => {
    try {
      const submitData: Partial<API.SysMenuDto> = {
        ...values,
        parentId: values.parentId || parentId,
        visible: values.visible ? '0' : '1',
      };
      let res: API.ApiResult;
      if (editingRecord) {
        res = await updateMenu(editingRecord.id!, submitData);
      } else {
        res = await createMenu(submitData);
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
    } catch (error) {
      message.error('操作失败');
      return false;
    }
  };

  const columns: ProColumns<API.SysMenuDto>[] = [
    {
      title: '菜单名称',
      dataIndex: 'menuName',
      width: 200,
    },
    {
      title: '图标',
      dataIndex: 'icon',
      width: 100,
      search: false,
    },
    {
      title: '类型',
      dataIndex: 'menuType',
      width: 100,
      valueType: 'select',
      valueEnum: {
        '0': '目录',
        '1': '菜单',
        '2': '按钮',
      },
      render: (_, record) => <MenuTypeTag type={record.menuType} />,
    },
    {
      title: '路由路径',
      dataIndex: 'path',
      width: 200,
      search: false,
    },
    {
      title: '组件路径',
      dataIndex: 'component',
      width: 200,
      search: false,
    },
    {
      title: '权限标识',
      dataIndex: 'perms',
      width: 180,
      search: false,
    },
    {
      title: '外部链接',
      dataIndex: 'external',
      width: 100,
      search: false,
      render: (_, record) => (
        <Tag color={record.external ? 'blue' : 'default'}>
          {record.external ? '是' : '否'}
        </Tag>
      ),
    },
    {
      title: '操作',
      dataIndex: 'option',
      valueType: 'option',
      width: 200,
      fixed: 'right',
      render: (_, record) => (
        <Space size="small">
          <Authorized permission="system:menu:add">
            <Button
              type="link"
              size="small"
              icon={<PlusCircleOutlined />}
              onClick={() => handleAddChild(record)}
            >
              新增
            </Button>
          </Authorized>
          <Authorized permission="system:menu:edit">
            <Button
              type="link"
              size="small"
              icon={<EditOutlined />}
              onClick={() => handleEdit(record)}
            >
              编辑
            </Button>
          </Authorized>
          <Authorized permission="system:menu:delete">
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
      <ProTable<API.SysMenuDto>
        headerTitle="菜单管理"
        actionRef={actionRef}
        rowKey="id"
        search={{
          labelWidth: 100,
        }}
        toolBarRender={() => [
          <Authorized permission="system:menu:add" key="add">
            <Button
              type="primary"
              icon={<PlusOutlined />}
              onClick={() => {
                setParentId(undefined);
                setEditingRecord(undefined);
                setFormModalVisible(true);
              }}
            >
              新增菜单
            </Button>
          </Authorized>,
        ]}
        request={async (params) => {
          const res = await getMenuTree({
            menuName: params.menuName || params.keyword,
          });
          if (res.success) {
            return {
              data: res.data,
              success: true,
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

      {/* 菜单表单弹窗 */}
      <ModalForm
        title={editingRecord ? '编辑菜单' : '新增菜单'}
        open={formModalVisible}
        onOpenChange={setFormModalVisible}
        onFinish={handleSubmit}
        initialValues={
          editingRecord
            ? {
                ...editingRecord,
                visible: editingRecord.visible === '0',
              }
            : undefined
        }
        modalProps={{
          destroyOnClose: true,
        }}
        layout="horizontal"
        labelCol={{ span: 6 }}
        wrapperCol={{ span: 16 }}
      >
        <ProFormTreeSelect
          name="parentId"
          label="上级菜单"
          placeholder="请选择上级菜单"
          fieldProps={{
            treeDefaultExpandAll: true,
            treeData: menuTree,
          }}
          initialValue={parentId}
        />
        <ProFormText
          name="menuName"
          label="菜单名称"
          rules={[{ required: true, message: '请输入菜单名称' }]}
          placeholder="请输入菜单名称"
        />
        <ProFormSelect
          name="menuType"
          label="菜单类型"
          valueEnum={{
            '0': '目录',
            '1': '菜单',
            '2': '按钮',
          }}
          initialValue="1"
          rules={[{ required: true, message: '请选择菜单类型' }]}
          placeholder="请选择菜单类型"
          fieldProps={{
            onChange: (value: string) => setMenuType(value),
          }}
        />
        <ProFormDigit
          name="sort"
          label="显示排序"
          min={0}
          rules={[{ required: true, message: '请输入显示排序' }]}
          placeholder="请输入显示排序"
        />
        {menuType !== '2' && (
          <ProFormText name="icon" label="图标" placeholder="请输入图标名称" />
        )}
        {menuType === '1' && (
          <>
            <ProFormText
              name="path"
              label="路由路径"
              placeholder="请输入路由路径"
            />
            <ProFormText
              name="component"
              label="组件路径"
              placeholder="请输入组件路径"
            />
          </>
        )}
        <ProFormText
          name="perms"
          label="权限标识"
          placeholder="请输入权限标识，如：system:user:list"
        />
        {menuType === '1' && (
          <ProFormSwitch
            name="cacheable"
            label="是否缓存"
            checkedChildren="是"
            unCheckedChildren="否"
            initialValue={false}
          />
        )}
        <ProFormSwitch
          name="external"
          label="是否外部链接"
          checkedChildren="是"
          unCheckedChildren="否"
          initialValue={false}
        />
        <ProFormSwitch
          name="visible"
          label="显示状态"
          checkedChildren="显示"
          unCheckedChildren="隐藏"
          initialValue={true}
        />
        {menuType === '1' && (
          <ProFormText
            name="parameters"
            label="路由参数"
            placeholder="请输入路由参数"
          />
        )}
      </ModalForm>
    </PageContainer>
  );
};

export default Menu;
