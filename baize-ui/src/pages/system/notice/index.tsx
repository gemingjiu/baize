import {
  DeleteOutlined,
  EditOutlined,
  EyeOutlined,
  PlusOutlined,
} from '@ant-design/icons';
import type { ActionType, ProColumns } from '@ant-design/pro-components';
import {
  ModalForm,
  PageContainer,
  ProFormSelect,
  ProFormText,
  ProFormTextArea,
  ProTable,
} from '@ant-design/pro-components';
import { Button, Drawer, Modal, message, Space, Tag } from 'antd';
import React, { useRef, useState } from 'react';
import Authorized from '@/components/Authorized';
import { useDict } from '@/hooks/useDict';
import {
  createNotice,
  deleteNotice,
  getNoticePage,
  updateNotice,
} from '@/services/system/notice';
import { DEFAULT_PAGE_SIZE, toPageQuery } from '@/utils/pagination';

/**
 * 通知公告管理页面
 */
const Notice: React.FC = () => {
  const actionRef = useRef<ActionType | null>(null);
  const { toValueEnum, getDictOptions } = useDict();
  const [formModalVisible, setFormModalVisible] = useState(false);
  const [detailVisible, setDetailVisible] = useState(false);
  const [editingRecord, setEditingRecord] = useState<
    API.SysNoticeDto | undefined
  >();
  const [detailRecord, setDetailRecord] = useState<
    API.SysNoticeDto | undefined
  >();

  const handleEdit = (record: API.SysNoticeDto) => {
    setEditingRecord(record);
    setFormModalVisible(true);
  };

  const handleDelete = (record: API.SysNoticeDto) => {
    Modal.confirm({
      title: '确认删除',
      content: `确定要删除公告 "${record.noticeTitle}" 吗？`,
      okText: '确认',
      okButtonProps: { danger: true },
      cancelText: '取消',
      onOk: async () => {
        const res = await deleteNotice(record.id!);
        if (res.success) {
          message.success('删除成功');
          actionRef.current?.reload();
        }
      },
    });
  };

  const handleViewDetail = (record: API.SysNoticeDto) => {
    setDetailRecord(record);
    setDetailVisible(true);
  };

  const handleSubmit = async (values: Partial<API.SysNoticeDto>) => {
    try {
      let res: API.ApiResult;
      if (editingRecord) {
        res = await updateNotice(editingRecord.id!, values);
      } else {
        res = await createNotice(values);
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

  const columns: ProColumns<API.SysNoticeDto>[] = [
    {
      title: '公告标题',
      dataIndex: 'noticeTitle',
      width: 200,
    },
    {
      title: '公告类型',
      dataIndex: 'noticeType',
      width: 100,
      valueType: 'select',
      valueEnum: toValueEnum('sys_notice_type'),
      render: (_, record) => (
        <Tag color={record.noticeType === '1' ? 'blue' : 'green'}>
          {record.noticeType === '1' ? '通知' : '公告'}
        </Tag>
      ),
    },
    {
      title: '状态',
      dataIndex: 'status',
      width: 100,
      valueType: 'select',
      valueEnum: toValueEnum('sys_normal_disable', {
        '0': 'Success',
        '1': 'Error',
      }),
      render: (_, record) => (
        <Tag color={record.status === '0' ? 'success' : 'error'}>
          {record.status === '0' ? '正常' : '停用'}
        </Tag>
      ),
    },
    {
      title: '创建时间',
      dataIndex: 'createdTime',
      valueType: 'dateTime',
      width: 180,
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
          <Button
            type="link"
            size="small"
            icon={<EyeOutlined />}
            onClick={() => handleViewDetail(record)}
          >
            查看
          </Button>
          <Authorized permission="system:notice:edit">
            <Button
              type="link"
              size="small"
              icon={<EditOutlined />}
              onClick={() => handleEdit(record)}
            >
              编辑
            </Button>
          </Authorized>
          <Authorized permission="system:notice:remove">
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
      <ProTable<API.SysNoticeDto>
        headerTitle="通知公告管理"
        actionRef={actionRef}
        rowKey="id"
        search={{
          labelWidth: 100,
        }}
        toolBarRender={() => [
          <Authorized permission="system:notice:add" key="add">
            <Button
              type="primary"
              icon={<PlusOutlined />}
              onClick={() => {
                setEditingRecord(undefined);
                setFormModalVisible(true);
              }}
            >
              新增公告
            </Button>
          </Authorized>,
        ]}
        request={async (params) => {
          const { current, pageSize } = params;
          const res = await getNoticePage(
            { current: current || 1, size: pageSize || DEFAULT_PAGE_SIZE },
            toPageQuery<API.SysNoticeDto>(params),
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
        title={editingRecord ? '编辑公告' : '新增公告'}
        open={formModalVisible}
        onOpenChange={setFormModalVisible}
        onFinish={handleSubmit}
        initialValues={editingRecord}
        modalProps={{
          destroyOnHidden: true,
        }}
        width={600}
      >
        <ProFormText
          name="noticeTitle"
          label="公告标题"
          rules={[{ required: true, message: '请输入公告标题' }]}
          placeholder="请输入公告标题"
        />
        <ProFormSelect
          name="noticeType"
          label="公告类型"
          options={getDictOptions('sys_notice_type')}
          initialValue="1"
          rules={[{ required: true, message: '请选择公告类型' }]}
        />
        <ProFormSelect
          name="status"
          label="状态"
          options={getDictOptions('sys_normal_disable')}
          initialValue="0"
        />
        <ProFormTextArea
          name="noticeContent"
          label="公告内容"
          rules={[{ required: true, message: '请输入公告内容' }]}
          placeholder="请输入公告内容"
          fieldProps={{ rows: 6 }}
        />
        <ProFormTextArea name="remark" label="备注" placeholder="请输入备注" />
      </ModalForm>

      <Drawer
        title="公告详情"
        width={600}
        open={detailVisible}
        onClose={() => setDetailVisible(false)}
      >
        {detailRecord && (
          <div>
            <h2>{detailRecord.noticeTitle}</h2>
            <Space style={{ marginBottom: 16 }}>
              <Tag color={detailRecord.noticeType === '1' ? 'blue' : 'green'}>
                {detailRecord.noticeType === '1' ? '通知' : '公告'}
              </Tag>
              <Tag color={detailRecord.status === '0' ? 'success' : 'error'}>
                {detailRecord.status === '0' ? '正常' : '停用'}
              </Tag>
              <span style={{ color: '#999' }}>{detailRecord.createdTime}</span>
            </Space>
            <div
              style={{
                padding: 16,
                background: '#fafafa',
                borderRadius: 8,
                minHeight: 200,
                whiteSpace: 'pre-wrap',
                wordBreak: 'break-all',
              }}
            >
              {detailRecord.noticeContent || '暂无内容'}
            </div>
          </div>
        )}
      </Drawer>
    </PageContainer>
  );
};

export default Notice;
