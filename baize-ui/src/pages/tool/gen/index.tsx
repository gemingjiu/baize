import { CodeOutlined, DownloadOutlined } from '@ant-design/icons';
import type { ActionType, ProColumns } from '@ant-design/pro-components';
import { PageContainer, ProTable } from '@ant-design/pro-components';
import { Button, Drawer, message, Space, Tabs } from 'antd';
import React, { useRef, useState } from 'react';
import Authorized from '@/components/Authorized';
import { API_VERSION } from '@/constants/api';
import {
  type GenCodeParams,
  type GenTableItem,
  generateCode,
  getGenTables,
} from '@/services/tool/gen';
import { downloadExportFile } from '@/utils/exportExcel';

/**
 * 代码生成页面。
 *
 * 后端 baize-gen（网关路由 /api/v1/gen/** -> lb://baize-gen）：
 *   GET  /gen/tables   列出业务库所有数据表
 *   POST /gen/generate 生成代码，返回「相对文件路径 -> 源码」
 *   POST /gen/download 生成代码并打包成 zip 下载
 *
 * 生成物（一次 9 个文件，风格照抄 post 模块）：
 *   后端 7 个：Entity / DTO / Convert / Mapper / Service / ServiceImpl / Controller
 *   前端 2 个：services/{module}/{business}.ts、pages/{module}/{business}/index.tsx
 */
const Gen: React.FC = () => {
  const actionRef = useRef<ActionType | null>(null);
  const [previewVisible, setPreviewVisible] = useState(false);
  const [previewFiles, setPreviewFiles] = useState<[string, string][]>([]);
  const [previewTitle, setPreviewTitle] = useState('');
  const [generating, setGenerating] = useState(false);
  const [downloading, setDownloading] = useState(false);

  /** 生成并预览：拿到「文件路径 -> 源码」后在抽屉里分文件展示 */
  const handleGenerate = async (record: GenTableItem) => {
    const params: GenCodeParams = { tableName: record.tableName || '' };
    if (!params.tableName) {
      message.warning('表名不能为空');
      return;
    }
    setGenerating(true);
    try {
      const res = await generateCode(params);
      if (res.success && res.data) {
        const files = Object.entries(res.data);
        if (files.length === 0) {
          message.warning('未生成任何文件，请查看后端日志');
          return;
        }
        setPreviewFiles(files);
        setPreviewTitle(`${record.tableName} 生成预览`);
        setPreviewVisible(true);
      }
    } catch {
      message.error('生成失败，请确认网关已放开 /api/v1/gen/** 路由');
    } finally {
      setGenerating(false);
    }
  };

  /** 生成并下载 zip（二进制流，走原生 fetch 而非统一 request） */
  const handleDownload = async (record: GenTableItem) => {
    const tableName = record.tableName || '';
    if (!tableName) {
      message.warning('表名不能为空');
      return;
    }
    setDownloading(true);
    const ok = await downloadExportFile<GenCodeParams>(
      `${API_VERSION}/gen/download`,
      { tableName },
      `${tableName}.zip`,
    );
    setDownloading(false);
    if (ok) {
      message.success('已生成 zip，请查看下载');
    } else {
      message.error('下载失败，请查看后端日志');
    }
  };

  const columns: ProColumns<GenTableItem>[] = [
    {
      title: '表名称',
      dataIndex: 'tableName',
      width: 260,
      copyable: true,
    },
    {
      title: '表描述',
      dataIndex: 'tableComment',
      ellipsis: true,
    },
    {
      title: '表类型',
      dataIndex: 'tableType',
      width: 120,
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
          <Authorized permission="tool:gen:code">
            <Button
              type="link"
              size="small"
              icon={<CodeOutlined />}
              loading={generating}
              onClick={() => handleGenerate(record)}
            >
              生成预览
            </Button>
          </Authorized>
          <Authorized permission="tool:gen:code">
            <Button
              type="link"
              size="small"
              icon={<DownloadOutlined />}
              loading={downloading}
              onClick={() => handleDownload(record)}
            >
              下载
            </Button>
          </Authorized>
        </Space>
      ),
    },
  ];

  return (
    <PageContainer>
      <ProTable<GenTableItem>
        headerTitle="代码生成"
        actionRef={actionRef}
        rowKey="tableName"
        search={false}
        columns={columns}
        pagination={{ pageSize: 10 }}
        request={async (params) => {
          const res = await getGenTables();
          if (res.success) {
            const records = Array.isArray(res.data) ? res.data : [];
            // 后端一次性返回全量表，前端按当前分页切片
            const current = params.current || 1;
            const pageSize = params.pageSize || 10;
            return {
              data: records.slice((current - 1) * pageSize, current * pageSize),
              success: true,
              total: records.length,
            };
          }
          return { data: [], success: false };
        }}
      />

      <Drawer
        title={previewTitle}
        width={900}
        open={previewVisible}
        onClose={() => setPreviewVisible(false)}
        destroyOnHidden
      >
        <Tabs
          defaultActiveKey="0"
          items={previewFiles.map(([path, content], index) => ({
            key: String(index),
            label: path.split('/').pop() || path,
            children: (
              <div>
                <div style={{ color: '#888', fontSize: 12, marginBottom: 8 }}>
                  {path}
                </div>
                <pre
                  style={{
                    maxHeight: 'calc(100vh - 260px)',
                    overflow: 'auto',
                    background: '#f5f5f5',
                    padding: 12,
                    borderRadius: 4,
                    fontSize: 12,
                  }}
                >
                  {content}
                </pre>
              </div>
            ),
          }))}
        />
      </Drawer>
    </PageContainer>
  );
};

export default Gen;
