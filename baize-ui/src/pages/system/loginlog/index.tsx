import {
  ClearOutlined,
  DeleteOutlined,
  DownloadOutlined,
} from '@ant-design/icons';
import type { ActionType, ProColumns } from '@ant-design/pro-components';
import { PageContainer, ProTable } from '@ant-design/pro-components';
import { Button, Modal, message, Space, Tag } from 'antd';
import type { FormInstance } from 'antd';
import React, { useCallback, useRef } from 'react';
import Authorized from '@/components/Authorized';
import { useDict } from '@/hooks/useDict';
import { useExportXlsx } from '@/hooks/useExportXlsx';
import {
  cleanLoginLog,
  deleteLoginLog,
  exportLoginLogs,
  getLoginLogPage,
} from '@/services/system/loginlog';
import { DEFAULT_PAGE_SIZE, toPageQuery } from '@/utils/pagination';

/**
 * 登录日志管理页面
 */
const LoginLog: React.FC = () => {
  const actionRef = useRef<ActionType | null>(null);
  const { toValueEnum } = useDict();
  // 表格查询表单实例，导出时直接取当前查询条件
  const formRef = useRef<FormInstance<API.SysLoginLogDto> | undefined>(
    undefined,
  );
  const [exporting, handleExport] =
    useExportXlsx<API.SysLoginLogDto>(exportLoginLogs);

  /** 按当前查询条件导出登录日志（无查询条件即全量导出） */
  const handleExportLoginLogs = useCallback(() => {
    handleExport(formRef.current?.getFieldsValue() ?? {});
  }, [handleExport]);

  const handleDelete = (record: API.SysLoginLogDto) => {
    Modal.confirm({
      title: '确认删除',
      content: `确定要删除用户 "${record.userName}" 的登录日志吗？`,
      okText: '确认',
      okButtonProps: { danger: true },
      cancelText: '取消',
      onOk: async () => {
        const res = await deleteLoginLog(record.id!);
        if (res.success) {
          message.success('删除成功');
          actionRef.current?.reload();
        }
      },
    });
  };

  const handleClean = () => {
    Modal.confirm({
      title: '确认清空',
      content: '确定要清空所有登录日志吗？此操作不可恢复！',
      okText: '确认',
      okButtonProps: { danger: true },
      cancelText: '取消',
      onOk: async () => {
        const res = await cleanLoginLog();
        if (res.success) {
          message.success('清空成功');
          actionRef.current?.reload();
        }
      },
    });
  };

  const columns: ProColumns<API.SysLoginLogDto>[] = [
    {
      title: '日志编号',
      dataIndex: 'id',
      width: 100,
      search: false,
    },
    {
      title: '用户名',
      dataIndex: 'userName',
      width: 120,
    },
    {
      title: 'IP 地址',
      dataIndex: 'ipaddr',
      width: 140,
    },
    {
      title: '登录地点',
      dataIndex: 'loginLocation',
      width: 150,
      search: false,
    },
    {
      title: '浏览器',
      dataIndex: 'browser',
      width: 120,
      search: false,
    },
    {
      title: '操作系统',
      dataIndex: 'os',
      width: 140,
      search: false,
    },
    {
      title: '登录状态',
      dataIndex: 'status',
      width: 100,
      valueType: 'select',
      valueEnum: toValueEnum('sys_oper_status', {
        '0': 'Success',
        '1': 'Error',
      }),
      render: (_, record) => (
        <Tag color={record.status === '0' ? 'success' : 'error'}>
          {record.status === '0' ? '成功' : '失败'}
        </Tag>
      ),
    },
    {
      title: '提示消息',
      dataIndex: 'msg',
      width: 180,
      search: false,
      ellipsis: true,
    },
    {
      title: '登录时间',
      dataIndex: 'loginTime',
      valueType: 'dateTime',
      width: 180,
      search: false,
    },
    {
      title: '操作',
      dataIndex: 'option',
      valueType: 'option',
      width: 100,
      fixed: 'right',
      render: (_, record) => (
        <Space size="small">
          <Authorized permission="monitor:logininfor:remove">
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
      <ProTable<API.SysLoginLogDto>
        headerTitle="登录日志"
        actionRef={actionRef}
        formRef={formRef}
        rowKey="id"
        search={{
          labelWidth: 100,
        }}
        toolBarRender={() => [
          <Authorized permission="monitor:logininfor:remove" key="clean">
            <Button danger icon={<ClearOutlined />} onClick={handleClean}>
              清空日志
            </Button>
          </Authorized>,
          <Authorized permission="monitor:logininfor:export" key="export">
            <Button
              icon={<DownloadOutlined />}
              loading={exporting}
              onClick={handleExportLoginLogs}
            >
              导出
            </Button>
          </Authorized>,
        ]}
        request={async (params) => {
          const { current, pageSize } = params;
          const res = await getLoginLogPage(
            { current: current || 1, size: pageSize || DEFAULT_PAGE_SIZE },
            toPageQuery<API.SysLoginLogDto>(params),
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
    </PageContainer>
  );
};

export default LoginLog;
