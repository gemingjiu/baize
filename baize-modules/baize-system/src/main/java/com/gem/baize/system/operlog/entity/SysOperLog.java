package com.gem.baize.system.operlog.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gem.baize.common.database.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 操作日志实体
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("sys_oper_log")
public class SysOperLog extends BaseEntity {

    /**
     * 租户ID
     */
    @TableField(value = "tenant_id")
    private String tenantId;

    /**
     * 模块标题
     */
    @TableField(value = "title")
    private String title;

    /**
     * 业务类型（0其它 1新增 2修改 3删除 4查询 5导出 6导入 7强退 8生成代码 9清空数据）
     */
    @TableField(value = "business_type")
    private Integer businessType;

    /**
     * 方法名称
     */
    @TableField(value = "method")
    private String method;

    /**
     * 请求方式
     */
    @TableField(value = "request_method")
    private String requestMethod;

    /**
     * 操作类别（0其它 1后台用户 2手机端用户）
     */
    @TableField(value = "operator_type")
    private Integer operatorType;

    /**
     * 操作人员
     */
    @TableField(value = "user_name")
    private String userName;

    /**
     * 部门名称
     */
    @TableField(value = "dept_name")
    private String deptName;

    /**
     * 请求URL
     */
    @TableField(value = "oper_url")
    private String operUrl;

    /**
     * 主机地址
     */
    @TableField(value = "oper_ip")
    private String operIp;

    /**
     * 操作地点
     */
    @TableField(value = "oper_location")
    private String operLocation;

    /**
     * 请求参数
     */
    @TableField(value = "oper_param")
    private String operParam;

    /**
     * 返回参数
     */
    @TableField(value = "json_result")
    private String jsonResult;

    /**
     * 操作状态（0正常 1异常）
     */
    @TableField(value = "oper_status")
    private Integer operStatus;

    /**
     * 错误消息
     */
    @TableField(value = "error_msg")
    private String errorMsg;

    /**
     * 操作时间
     */
    @TableField(value = "oper_time")
    private LocalDateTime operTime;

    /**
     * 消耗时间（毫秒）
     */
    @TableField(value = "cost_time")
    private Long costTime;
}
