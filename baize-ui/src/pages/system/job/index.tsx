import {
  CaretRightOutlined,
  DeleteOutlined,
  EditOutlined,
  PauseCircleOutlined,
  PlayCircleOutlined,
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
import { Button, Modal, message, Space, Tag } from 'antd';
import React, { useRef, useState } from 'react';
import Authorized from '@/components/Authorized';
import { useDict } from '@/hooks/useDict';
import {
  createJob,
  deleteJob,
  getJobPage,
  pauseJob,
  resumeJob,
  runJob,
  updateJob,
} from '@/services/system/job';

/**
 * 定时任务管理页面
 */
const Job: React.FC = () => {
  const actionRef = useRef<ActionType | null>(null);
  const { toValueEnum, getDictOptions } = useDict();
  const [formModalVisible, setFormModalVisible] = useState(false);
  const [editingRecord, setEditingRecord] = useState<API.SysJobDto | undefined>();

  const handleEdit = (record: API.SysJobDto) => {
    setEditingRecord(record);
    setFormModalVisible(true);
  };

  const handleDelete = (record: API.SysJobDto) => {
    Modal.confirm({
      title: '确认删除',
      content: `确定要删除定时任务 "${record.jobName}" 吗？`,
      okText: '确认',
      okButtonProps: { danger: true },
      cancelText: '取消',
      onOk: async () => {
        const res = await deleteJob(record.id!);
        if (res.success) {
          message.success('删除成功');
          actionRef.current?.reload();
        }
      },
    });
  };

  const handleRun = (record: API.SysJobDto) => {
    Modal.confirm({
      title: '确认执行',
      content: `确定要立即执行任务 "${record.jobName}" 吗？`,
      okText: '确认',
      cancelText: '取消',
      onOk: async () => {
        const res = await runJob(record.id!);
        if (res.success) {
          message.success('执行成功');
        }
      },
    });
  };

  const handlePause = (record: API.SysJobDto) => {
    Modal.confirm({
      title: '确认暂停',
      content: `确定要暂停任务 "${record.jobName}" 吗？`,
      okText: '确认',
      cancelText: '取消',
      onOk: async () => {
        const res = await pauseJob(record.id!);
        if (res.success) {
          message.success('暂停成功');
          actionRef.current?.reload();
        }
      },
    });
  };

  const handleResume = (record: API.SysJobDto) => {
    Modal.confirm({
      title: '确认恢复',
      content: `确定要恢复任务 "${record.jobName}" 吗？`,
      okText: '确认',
      cancelText: '取消',
      onOk: async () => {
        const res = await resumeJob(record.id!);
        if (res.success) {
          message.success('恢复成功');
          actionRef.current?.reload();
        }
      },
    });
  };

  const handleSubmit = async (values: Partial<API.SysJobDto>) => {
    try {
      let res: API.ApiResult;
      if (editingRecord) {
        res = await updateJob({ ...values, id: editingRecord.id });
      } else {
        res = await createJob(values);
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

  const columns: ProColumns<API.SysJobDto>[] = [
    {
      title: '任务名称',
      dataIndex: 'jobName',
      width: 150,
    },
    {
      title: '任务组',
      dataIndex: 'jobGroup',
      width: 120,
      search: false,
    },
    {
      title: '调用目标',
      dataIndex: 'invokeTarget',
      width: 200,
      search: false,
      ellipsis: true,
    },
    {
      title: 'Cron 表达式',
      dataIndex: 'cronExpression',
      width: 160,
      search: false,
    },
    {
      title: '状态',
      dataIndex: 'status',
      width: 100,
      valueType: 'select',
      valueEnum: toValueEnum('sys_job_status'),
      render: (_, record) => (
        <Tag color={record.status === '0' ? 'success' : 'warning'}>
          {record.status === '0' ? '运行中' : '已暂停'}
        </Tag>
      ),
    },
    {
      title: '并发',
      dataIndex: 'concurrent',
      width: 80,
      search: false,
      render: (_, record) => (
        <Tag color={record.concurrent === '0' ? 'blue' : 'default'}>
          {record.concurrent === '0' ? '允许' : '禁止'}
        </Tag>
      ),
    },
    {
      title: '操作',
      dataIndex: 'option',
      valueType: 'option',
      width: 320,
      fixed: 'right',
      render: (_, record) => (
        <Space size="small">
          <Authorized permission="system:job:edit">
            <Button
              type="link"
              size="small"
              icon={<EditOutlined />}
              onClick={() => handleEdit(record)}
            >
              编辑
            </Button>
          </Authorized>
          <Authorized permission="system:job:edit">
            <Button
              type="link"
              size="small"
              icon={<CaretRightOutlined />}
              onClick={() => handleRun(record)}
            >
              执行
            </Button>
          </Authorized>
          {record.status === '0' ? (
            <Authorized permission="system:job:edit">
              <Button
                type="link"
                size="small"
                icon={<PauseCircleOutlined />}
                onClick={() => handlePause(record)}
              >
                暂停
              </Button>
            </Authorized>
          ) : (
            <Authorized permission="system:job:edit">
              <Button
                type="link"
                size="small"
                icon={<PlayCircleOutlined />}
                onClick={() => handleResume(record)}
              >
                恢复
              </Button>
            </Authorized>
          )}
          <Authorized permission="system:job:remove">
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
      <ProTable<API.SysJobDto>
        headerTitle="定时任务管理"
        actionRef={actionRef}
        rowKey="id"
        search={{
          labelWidth: 100,
        }}
        toolBarRender={() => [
          <Authorized permission="system:job:add" key="add">
            <Button
              type="primary"
              icon={<PlusOutlined />}
              onClick={() => {
                setEditingRecord(undefined);
                setFormModalVisible(true);
              }}
            >
              新增任务
            </Button>
          </Authorized>,
        ]}
        request={async (params) => {
          const { current, pageSize, status } = params;
          // 用分页端点拿 PageResult，total 由后端给出，翻页条才能算对总页数。
          // （旧实现用 GET /list 只有当前页记录，total 被写成 records.length，翻页条恒为 1 页）
          const res = await getJobPage(
            {
              current: current || 1,
              size: pageSize || 10,
            },
            { status: status as string },
          );
          if (res.success) {
            const page = res.data;
            return {
              data: page?.records ?? [],
              success: true,
              total: page?.total ?? 0,
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
        title={editingRecord ? '编辑定时任务' : '新增定时任务'}
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
          name="jobName"
          label="任务名称"
          rules={[{ required: true, message: '请输入任务名称' }]}
          placeholder="请输入任务名称"
        />
        <ProFormText
          name="jobGroup"
          label="任务组名"
          rules={[{ required: true, message: '请输入任务组名' }]}
          placeholder="请输入任务组名"
        />
        <ProFormText
          name="invokeTarget"
          label="调用目标"
          rules={[{ required: true, message: '请输入调用目标' }]}
          placeholder="如：com.gem.baize.system.task.SampleTask#execute"
        />
        <ProFormText
          name="cronExpression"
          label="Cron 表达式"
          rules={[{ required: true, message: '请输入 Cron 表达式' }]}
          placeholder="如：0/5 * * * * ?"
        />
        <ProFormSelect
          name="misfirePolicy"
          label="执行策略"
          options={getDictOptions('sys_job_misfire')}
          initialValue="0"
        />
        <ProFormSelect
          name="concurrent"
          label="是否并发"
          options={getDictOptions('sys_job_concurrent')}
          initialValue="0"
        />
        <ProFormTextArea name="remark" label="备注" placeholder="请输入备注" />
      </ModalForm>
    </PageContainer>
  );
};

export default Job;
