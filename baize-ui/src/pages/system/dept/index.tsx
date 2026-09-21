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
  ProFormText,
  ProFormTextArea,
  ProFormTreeSelect,
  ProTable,
} from '@ant-design/pro-components';
import { Button, Modal, message, Space, Tag } from 'antd';
import React, { useEffect, useRef, useState } from 'react';
import Authorized from '@/components/Authorized';
import {
  createDept,
  deleteDept,
  getDeptTree,
  getDeptTreeSelect,
  updateDept,
} from '@/services/system/dept';
import { transformDeptTree } from '@/utils/tree';

/**
 * 部门管理页面
 */
const Dept: React.FC = () => {
  const actionRef = useRef<ActionType | null>(null);
  const [formModalVisible, setFormModalVisible] = useState(false);
  const [editingRecord, setEditingRecord] = useState<
    API.SysDeptDto | undefined
  >();
  const [deptTree, setDeptTree] = useState<any[]>([]);
  const [parentId, setParentId] = useState<string | undefined>();

  // 获取部门树选择数据
  useEffect(() => {
    const fetchDeptTreeSelect = async () => {
      const res = await getDeptTreeSelect();
      if (res.success) {
        setDeptTree([
          { label: '主类目', value: undefined },
          ...transformDeptTree(res.data),
        ]);
      }
    };
    fetchDeptTreeSelect();
  }, [formModalVisible]);

  /**
   * 编辑部门
   */
  const handleEdit = (record: API.SysDeptDto) => {
    setEditingRecord(record);
    setFormModalVisible(true);
  };

  /**
   * 新增子部门
   */
  const handleAddChild = (record: API.SysDeptDto) => {
    setParentId(record.id);
    setEditingRecord(undefined);
    setFormModalVisible(true);
  };

  /**
   * 删除部门
   */
  const handleDelete = (record: API.SysDeptDto) => {
    if (record.children && record.children.length > 0) {
      message.warning('该部门下有子部门，无法删除');
      return;
    }
    Modal.confirm({
      title: '确认删除',
      content: `确定要删除部门 "${record.deptName}" 吗？`,
      okText: '确认',
      okButtonProps: { danger: true },
      cancelText: '取消',
      onOk: async () => {
        const res = await deleteDept(record.id!);
        if (res.success) {
          message.success('删除成功');
          actionRef.current?.reload();
        }
      },
    });
  };

  /**
   * 提交部门表单
   */
  const handleSubmit = async (values: any) => {
    try {
      const submitData = {
        ...values,
        parentId: values.parentId || parentId,
      };
      let res: API.ApiResult;
      if (editingRecord) {
        res = await updateDept(editingRecord.id!, submitData);
      } else {
        res = await createDept(submitData);
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

  const columns: ProColumns<API.SysDeptDto>[] = [
    {
      title: '部门名称',
      dataIndex: 'deptName',
      width: 250,
    },
    {
      title: '排序',
      dataIndex: 'orderNum',
      width: 100,
      search: false,
    },
    {
      title: '负责人',
      dataIndex: 'leader',
      width: 120,
      search: false,
    },
    {
      title: '联系电话',
      dataIndex: 'phone',
      width: 150,
      search: false,
    },
    {
      title: '邮箱',
      dataIndex: 'email',
      width: 200,
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
          <Authorized permission="system:dept:add">
            <Button
              type="link"
              size="small"
              icon={<PlusCircleOutlined />}
              onClick={() => handleAddChild(record)}
            >
              新增
            </Button>
          </Authorized>
          <Authorized permission="system:dept:edit">
            <Button
              type="link"
              size="small"
              icon={<EditOutlined />}
              onClick={() => handleEdit(record)}
            >
              编辑
            </Button>
          </Authorized>
          <Authorized permission="system:dept:delete">
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
      <ProTable<API.SysDeptDto>
        headerTitle="部门管理"
        actionRef={actionRef}
        rowKey="id"
        search={{
          labelWidth: 100,
        }}
        toolBarRender={() => [
          <Authorized permission="system:dept:add" key="add">
            <Button
              type="primary"
              icon={<PlusOutlined />}
              onClick={() => {
                setParentId(undefined);
                setEditingRecord(undefined);
                setFormModalVisible(true);
              }}
            >
              新增部门
            </Button>
          </Authorized>,
        ]}
        request={async (params) => {
          const res = await getDeptTree({
            deptName: params.deptName || params.keyword,
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

      {/* 部门表单弹窗 */}
      <ModalForm
        title={editingRecord ? '编辑部门' : '新增部门'}
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
          label="上级部门"
          placeholder="请选择上级部门"
          fieldProps={{
            treeDefaultExpandAll: true,
            treeData: deptTree,
          }}
          initialValue={parentId}
        />
        <ProFormText
          name="deptName"
          label="部门名称"
          rules={[{ required: true, message: '请输入部门名称' }]}
          placeholder="请输入部门名称"
        />
        <ProFormText name="leader" label="负责人" placeholder="请输入负责人" />
        <ProFormText
          name="phone"
          label="联系电话"
          placeholder="请输入联系电话"
        />
        <ProFormText name="email" label="邮箱" placeholder="请输入邮箱" />
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
        <ProFormTextArea
          name="remark"
          label="备注"
          rows={3}
          placeholder="请输入备注"
        />
      </ModalForm>
    </PageContainer>
  );
};

export default Dept;
