import { ClearOutlined, DeleteOutlined, EyeOutlined } from '@ant-design/icons';
import type { ActionType, ProColumns } from '@ant-design/pro-components';
import { PageContainer, ProTable } from '@ant-design/pro-components';
import { Button, Descriptions, Drawer, Modal, message, Space, Tag } from 'antd';
import React, { useRef, useState } from 'react';
import Authorized from '@/components/Authorized';
import {
  cleanOperLog,
  deleteOperLog,
  getOperLogPage,
} from '@/services/system/operlog';

/**
 * 操作日志管理页面
 */
const OperLog: React.FC = () => {
  const actionRef = useRef<ActionType | null>(null);
  const [detailVisible, setDetailVisible] = useState(false);
  const [detailRecord, setDetailRecord] = useState<API.SysOperLogDto | undefined>();

  const handleDelete = (record: API.SysOperLogDto) => {
    Modal.confirm({
      title: '确认删除',
      content: `确定要删除操作日志 "${record.title}" 吗？`,
      okText: '确认',
      cancelText: '取消',
      onOk: async () => {
        const res = await deleteOperLog(record.id!);
        if (res.success) {
          message.success('删除成功');
          actionRef.current?.reload();
        }
      },
    });
  };

  const handleViewDetail = (record: API.SysOperLogDto) => {
    setDetailRecord(record);
    setDetailVisible(true);
  };

  const handleClean = () => {
    Modal.confirm({
      title: '确认清空',
      content: '确定要清空所有操作日志吗？此操作不可恢复！',
      okText: '确认',
      okButtonProps: { danger: true },
      cancelText: '取消',
      onOk: async () => {
        const res = await cleanOperLog();
        if (res.success) {
          message.success('清空成功');
          actionRef.current?.reload();
        }
      },
    });
  };

  const businessTypeMap: Record<string, { text: string; color: string }> = {
    '1': { text: '新增', color: 'blue' },
    '2': { text: '修改', color: 'green' },
    '3': { text: '删除', color: 'red' },
    '4': { text: '授权', color: 'orange' },
    '5': { text: '导出', color: 'purple' },
    '6': { text: '导入', color: 'cyan' },
    '7': { text: '强退', color: 'magenta' },
    '8': { text: '生成代码', color: 'geekblue' },
    '9': { text: '清空数据', color: 'volcano' },
  };

  const columns: ProColumns<API.SysOperLogDto>[] = [
    {
      title: '日志编号',
      dataIndex: 'id',
      width: 100,
      search: false,
    },
    {
      title: '系统模块',
      dataIndex: 'title',
      width: 150,
    },
    {
      title: '操作类型',
      dataIndex: 'businessType',
      width: 100,
      valueType: 'select',
      valueEnum: {
        '1': '新增',
        '2': '修改',
        '3': '删除',
        '4': '授权',
        '5': '导出',
        '6': '导入',
        '7': '强退',
        '8': '生成代码',
        '9': '清空数据',
      },
      render: (_, record) => {
        const info = businessTypeMap[record.businessType || '1'];
        return <Tag color={info.color}>{info.text}</Tag>;
      },
    },
    {
      title: '请求方式',
      dataIndex: 'requestMethod',
      width: 100,
      search: false,
      render: (_, record) => {
        const colorMap: Record<string, string> = {
          GET: 'blue',
          POST: 'green',
          PUT: 'orange',
          DELETE: 'red',
        };
        return (
          <Tag color={colorMap[record.requestMethod || '']}>
            {record.requestMethod}
          </Tag>
        );
      },
    },
    {
      title: '操作人员',
      dataIndex: 'userName',
      width: 120,
    },
    {
      title: '操作IP',
      dataIndex: 'operIp',
      width: 140,
      search: false,
    },
    {
      title: '操作地点',
      dataIndex: 'operLocation',
      width: 140,
      search: false,
    },
    {
      title: '操作状态',
      dataIndex: 'status',
      width: 100,
      valueType: 'select',
      valueEnum: {
        '0': '成功',
        '1': '失败',
      },
      render: (_, record) => (
        <Tag color={Number(record.status) === 0 ? 'success' : 'error'}>
          {Number(record.status) === 0 ? '成功' : '失败'}
        </Tag>
      ),
    },
    {
      title: '操作时间',
      dataIndex: 'operTime',
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
          <Button
            type="link"
            size="small"
            icon={<EyeOutlined />}
            onClick={() => handleViewDetail(record)}
          >
            查看
          </Button>
          <Authorized permission="system:operlog:delete">
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
      <ProTable<API.SysOperLogDto>
        headerTitle="操作日志"
        actionRef={actionRef}
        rowKey="id"
        search={{
          labelWidth: 100,
        }}
        toolBarRender={() => [
          <Authorized permission="system:operlog:clean" key="clean">
            <Button danger icon={<ClearOutlined />} onClick={handleClean}>
              清空日志
            </Button>
          </Authorized>,
        ]}
        request={async (params) => {
          const { current, pageSize, ...rest } = params;
          const res = await getOperLogPage(
            { current: current || 1, size: pageSize || 10 },
            rest as API.SysOperLogDto,
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

      <Drawer
        title="日志详情"
        width={720}
        open={detailVisible}
        onClose={() => setDetailVisible(false)}
      >
        {detailRecord && (
          <Descriptions column={1} bordered>
            <Descriptions.Item label="日志编号">{detailRecord.id}</Descriptions.Item>
            <Descriptions.Item label="系统模块">{detailRecord.title}</Descriptions.Item>
            <Descriptions.Item label="请求方式">
              <Tag color={
                {
                  GET: 'blue',
                  POST: 'green',
                  PUT: 'orange',
                  DELETE: 'red',
                }[detailRecord.requestMethod || '']
              }>
                {detailRecord.requestMethod}
              </Tag>
            </Descriptions.Item>
            <Descriptions.Item label="请求URL">{detailRecord.operUrl}</Descriptions.Item>
            <Descriptions.Item label="操作人员">{detailRecord.userName}</Descriptions.Item>
            <Descriptions.Item label="操作IP">{detailRecord.operIp}</Descriptions.Item>
            <Descriptions.Item label="操作地点">{detailRecord.operLocation}</Descriptions.Item>
            <Descriptions.Item label="操作状态">
              <Tag color={Number(detailRecord.status) === 0 ? 'success' : 'error'}>
                {Number(detailRecord.status) === 0 ? '成功' : '失败'}
              </Tag>
            </Descriptions.Item>
            <Descriptions.Item label="异常信息">
              {detailRecord.errorMsg || '无'}
            </Descriptions.Item>
            <Descriptions.Item label="耗时">{detailRecord.costTime}ms</Descriptions.Item>
            <Descriptions.Item label="操作时间">{detailRecord.operTime}</Descriptions.Item>
            <Descriptions.Item label="请求参数">
              <pre style={{ margin: 0, whiteSpace: 'pre-wrap', wordBreak: 'break-all' }}>
                {detailRecord.operParam || '无'}
              </pre>
            </Descriptions.Item>
            <Descriptions.Item label="返回结果">
              <pre style={{ margin: 0, whiteSpace: 'pre-wrap', wordBreak: 'break-all' }}>
                {detailRecord.jsonResult || '无'}
              </pre>
            </Descriptions.Item>
          </Descriptions>
        )}
      </Drawer>
    </PageContainer>
  );
};

export default OperLog;
