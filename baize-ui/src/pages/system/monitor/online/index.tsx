import { DeleteOutlined, ReloadOutlined } from '@ant-design/icons';
import type { ActionType, ProColumns } from '@ant-design/pro-components';
import { PageContainer, ProTable } from '@ant-design/pro-components';
import { Button, Modal, Space, Tag, message } from 'antd';
import React, { useRef, useState } from 'react';
import Authorized from '@/components/Authorized';
import { getOnlineCount, getOnlineUsers, kickoutUser } from '@/services/system/online';
import { getUserById } from '@/services/system/user';

/**
 * 在线用户管理页面。
 *
 * 数据契约说明：后端 GET /system/online/list 只返回用户 ID 数组（List<String>），
 * 不含用户名/昵称等可读字段。因此这里先把 userId 列表拉回来，再逐个回查
 * GET /system/user/{id} 才能展示表格。
 */
type OnlineRow = {
  userId: string;
  userName?: string;
  nickName?: string;
  deptId?: string;
};

const Online: React.FC = () => {
  const actionRef = useRef<ActionType | null>(null);
  const [count, setCount] = useState<number | undefined>();

  const fetchCount = async () => {
    const res = await getOnlineCount();
    if (res.success) {
      setCount(res.data);
    }
  };

  const loadRows = async (): Promise<OnlineRow[]> => {
    const res = await getOnlineUsers();
    if (!res.success || !Array.isArray(res.data)) {
      return [];
    }
    // 逐个回查用户信息；单个用户查询失败不影响整行列表渲染
    const rows = await Promise.all(
      res.data.map(async (userId) => {
        const row: OnlineRow = { userId };
        try {
          const detail = await getUserById(userId);
          if (detail.success && detail.data) {
            row.userName = detail.data.userName;
            row.nickName = detail.data.nickName;
            row.deptId = detail.data.deptId;
          }
        } catch {
          // 用户可能已被删除，保留 userId 即可
        }
        return row;
      }),
    );
    return rows;
  };

  const handleKickout = (record: OnlineRow) => {
    Modal.confirm({
      title: '确认强制下线',
      content: `确定要强制用户 "${record.userName || record.userId}" 下线吗？`,
      okText: '确认',
      okButtonProps: { danger: true },
      cancelText: '取消',
      onOk: async () => {
        const res = await kickoutUser(record.userId);
        if (res.success) {
          message.success('已强制下线');
          actionRef.current?.reload();
          fetchCount();
        }
      },
    });
  };

  const columns: ProColumns<OnlineRow>[] = [
    {
      title: '用户ID',
      dataIndex: 'userId',
      width: 120,
    },
    {
      title: '用户名',
      dataIndex: 'userName',
      width: 140,
      render: (_, record) => record.userName || '-',
    },
    {
      title: '昵称',
      dataIndex: 'nickName',
      width: 140,
      render: (_, record) => record.nickName || '-',
    },
    {
      title: '部门ID',
      dataIndex: 'deptId',
      width: 120,
      search: false,
      render: (_, record) => record.deptId || '-',
    },
    {
      title: '状态',
      dataIndex: 'status',
      width: 100,
      search: false,
      render: () => <Tag color="success">在线</Tag>,
    },
    {
      title: '操作',
      dataIndex: 'option',
      valueType: 'option',
      width: 120,
      fixed: 'right',
      render: (_, record) => (
        <Space size="small">
          <Authorized permission="monitor:online:forceLogout">
            <Button
              type="link"
              size="small"
              danger
              icon={<DeleteOutlined />}
              onClick={() => handleKickout(record)}
            >
              强制下线
            </Button>
          </Authorized>
        </Space>
      ),
    },
  ];

  return (
    <PageContainer>
      <ProTable<OnlineRow>
        headerTitle="在线用户"
        actionRef={actionRef}
        rowKey="userId"
        search={false}
        toolBarRender={() => [
          <span key="count" style={{ marginRight: 'auto', lineHeight: '32px' }}>
            当前在线：{count ?? '-'} 人
          </span>,
          <Button
            key="refresh"
            icon={<ReloadOutlined />}
            onClick={() => {
              actionRef.current?.reload();
              fetchCount();
            }}
          >
            刷新
          </Button>,
        ]}
        request={async () => {
          await fetchCount();
          const rows = await loadRows();
          return {
            data: rows,
            success: true,
            total: rows.length,
          };
        }}
        columns={columns}
        pagination={false}
      />
    </PageContainer>
  );
};

export default Online;
