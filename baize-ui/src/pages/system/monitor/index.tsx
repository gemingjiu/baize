import {
  CloudServerOutlined,
  DashboardOutlined,
  DatabaseOutlined,
  HddOutlined,
  ReloadOutlined,
} from '@ant-design/icons';
import { PageContainer } from '@ant-design/pro-components';
import {
  Button,
  Card,
  Col,
  Descriptions,
  Empty,
  Progress,
  Result,
  Row,
  Space,
  Spin,
  Statistic,
  Tag,
} from 'antd';
import { useAccess, history } from '@umijs/max';
import React, { useCallback, useEffect, useState } from 'react';
import { getServerInfo } from '@/services/system/monitor';

/** 页面权限码，与后端 @RequiresPermission 保持一致 */
const PERM_SERVER_QUERY = 'monitor:server:query';

/**
 * 服务监控页面
 */
const Monitor: React.FC = () => {
  const access = useAccess();
  const [loading, setLoading] = useState(false);
  const [serverInfo, setServerInfo] = useState<API.ServerInfoVO | undefined>();

  /**
   * 与 src/components/Authorized 保持一致的防御写法：
   * useAccess() 在 AccessProvider 之外会拿到 null，此时放行（后端才是真正的鉴权方）。
   */
  const canQuery =
    typeof access?.canPerm === 'function' ? access.canPerm(PERM_SERVER_QUERY) : true;

  const fetchData = useCallback(async () => {
    setLoading(true);
    try {
      const res = await getServerInfo();
      if (res.success) {
        setServerInfo(res.data);
      }
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    if (!canQuery) {
      return undefined;
    }
    fetchData();
    const timer = setInterval(fetchData, 30000); // 每30秒刷新
    return () => clearInterval(timer);
  }, [fetchData, canQuery]);

  const formatUptime = (seconds?: number) => {
    if (!seconds) return '-';
    const days = Math.floor(seconds / 86400);
    const hours = Math.floor((seconds % 86400) / 3600);
    const mins = Math.floor((seconds % 3600) / 60);
    const parts: string[] = [];
    if (days > 0) parts.push(`${days}天`);
    if (hours > 0) parts.push(`${hours}小时`);
    if (mins > 0) parts.push(`${mins}分钟`);
    return parts.join(' ') || '不足1分钟';
  };

  const getProgressColor = (usage: number) => {
    if (usage < 50) return '#52c41a';
    if (usage < 80) return '#faad14';
    return '#f5222d';
  };

  /** -1 视为未知，不参与进度条渲染 */
  const isUsageKnown = (usage?: number) => usage !== undefined && usage >= 0;

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

  const cpuUsage = serverInfo?.cpu?.usage;
  const disks = serverInfo?.disks ?? [];

  return (
    <PageContainer>
      <Spin spinning={loading}>
        <Row gutter={[16, 16]}>
          {/* CPU 信息 */}
          <Col xs={24} lg={12}>
            <Card
              title={
                <Space>
                  <DashboardOutlined />
                  CPU 信息
                </Space>
              }
            >
              <Row gutter={16}>
                <Col span={12}>
                  {isUsageKnown(cpuUsage) ? (
                    <Statistic
                      title="CPU 使用率"
                      value={cpuUsage}
                      suffix="%"
                      precision={1}
                    />
                  ) : (
                    <Statistic title="CPU 使用率" value="未知" />
                  )}
                </Col>
                <Col span={12}>
                  <Statistic
                    title="核心数"
                    value={serverInfo?.cpu?.physicalCount || 0}
                    suffix="核"
                  />
                </Col>
              </Row>
              {isUsageKnown(cpuUsage) ? (
                <Progress
                  percent={cpuUsage}
                  strokeColor={getProgressColor(cpuUsage ?? 0)}
                  style={{ marginTop: 16 }}
                />
              ) : (
                <Progress
                  percent={0}
                  showInfo={false}
                  strokeColor="#d9d9d9"
                  style={{ marginTop: 16 }}
                />
              )}
              <Descriptions column={1} size="small" style={{ marginTop: 16 }}>
                <Descriptions.Item label="架构">{serverInfo?.cpu?.name || '-'}</Descriptions.Item>
                <Descriptions.Item label="逻辑核心数">
                  {serverInfo?.cpu?.logicalCount || 0}
                </Descriptions.Item>
                <Descriptions.Item label="系统负载">
                  {serverInfo?.cpu?.loadAverage !== undefined &&
                  serverInfo?.cpu?.loadAverage >= 0
                    ? serverInfo?.cpu?.loadAverage
                    : '未知'}
                </Descriptions.Item>
              </Descriptions>
            </Card>
          </Col>

          {/* 内存信息 */}
          <Col xs={24} lg={12}>
            <Card
              title={
                <Space>
                  <DatabaseOutlined />
                  内存信息
                </Space>
              }
            >
              <Row gutter={16}>
                <Col span={8}>
                  <Statistic
                    title="总内存"
                    value={serverInfo?.mem?.total || 0}
                    suffix="MB"
                  />
                </Col>
                <Col span={8}>
                  <Statistic
                    title="已使用"
                    value={serverInfo?.mem?.used || 0}
                    suffix="MB"
                    styles={{ content: { color: '#cf1322' } }}
                  />
                </Col>
                <Col span={8}>
                  <Statistic
                    title="剩余"
                    value={serverInfo?.mem?.free || 0}
                    suffix="MB"
                    styles={{ content: { color: '#3f8600' } }}
                  />
                </Col>
              </Row>
              <Progress
                percent={serverInfo?.mem?.usage || 0}
                strokeColor={getProgressColor(serverInfo?.mem?.usage || 0)}
                style={{ marginTop: 16 }}
              />
            </Card>
          </Col>

          {/* JVM 信息 */}
          <Col xs={24} lg={12}>
            <Card
              title={
                <Space>
                  <CloudServerOutlined />
                  JVM 信息
                </Space>
              }
            >
              <Row gutter={16}>
                <Col span={8}>
                  <Statistic
                    title="JVM 总内存"
                    value={serverInfo?.jvm?.total || 0}
                    suffix="MB"
                  />
                </Col>
                <Col span={8}>
                  <Statistic
                    title="已使用"
                    value={serverInfo?.jvm?.used || 0}
                    suffix="MB"
                    styles={{ content: { color: '#cf1322' } }}
                  />
                </Col>
                <Col span={8}>
                  <Statistic
                    title="最大内存"
                    value={serverInfo?.jvm?.max || 0}
                    suffix="MB"
                  />
                </Col>
              </Row>
              <Progress
                percent={serverInfo?.jvm?.usage || 0}
                strokeColor={getProgressColor(serverInfo?.jvm?.usage || 0)}
                style={{ marginTop: 16 }}
              />
              <Descriptions column={1} size="small" style={{ marginTop: 16 }}>
                <Descriptions.Item label="JVM 名称">{serverInfo?.jvm?.name || '-'}</Descriptions.Item>
                <Descriptions.Item label="Java 版本">
                  <Tag color="blue">{serverInfo?.jvm?.version || '-'}</Tag>
                </Descriptions.Item>
                <Descriptions.Item label="启动时间">{serverInfo?.jvm?.startTime || '-'}</Descriptions.Item>
                <Descriptions.Item label="运行时长">{formatUptime(serverInfo?.jvm?.uptime)}</Descriptions.Item>
              </Descriptions>
            </Card>
          </Col>

          {/* 磁盘信息（首个分区） */}
          <Col xs={24} lg={12}>
            <Card
              title={
                <Space>
                  <HddOutlined />
                  磁盘信息
                  {disks.length > 1 ? <Tag color="blue">{disks.length} 个分区</Tag> : null}
                </Space>
              }
            >
              {disks.length === 0 ? (
                <Empty description="未获取到磁盘信息" image={Empty.PRESENTED_IMAGE_SIMPLE} />
              ) : (
                <>
                  <Row gutter={16}>
                    <Col span={8}>
                      <Statistic
                        title="总容量"
                        value={serverInfo?.disk?.total || 0}
                        suffix="GB"
                      />
                    </Col>
                    <Col span={8}>
                      <Statistic
                        title="已使用"
                        value={serverInfo?.disk?.used || 0}
                        suffix="GB"
                        styles={{ content: { color: '#cf1322' } }}
                      />
                    </Col>
                    <Col span={8}>
                      <Statistic
                        title="剩余"
                        value={serverInfo?.disk?.free || 0}
                        suffix="GB"
                        styles={{ content: { color: '#3f8600' } }}
                      />
                    </Col>
                  </Row>
                  <Progress
                    percent={serverInfo?.disk?.usage || 0}
                    strokeColor={getProgressColor(serverInfo?.disk?.usage || 0)}
                    style={{ marginTop: 16 }}
                  />
                  <Descriptions column={1} size="small" style={{ marginTop: 16 }}>
                    <Descriptions.Item label="分区">
                      {serverInfo?.disk?.name || serverInfo?.disk?.path || '-'}
                    </Descriptions.Item>
                  </Descriptions>
                </>
              )}
            </Card>
          </Col>

          {/* 全部分区列表（多分区） */}
          <Col span={24}>
            <Card
              title="磁盘分区明细"
              extra={
                <a onClick={fetchData}>
                  <ReloadOutlined /> 刷新
                </a>
              }
            >
              {disks.length === 0 ? (
                <Empty description="未获取到磁盘分区" image={Empty.PRESENTED_IMAGE_SIMPLE} />
              ) : (
                <Row gutter={[16, 16]}>
                  {disks.map((item, index) => (
                    <Col xs={24} sm={12} lg={8} key={item.path || item.name || index}>
                      <Card size="small" title={item.name || item.path || `分区 ${index + 1}`}>
                        <Row gutter={8}>
                          <Col span={8}>
                            <Statistic
                              title="总容量"
                              value={item.total || 0}
                              suffix="GB"
                              styles={{ content: { fontSize: 16 } }}
                            />
                          </Col>
                          <Col span={8}>
                            <Statistic
                              title="已用"
                              value={item.used || 0}
                              suffix="GB"
                              styles={{ content: { fontSize: 16, color: '#cf1322' } }}
                            />
                          </Col>
                          <Col span={8}>
                            <Statistic
                              title="剩余"
                              value={item.free || 0}
                              suffix="GB"
                              styles={{ content: { fontSize: 16, color: '#3f8600' } }}
                            />
                          </Col>
                        </Row>
                        <Progress
                          percent={item.usage || 0}
                          strokeColor={getProgressColor(item.usage || 0)}
                          style={{ marginTop: 12 }}
                        />
                      </Card>
                    </Col>
                  ))}
                </Row>
              )}
            </Card>
          </Col>

          {/* 操作系统信息 */}
          <Col span={24}>
            <Card
              title="操作系统信息"
              extra={
                <a onClick={fetchData}>
                  <ReloadOutlined /> 刷新
                </a>
              }
            >
              <Descriptions column={{ xs: 1, sm: 2, md: 3 }}>
                <Descriptions.Item label="操作系统名称">
                  {serverInfo?.os?.name || '-'}
                </Descriptions.Item>
                <Descriptions.Item label="系统架构">
                  {serverInfo?.os?.arch || '-'}
                </Descriptions.Item>
                <Descriptions.Item label="系统版本">
                  {serverInfo?.os?.version || '-'}
                </Descriptions.Item>
              </Descriptions>
            </Card>
          </Col>
        </Row>
      </Spin>
    </PageContainer>
  );
};

export default Monitor;
