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
import { useDict } from '@/hooks/useDict';
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
import { DEFAULT_PAGE_SIZE, toPageQuery } from '@/utils/pagination';

/**
 * 字典管理页面
 */
const Dict: React.FC = () => {
  const actionRef = useRef<ActionType | null>(null);
  const dataActionRef = useRef<ActionType | null>(null);
  const { toValueEnum, getDictOptions, reload: reloadDicts } = useDict();
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
          await reloadDicts();
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
        await reloadDicts();
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
          await reloadDicts();
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
        await reloadDicts();
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
      render: (_, record) => (
        <>
          {record.dictName}
          {record.isBuiltin === '1' && <Tag color="gold">内置</Tag>}
        </>
      ),
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
      valueEnum: toValueEnum('sys_normal_disable', {
        '0': 'Success',
        '1': 'Error',
      }),
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
          <Authorized permission="system:dict:remove">
            <Button
              type="link"
              size="small"
              danger
              icon={<DeleteOutlined />}
              disabled={record.isBuiltin === '1'}
              title={record.isBuiltin === '1' ? '内置字典类型不允许删除，请使用停用' : undefined}
              onClick={() => handleDelete(record)}
            >
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
      render: (_, record) => (
        <>
          {record.dictLabel}
          {record.isBuiltin === '1' && <Tag color="gold">内置</Tag>}
        </>
      ),
    },
    {
      title: '字典键值',
      dataIndex: 'dictValue',
      width: 140,
    },
    {
      title: '是否默认',
      dataIndex: 'defaulted',
      width: 100,
      valueEnum: toValueEnum('sys_defaulted'),
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
      valueEnum: toValueEnum('sys_normal_disable', {
        '0': 'Success',
        '1': 'Error',
      }),
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
          <Authorized permission="system:dict:remove">
            <Button
              type="link"
              size="small"
              danger
              icon={<DeleteOutlined />}
              disabled={record.isBuiltin === '1'}
              title={record.isBuiltin === '1' ? '内置字典项不允许删除，请使用停用' : undefined}
              onClick={() => handleDataDelete(record)}
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
          const { current, pageSize } = params;
          const res = await getDictTypePage(
            { current: current || 1, size: pageSize || DEFAULT_PAGE_SIZE },
            toPageQuery<API.SysDictTypeDto>(params),
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
        title={editingRecord ? '编辑字典' : '新增字典'}
        open={formModalVisible}
        onOpenChange={setFormModalVisible}
        onFinish={handleSubmit}
        initialValues={editingRecord}
        modalProps={{
          destroyOnHidden: true,
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
          options={getDictOptions('sys_normal_disable')}
        />
        <ProFormDigit name="sort" label="排序" initialValue={0} min={0} />
        <ProFormTextArea name="remark" label="备注" placeholder="请输入备注" />
      </ModalForm>

      <Drawer
        title={`字典数据 - ${currentType?.dictName || ''} (${currentType?.dictType || ''})`}
        width={860}
        open={dataVisible}
        onClose={() => setDataVisible(false)}
        destroyOnHidden
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
              { current: current || 1, size: pageSize || DEFAULT_PAGE_SIZE },
              { dictType: currentType?.dictType },
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
            destroyOnHidden: true,
          }}
        >
          <ProFormText name="dictLabel" label="字典标签" rules={[{ required: true, message: '请输入字典标签' }]} />
          <ProFormText name="dictValue" label="字典键值" rules={[{ required: true, message: '请输入字典键值' }]} />
          <ProFormSelect
            name="defaulted"
            label="是否默认"
            initialValue="0"
            options={getDictOptions('sys_defaulted')}
          />
          <ProFormSelect
            name="status"
            label="状态"
            initialValue="0"
            options={getDictOptions('sys_normal_disable')}
          />
          <ProFormDigit name="sort" label="排序" initialValue={0} min={0} />
          <ProFormTextArea name="remark" label="备注" />
        </ModalForm>
      </Drawer>
    </PageContainer>
  );
};

export default Dict;
