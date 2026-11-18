import { DatabaseOutlined, ReloadOutlined } from '@ant-design/icons';
import type { ActionType, ProColumns } from '@ant-design/pro-components';
import { PageContainer, ProTable } from '@ant-design/pro-components';
import { history, useAccess } from '@umijs/max';
import { Alert, Button, Card, Col, Result, Row, Statistic, Tag } from 'antd';
import React, { useCallback, useEffect, useRef, useState } from 'react';
import { getCacheInfo, getCacheKeys } from '@/services/system/monitor';

/** 页面权限码，与后端 @RequiresPermission 保持一致 */
const PERM_CACHE_QUERY = 'monitor:cache:query';

/** 后端用 -1 表示「取不到值」，页面据此显示「未知」；>= 0 才当数值渲染 */
const isRateKnown = (value?: number) => value !== undefined && value >= 0;

type CacheKeyRow = {
  key?: string;
  type?: string;
  ttl?: number;
  ttlHuman?: string;
};

/** Redis 类型 → 标签配色 */
const TYPE_COLOR_MAP: Record<string, string> = {
  string: 'blue',
  list: 'green',
  set: 'cyan',
  zset: 'purple',
  hash: 'orange',
  stream: 'magenta',
};

const Cache: React.FC = () => {
  const access = useAccess();
  const actionRef = useRef<ActionType | null>(null);
  const [info, setInfo] = useState<API.CacheInfoVO | undefined>();
  const [infoLoading, setInfoLoading] = useState(false);

  /**
   * 与 src/components/Authorized 保持一致的防御写法：
   * useAccess() 在 AccessProvider 之外会拿到 null，此时放行（后端才是真正的鉴权方）。
   */
  const canQuery =
    typeof access?.canPerm === 'function'
      ? access.canPerm(PERM_CACHE_QUERY)
      : true;

  const fetchInfo = useCallback(async () => {
    setInfoLoading(true);
    try {
      const res = await getCacheInfo();
      if (res.success) {
        setInfo(res.data);
      }
    } finally {
      setInfoLoading(false);
    }
  }, []);

  useEffect(() => {
    if (canQuery) {
      fetchInfo();
    }
  }, [canQuery, fetchInfo]);

  const columns: ProColumns<CacheKeyRow>[] = [
    {
      title: 'Key',
      dataIndex: 'key',
      ellipsis: true,
      copyable: true,
    },
    {
      title: '类型',
      dataIndex: 'type',
      width: 100,
      search: false,
      render: (_, record) => (
        <Tag color={TYPE_COLOR_MAP[record.type || ''] || 'default'}>
          {record.type || 'unknown'}
        </Tag>
      ),
    },
    {
      title: 'TTL（秒）',
      dataIndex: 'ttl',
      width: 120,
      search: false,
      render: (_, record) => {
        if (record.ttl === undefined) return '-';
        if (record.ttl === -1) return <Tag color="blue">永不过期</Tag>;
        if (record.ttl === -2) return <Tag color="red">已过期</Tag>;
        return record.ttl;
      },
    },
    {
      title: '剩余时间',
      dataIndex: 'ttlHuman',
      width: 140,
      search: false,
      render: (_, record) => record.ttlHuman || '-',
    },
  ];

  if (!canQuery) {
    return (
      <PageContainer>
        <Result
          status="403"
          title="403"
          subTitle="抱歉，您没有访问该页面的权限。"
          extra={
            <Button type="primary" onClick={() => history.push('/')}>
              返回首页
            </Button>
          }
        />
      </PageContainer>
    );
  }

  const hitRate = info?.hitRate;
  const hitRateKnown = isRateKnown(hitRate);
  const prefixStats = info?.prefixStats ?? [];

  return (
    <PageContainer>
      {info?.unavailable ? (
        <Alert
          type="warning"
          showIcon
          title="Redis 当前不可用"
          description={
            info.errorMessage || '无法读取 Redis INFO，以下统计可能不准确。'
          }
          style={{ marginBottom: 16 }}
        />
      ) : null}

      <Card loading={infoLoading}>
        <Row gutter={[16, 16]}>
          <Col xs={12} sm={8} lg={4}>
            <Statistic
              title="Redis 版本"
              value={info?.version || '-'}
              prefix={<DatabaseOutlined />}
            />
          </Col>
          <Col xs={12} sm={8} lg={4}>
            <Statistic title="运行模式" value={info?.mode || '-'} />
          </Col>
          <Col xs={12} sm={8} lg={4}>
            <Statistic
              title="连接客户端"
              value={info?.connectedClients ?? 0}
              suffix="个"
            />
          </Col>
          <Col xs={12} sm={8} lg={4}>
            <Statistic
              title="已用内存"
              value={info?.usedMemoryHuman || '-'}
              styles={{ content: { color: '#cf1322' } }}
            />
          </Col>
          <Col xs={12} sm={8} lg={4}>
            <Statistic
              title="内存峰值"
              value={info?.usedMemoryPeakHuman || '-'}
            />
          </Col>
          <Col xs={12} sm={8} lg={4}>
            <Statistic
              title="Key 总数"
              value={info?.dbSize ?? 0}
              suffix={
                info?.maxMemoryPolicy
                  ? `（${info.maxMemoryPolicy}）`
                  : undefined
              }
            />
          </Col>
        </Row>
      </Card>

      <Card style={{ marginTop: 16 }} loading={infoLoading}>
        <Row gutter={[16, 16]}>
          <Col xs={24} sm={12} lg={6}>
            {hitRateKnown ? (
              <Statistic
                title="命中率"
                value={hitRate}
                suffix="%"
                precision={1}
                styles={{ content: { color: '#3f8600' } }}
              />
            ) : (
              <Statistic title="命中率" value="未知" />
            )}
          </Col>
          <Col xs={12} sm={6} lg={4}>
            <Statistic title="命中次数" value={info?.keyspaceHits ?? 0} />
          </Col>
          <Col xs={12} sm={6} lg={4}>
            <Statistic
              title="未命中次数"
              value={info?.keyspaceMisses ?? 0}
              styles={{ content: { color: '#cf1322' } }}
            />
          </Col>
          <Col xs={12} sm={6} lg={4}>
            <Statistic
              title="每秒操作"
              value={info?.instantaneousOpsPerSec ?? 0}
            />
          </Col>
          <Col xs={12} sm={6} lg={6}>
            <Statistic
              title="运行时长"
              value={info?.uptimeHuman || '-'}
              styles={{ content: { fontSize: 20 } }}
            />
          </Col>
        </Row>
      </Card>

      {prefixStats.length > 0 ? (
        <Card
          title="Key 分布（按前缀）"
          style={{ marginTop: 16 }}
          loading={infoLoading}
        >
          <Row gutter={[8, 8]}>
            {prefixStats.map((item, index) => (
              <Col key={item.prefix || index}>
                <Tag color={item.count ? 'blue' : 'default'}>
                  {item.prefix} · {item.count ?? 0}
                </Tag>
              </Col>
            ))}
          </Row>
        </Card>
      ) : null}

      <ProTable<CacheKeyRow>
        headerTitle="缓存 Key 列表"
        actionRef={actionRef}
        rowKey="key"
        style={{ marginTop: 16 }}
        search={{ labelWidth: 'auto' }}
        toolBarRender={() => [
          <Button
            key="refresh"
            icon={<ReloadOutlined />}
            onClick={() => {
              actionRef.current?.reload();
              fetchInfo();
            }}
          >
            刷新
          </Button>,
        ]}
        columns={columns}
        request={async (params) => {
          const res = await getCacheKeys({ pattern: params.key || '*' });
          const rows: CacheKeyRow[] =
            res.success && Array.isArray(res.data?.keys)
              ? (res.data?.keys ?? [])
              : [];
          return {
            data: rows,
            success: true,
            total: rows.length,
          };
        }}
        pagination={false}
      />
    </PageContainer>
  );
};

export default Cache;
