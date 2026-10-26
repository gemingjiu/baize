import { Button, Result, Typography } from 'antd';
import React from 'react';

const { Paragraph, Text } = Typography;

type Props = {
  children?: React.ReactNode;
};

type State = {
  hasError: boolean;
  error?: Error;
  info?: string;
};

/**
 * 全局渲染错误边界。
 *
 * 背景：项目原先没有 ErrorBoundary。任何页面组件在渲染阶段抛异常时，
 * React 会卸载整棵子树，表现为"左侧导航还在、右侧内容区空白"，
 * 控制台以外的用户完全看不到原因，排查成本极高。
 *
 * 这里把异常捕获并显式渲染出来，至少让问题可见、可复制、可上报。
 */
class ErrorBoundary extends React.Component<Props, State> {
  state: State = { hasError: false };

  static getDerivedStateFromError(error: Error): State {
    return { hasError: true, error };
  }

  componentDidCatch(error: Error, errorInfo: React.ErrorInfo) {
    // 保留堆栈到控制台，方便开发者在浏览器里直接看到
    console.error('[ErrorBoundary] 页面渲染异常:', error, errorInfo);
    this.setState({ info: errorInfo?.componentStack || '' });
  }

  handleReload = () => {
    window.location.reload();
  };

  render() {
    if (this.state.hasError) {
      const message = this.state.error?.message || '未知错误';
      return (
        <Result
          status="error"
          title="页面渲染失败"
          subTitle="该页面在渲染过程中抛出了异常，请把下面的信息反馈给开发者。"
          extra={[
            <Button type="primary" key="reload" onClick={this.handleReload}>
              重新加载
            </Button>,
          ]}
        >
          <Paragraph>
            <Text strong>错误信息：</Text>
          </Paragraph>
          <Paragraph>
            <Text code>{message}</Text>
          </Paragraph>
          {this.state.info ? (
            <>
              <Paragraph>
                <Text strong>组件堆栈：</Text>
              </Paragraph>
              <Paragraph>
                <pre style={{ maxHeight: 240, overflow: 'auto', fontSize: 12 }}>
                  {this.state.info}
                </pre>
              </Paragraph>
            </>
          ) : null}
        </Result>
      );
    }
    return this.props.children;
  }
}

export default ErrorBoundary;
