import { DeleteOutlined, EditOutlined, PlusOutlined } from '@ant-design/icons';
import type { ActionType, ProColumns } from '@ant-design/pro-components';
import {
  DrawerForm,
  ModalForm,
  PageContainer,
  ProFormDigit,
  ProFormSelect,
  ProFormText,
  ProFormTextArea,
  ProTable,
} from '@ant-design/pro-components';
import { Button, Drawer, Modal, Space, Switch, Tag, message } from 'antd';
import React, { useRef, useState } from 'react';
import Authorized from '@/components/Authorized';
import {
  createDictData,
  createDictType,
  deleteDictData,
  deleteDictType,
  getDictDataPage,
  getDictTypePage,
  updateDictData,
  updateDictType,
} from '@/services/system/dict';

/**
 * 字典管理页面
 */
const Dict: React.FC = () => {
  const actionRef = useRef<ActionType | null>(null);
  const dataActionRef = useRef<ActionType | null>(null);
  const [formModalVisible, setFormModalVisible] = useState(false);
  const [editingRecord, setEditingRecord] = useState<API.SysDictTypeDto | undefined>();
  const [dataVisible, setDataVisible] = useState(false);
  const [currentType, setCurrentType] = useState<API.SysDictTypeDto | undefined>();
  const [dataModalVisible, setDataModalVisible] = useState(false);
  const [editingData, setEditingData] = useState<API.SysDictDataDto | undefined>();

  const handleEdit = (record: API.SysDictTypeDto) => {
    setEditingRecord(record);
    setFormModalVisible(true);
  };

  const handleDelete = (record: API.SysDictTypeDto) => {
    Modal.confirm({
      title: '确认删除',
      content: `确定要删除字典类型 "${record.dictName}" 吗？删除后该类型下所有字典数据将不可用！`,
      okText: '确认',
      okButtonProps: { danger: true },
      cancelText: '取消',
      onOk: async () => {
        const res = await deleteDictType(record.id || '');
        if (res.success) {
          message.success('删除成功');
          actionRef.current?.reload();
        }
      },
    });
  };

  const handleSubmit = async (values: Partial<API.SysDictTypeDto>) => {
    try {
      let res: API.ApiResult;
      if (editingRecord) {
        res = await updateDictType(editingRecord.id || '', values);
      } else {
        res = await createDictType(values);
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

  const handleDataEdit = (record: API.SysDictDataDto) => {
    setEditingData(record);
    setDataModalVisible(true);
  };

  const handleDataDelete = (record: API.SysDictDataDto) => {
    Modal.confirm({
      title: '确认删除',
      content: `确定要删除字典数据 "${record.dictLabel}" 吗？`,
      okText: '确认',
      okButtonProps: { danger: true },
      cancelText: '取消',
      onOk: async () => {
        const res = await deleteDictData(record.id || '');
        if (res.success) {
          message.success('删除成功');
          dataActionRef.current?.reload();
        }
      },
    });
  };

  const handleDataSubmit = async (values: Partial<API.SysDictDataDto>) => {
    try {
      const payload = { ...values, dictType: currentType?.dictType };
      let res: API.ApiResult;
      if (editingData) {
        res = await updateDictData(editingData.id || '', payload);
      } else {
        res = await createDictData(payload);
      }
      if (res.success) {
        message.success(editingData ? '更新成功' : '创建成功');
        setDataModalVisible(false);
        setEditingData(undefined);
        dataActionRef.current?.reload();
        return true;
      }
      return false;
    } catch {
      message.error('操作失败');
      return false;
    }
  };

  const columns: ProColumns<API.SysDictTypeDto>[] = [
    {
      title: '字典名称',
      dataIndex: 'dictName',
      width: 160,
    },
    {
      title: '字典类型',
      dataIndex: 'dictType',
      width: 180,
    },
    {
      title: '状态',
      dataIndex: 'status',
      width: 90,
      valueType: 'select',
      valueEnum: {
        '0': { text: '正常', status: 'Success' },
        '1': { text: '停用', status: 'Error' },
      },
      render: (_, record) =>
        record.status === '0' ? <Tag color="success">正常</Tag> : <Tag color="error">停用</Tag>,
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
      width: 200,
      fixed: 'right',
      render: (_, record) => (
        <Space>
          <Button type="link" size="small" onClick={() => {
            setCurrentType(record);
            setDataVisible(true);
          }}>
            字典数据
          </Button>
          <Authorized permission="system:dict:edit">
            <Button type="link" size="small" icon={<EditOutlined />} onClick={() => handleEdit(record)}>
              编辑
            </Button>
          </Authorized>
          <Authorized permission="system:dict:delete">
            <Button type="link" size="small" danger icon={<DeleteOutlined />} onClick={() => handleDelete(record)}>
              删除
            </Button>
          </Authorized>
        </Space>
      ),
    },
  ];

  const dataColumns: ProColumns<API.SysDictDataDto>[] = [
    {
      title: '字典标签',
      dataIndex: 'dictLabel',
      width: 140,
    },
    {
      title: '字典键值',
      dataIndex: 'dictValue',
      width: 140,
    },
    {
      title: '回显样式',
      dataIndex: 'listClass',
      width: 100,
      valueType: 'select',
      valueEnum: {
        default: { text: '默认' },
        primary: { text: '主色', status: 'Processing' },
        success: { text: '成功', status: 'Success' },
        info: { text: '信息' },
        warning: { text: '警告', status: 'Warning' },
        danger: { text: '危险', status: 'Error' },
      },
      render: (_, record) =>
        record.listClass && record.listClass !== 'default' ? (
          <Tag color={record.listClass}>{record.dictLabel}</Tag>
        ) : (
          record.dictLabel
        ),
    },
    {
      title: '是否默认',
      dataIndex: 'defaulted',
      width: 100,
      valueEnum: {
        '0': '否',
        '1': '是',
      },
      render: (_, record) => (record.defaulted === '1' ? <Tag color="blue">是</Tag> : '否'),
    },
    {
      title: '排序',
      dataIndex: 'sort',
      width: 70,
      search: false,
    },
    {
      title: '状态',
      dataIndex: 'status',
      width: 90,
      valueType: 'select',
      valueEnum: {
        '0': { text: '正常', status: 'Success' },
        '1': { text: '停用', status: 'Error' },
      },
      render: (_, record) =>
        record.status === '0' ? <Tag color="success">正常</Tag> : <Tag color="error">停用</Tag>,
    },
    {
      title: '操作',
      dataIndex: 'option',
      valueType: 'option',
      width: 140,
      fixed: 'right',
      render: (_, record) => (
        <Space>
          <Authorized permission="system:dict:edit">
            <Button type="link" size="small" icon={<EditOutlined />} onClick={() => handleDataEdit(record)}>
              编辑
            </Button>
          </Authorized>
          <Authorized permission="system:dict:delete">
            <Button type="link" size="small" danger icon={<DeleteOutlined />} onClick={() => handleDataDelete(record)}>
              删除
            </Button>
          </Authorized>
        </Space>
      ),
    },
  ];

  return (
    <PageContainer>
      <ProTable<API.SysDictTypeDto>
        headerTitle="字典管理"
        actionRef={actionRef}
        rowKey="id"
        search={{
          labelWidth: 100,
        }}
        toolBarRender={() => [
          <Authorized permission="system:dict:add" key="add">
            <Button
              type="primary"
              icon={<PlusOutlined />}
              onClick={() => {
                setEditingRecord(undefined);
                setFormModalVisible(true);
              }}
            >
              新增字典
            </Button>
          </Authorized>,
        ]}
        request={async (params) => {
          const { current, pageSize, ...rest } = params;
          const res = await getDictTypePage(
            { current: current || 1, size: pageSize || 10 },
            rest as API.SysDictTypeDto,
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
        title={editingRecord ? '编辑字典' : '新增字典'}
        open={formModalVisible}
        onOpenChange={setFormModalVisible}
        onFinish={handleSubmit}
        initialValues={editingRecord}
        modalProps={{
          destroyOnClose: true,
        }}
      >
        <ProFormText
          name="dictName"
          label="字典名称"
          rules={[{ required: true, message: '请输入字典名称' }]}
          placeholder="请输入字典名称"
        />
        <ProFormText
          name="dictType"
          label="字典类型"
          rules={[{ required: true, message: '请输入字典类型' }]}
          placeholder="请输入字典类型（如 sys_user_sex）"
          disabled={!!editingRecord}
        />
        <ProFormSelect
          name="status"
          label="状态"
          initialValue="0"
          options={[
            { label: '正常', value: '0' },
            { label: '停用', value: '1' },
          ]}
        />
        <ProFormDigit name="sort" label="排序" initialValue={0} min={0} />
        <ProFormTextArea name="remark" label="备注" placeholder="请输入备注" />
      </ModalForm>

      <Drawer
        title={`字典数据 - ${currentType?.dictName || ''} (${currentType?.dictType || ''})`}
        width={860}
        open={dataVisible}
        onClose={() => setDataVisible(false)}
        destroyOnClose
      >
        <ProTable<API.SysDictDataDto>
          headerTitle="字典数据列表"
          actionRef={dataActionRef}
          rowKey="id"
          search={false}
          toolBarRender={() => [
            <Authorized permission="system:dict:add" key="add">
              <Button
                type="primary"
                icon={<PlusOutlined />}
                onClick={() => {
                  setEditingData(undefined);
                  setDataModalVisible(true);
                }}
              >
                新增字典数据
              </Button>
            </Authorized>,
          ]}
          request={async (params) => {
            const { current, pageSize } = params;
            const res = await getDictDataPage(
              { current: current || 1, size: pageSize || 10 },
              { dictType: currentType?.dictType },
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
          columns={dataColumns}
          pagination={{
            pageSize: 10,
          }}
        />
        <ModalForm
          title={editingData ? '编辑字典数据' : '新增字典数据'}
          open={dataModalVisible}
          onOpenChange={setDataModalVisible}
          onFinish={handleDataSubmit}
          initialValues={editingData}
          modalProps={{
            destroyOnClose: true,
          }}
        >
          <ProFormText name="dictLabel" label="字典标签" rules={[{ required: true, message: '请输入字典标签' }]} />
          <ProFormText name="dictValue" label="字典键值" rules={[{ required: true, message: '请输入字典键值' }]} />
          <ProFormSelect
            name="listClass"
            label="回显样式"
            initialValue="default"
            options={[
              { label: '默认', value: 'default' },
              { label: '主色', value: 'primary' },
              { label: '成功', value: 'success' },
              { label: '信息', value: 'info' },
              { label: '警告', value: 'warning' },
              { label: '危险', value: 'danger' },
            ]}
          />
          <ProFormSelect
            name="defaulted"
            label="是否默认"
            initialValue="0"
            options={[
              { label: '否', value: '0' },
              { label: '是', value: '1' },
            ]}
          />
          <ProFormSelect
            name="status"
            label="状态"
            initialValue="0"
            options={[
              { label: '正常', value: '0' },
              { label: '停用', value: '1' },
            ]}
          />
          <ProFormDigit name="sort" label="排序" initialValue={0} min={0} />
          <ProFormTextArea name="remark" label="备注" />
        </ModalForm>
      </Drawer>
    </PageContainer>
  );
};

export default Dict;
