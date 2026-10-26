import { LinkOutlined } from '@ant-design/icons';
import { PageContainer } from '@ant-design/pro-components';
import { Alert, Button, Card, Descriptions, Space, Typography } from 'antd';
import React from 'react';

/**
 * 系统接口（Swagger）页面。
 *
 * 网关已配置 springdoc 聚合：
 *   - Swagger UI: /swagger-ui/index.html
 *   - baize-auth   OpenAPI: /api/v1/auth/v3/api-docs
 *   - baize-system OpenAPI: /api/v1/system/v3/api-docs
 *
 * 注意：前端 dev server 的 proxy 只转发 /api/v1/，因此 /swagger-ui/** 不会
 * 被代理到网关（会落到前端 SPA 的 history fallback）。生产环境由网关直接暴露，
 * 这里用 window.open 打开**经网关的地址**，绕过前端代理。
 */
const SWAGGER_URL = '/swagger-ui/index.html';

const Swagger: React.FC = () => {
  return (
    <PageContainer>
      <Alert
        type="info"
        showIcon
        message="接口文档在新窗口打开"
        description="Swagger UI 由网关直接暴露（未注册到前端 dev proxy 的 /api/v1/ 规则），因此点击下方按钮会打开经网关的地址。"
        style={{ marginBottom: 16 }}
      />
      <Card
        title="系统接口"
        extra={
          <Button
            type="primary"
            icon={<LinkOutlined />}
            onClick={() => window.open(SWAGGER_URL, '_blank')}
          >
            打开 Swagger UI
          </Button>
        }
      >
        <Descriptions column={1} size="small">
          <Descriptions.Item label="Swagger UI">
            {SWAGGER_URL}
          </Descriptions.Item>
          <Descriptions.Item label="baize-auth OpenAPI">
            /api/v1/auth/v3/api-docs
          </Descriptions.Item>
          <Descriptions.Item label="baize-system OpenAPI">
            /api/v1/system/v3/api-docs
          </Descriptions.Item>
        </Descriptions>
        <Space direction="vertical" style={{ marginTop: 16 }}>
          <Alert type="warning" showIcon message="鉴权说明" />
          <Typography.Text type="secondary">
            调试接口时，先在页面顶部 Authorize 填入
            <Typography.Text code>Bearer {'<accessToken>'}</Typography.Text>
            ，token 来自 POST /api/v1/auth/login 的 data.accessToken。
          </Typography.Text>
        </Space>
      </Card>
    </PageContainer>
  );
};

export default Swagger;
