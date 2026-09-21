import {
  AreaChartOutlined,
  MenuOutlined,
  SafetyOutlined,
  TeamOutlined,
} from '@ant-design/icons';
import { PageContainer } from '@ant-design/pro-components';
import { useModel } from '@umijs/max';
import { Card, Col, Row, Statistic, theme } from 'antd';
import React from 'react';

const StatisticCard: React.FC<{
  title: string;
  value: number | string;
  icon: React.ReactNode;
  color: string;
}> = ({ title, value, icon, color }) => {
  const { token } = theme.useToken();
  return (
    <Card
      bordered={false}
      style={{
        borderRadius: 8,
      }}
      styles={{
        body: {
          padding: '20px 24px',
        },
      }}
    >
      <Row align="middle" gutter={16}>
        <Col>
          <div
            style={{
              width: 56,
              height: 56,
              borderRadius: 8,
              backgroundColor: color,
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              fontSize: 24,
              color: '#fff',
            }}
          >
            {icon}
          </div>
        </Col>
        <Col flex="auto">
          <div
            style={{
              fontSize: 14,
              color: token.colorTextSecondary,
              marginBottom: 4,
            }}
          >
            {title}
          </div>
          <div
            style={{
              fontSize: 28,
              fontWeight: 600,
              color: token.colorTextHeading,
            }}
          >
            {value}
          </div>
        </Col>
      </Row>
    </Card>
  );
};

const Welcome: React.FC = () => {
  const { token } = theme.useToken();
  const { initialState } = useModel('@@initialState');

  return (
    <PageContainer>
      <div
        style={{
          background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
          borderRadius: 12,
          padding: '32px 24px',
          marginBottom: 24,
          color: '#fff',
        }}
      >
        <h1
          style={{
            fontSize: 28,
            fontWeight: 600,
            margin: 0,
            color: '#fff',
          }}
        >
          欢迎回来，{initialState?.currentUser?.username || '管理员'}
        </h1>
        <p
          style={{
            margin: '8px 0 0 0',
            fontSize: 14,
            opacity: 0.9,
          }}
        >
          今天天气不错，让我们开始工作吧！
        </p>
      </div>

      <Row gutter={[16, 16]}>
        <Col xs={24} sm={12} lg={6}>
          <StatisticCard
            title="用户总数"
            value="1,234"
            icon={<TeamOutlined />}
            color="#1890ff"
          />
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <StatisticCard
            title="菜单数量"
            value="56"
            icon={<MenuOutlined />}
            color="#52c41a"
          />
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <StatisticCard
            title="角色数量"
            value="8"
            icon={<SafetyOutlined />}
            color="#faad14"
          />
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <StatisticCard
            title="今日访问"
            value="3,567"
            icon={<AreaChartOutlined />}
            color="#f5222d"
          />
        </Col>
      </Row>

      <Row gutter={[16, 16]} style={{ marginTop: 24 }}>
        <Col xs={24} lg={16}>
          <Card
            title="快捷入口"
            bordered={false}
            style={{ borderRadius: 8, height: '100%' }}
            styles={{
              body: {
                padding: '16px 24px',
              },
            }}
          >
            <Row gutter={[16, 16]}>
              {[
                { name: '用户管理', path: '/system/user' },
                { name: '角色管理', path: '/system/role' },
                { name: '菜单管理', path: '/system/menu' },
                { name: '部门管理', path: '/system/dept' },
              ].map((item) => (
                <Col xs={12} sm={6} key={item.path}>
                  <a href={item.path}>
                    <div
                      style={{
                        padding: '16px',
                        textAlign: 'center',
                        borderRadius: 8,
                        backgroundColor: token.colorBgContainer,
                        border: `1px solid ${token.colorBorderSecondary}`,
                        transition: 'all 0.3s',
                      }}
                      onMouseEnter={(e) => {
                        e.currentTarget.style.borderColor = token.colorPrimary;
                        e.currentTarget.style.color = token.colorPrimary;
                      }}
                      onMouseLeave={(e) => {
                        e.currentTarget.style.borderColor =
                          token.colorBorderSecondary;
                        e.currentTarget.style.color = token.colorText;
                      }}
                    >
                      {item.name}
                    </div>
                  </a>
                </Col>
              ))}
            </Row>
          </Card>
        </Col>
        <Col xs={24} lg={8}>
          <Card
            title="系统信息"
            bordered={false}
            style={{ borderRadius: 8, height: '100%' }}
            styles={{
              body: {
                padding: '16px 24px',
              },
            }}
          >
            <div
              style={{
                fontSize: 14,
                color: token.colorTextSecondary,
                lineHeight: 2,
              }}
            >
              <div>
                <span style={{ fontWeight: 500 }}>系统版本：</span>v1.0.0
              </div>
              <div>
                <span style={{ fontWeight: 500 }}>数据库状态：</span>正常
              </div>
              <div>
                <span style={{ fontWeight: 500 }}>运行环境：</span>生产环境
              </div>
            </div>
          </Card>
        </Col>
      </Row>
    </PageContainer>
  );
};

export default Welcome;
